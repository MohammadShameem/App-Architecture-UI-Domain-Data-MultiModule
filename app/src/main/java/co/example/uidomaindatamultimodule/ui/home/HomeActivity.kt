package co.example.uidomaindatamultimodule.ui.home

import android.Manifest
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.work.*
import co.example.bluetoothprinter.BluetoothThreadPrinterHelper
import co.example.bluetoothprinter.STATE
import co.example.common.base.BaseActivity
import co.example.common.constant.AppConstant
import co.example.common.extfun.*
import co.example.connectivity.ConnectivityManager
import co.example.connectivity.NetworkResult
import co.example.dateutil.DateTimeFormat
import co.example.dateutil.DateTimeParser.getCurrentDeviceDateTime
import co.example.entity.room.BusStoppageEntity
import co.example.uidomaindatamultimodule.BuildConfig
import co.example.uidomaindatamultimodule.databinding.ActivityHomeBinding
import co.example.uidomaindatamultimodule.databinding.DialogPrintingTicketBinding
import co.example.uidomaindatamultimodule.ui.entry.EntryActivity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.disposables.CompositeDisposable
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@AndroidEntryPoint
class HomeActivity : BaseActivity<ActivityHomeBinding>() {

    override fun viewBindingLayout(): ActivityHomeBinding = ActivityHomeBinding.inflate(layoutInflater)

    private val viewModel by viewModels<HomeViewModel>()
    private val bluetoothStateReceiver by lazy {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent) {
                when (intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)) {
                    BluetoothAdapter.STATE_OFF -> {
                        launchSystemBluetoothEnableScreen()
                    }
                }
            }
        }
    }

    private lateinit var profileActivityResultLauncher: ActivityResultLauncher<Intent>
    private var bluetoothSettingsResultLauncher: ActivityResultLauncher<Intent>? = null
    private val disposable = CompositeDisposable()
    private lateinit var adapter: BusStoppageAdapter
    private var unSyncedTicketCount = 0
    private var networkManagerFlag = false
    private var ticketPrintingDialog: AlertDialog? = null
    private val connectivityManager by lazy { ConnectivityManager(applicationContext as Application)}
    @Inject lateinit var bluetoothPrinterHelper: BluetoothThreadPrinterHelper

    @Inject lateinit var sharedPrefHelper: SharedPrefHelper
    private var showConfirmationActivated = false
    private val bluetoothConnectionPermissionCode = 9


    override fun initializeView(savedInstanceState: Bundle?) {
        registerActivityResult()
        // initialize printing ticket alert dialog
        ticketPrintingDialog = showAlertDialogWithViewWithoutBtn(DialogPrintingTicketBinding.inflate(layoutInflater).root,false)
        toolBarWithoutBackBtn(binding.toolBarL.toolbar, sharedPrefHelper.getString(SpKey.companyName))

        setupAdapter()
        if (sharedPrefHelper.getString(SpKey.printerType)==AppConstant.bluetooth) setUpBluetoothPrinter()
        autoSyncSoldTicket()

        viewModel.configDataState bindTo::bindUiState
        viewModel.homeUiEvent bindTo::bindUiEvent
        viewModel.soldTicketCountData bindTo::bindSoldTicketCountUiState
        viewModel.soldTicketAmountData bindTo::bindSoldTicketFareUiState

    }

    override fun onResume() {
        super.onResume()
        // register broadcast receiver for observing bluetooth sate ON/OFF
        if (sharedPrefHelper.getString(SpKey.printerType)==AppConstant.bluetooth){
            registerReceiver(bluetoothStateReceiver,  IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
        }

        showConfirmationActivated = sharedPrefHelper.getBoolean(SpKey.isTicketConfirmationDialogEnable)
        // make serial 0 after 1 days
        if (sharedPrefHelper.getString(SpKey.soldTicketSerialCurrentDate) != getCurrentDeviceDateTime(DateTimeFormat.outputDMY)){
            sharedPrefHelper.putString(SpKey.soldTicketSerialCurrentDate, getCurrentDeviceDateTime(DateTimeFormat.outputDMY))
            sharedPrefHelper.putInt(SpKey.soldTicketSerial,0)
        }
        //check automatic timezone enable or not if not enable finish the app
        if(checkAutomaticTimeZoneIsEnable(application)){
            showToastMessage(getString(R.string.error_message_time_zone))
            finish()
        }
    }

    private fun bindSoldTicketCountUiState(state: HomeUiState<HomeUiState.SoldTicketCount>){
        if (state is HomeUiState.SoldTicketCount){
            binding.totalTicketTv.text = state.count.toString()
            unSyncedTicketCount = state.count
        }
    }

    private fun bindSoldTicketFareUiState(state: HomeUiState<HomeUiState.SoldTicketFare>){
        if (state is HomeUiState.SoldTicketFare){
            binding.totalAmountTv.text = getString(R.string.fare,state.fare)
        }
    }

    private fun bindUiState(state: HomeUiState<HomeUiState.ConfigData>){
        if (state is HomeUiState.ConfigData) {
            binding.fareCategoryCl.isVisible = !state.normalFareStatus
            binding.sellCategoryCl.isVisible = state.sellCategoryStatus
            adapter.submitList(state.stoppageList)
            binding.studentRb.isVisible = state.studentFareStatus
            binding.specialRb.isVisible = state.specialFareStatus
        }
    }

    private fun bindUiEvent(event: HomeUiEvent<Any>){
        when(event){
            is HomeUiEvent.ShowToast -> showMessage(event.message)
            is HomeUiEvent.ShowTicketPrintingDialog -> showTicketPrintingDialog()
            is HomeUiEvent.DismissTicketPrintingDialog -> {
                adapter.setClickable(true)
                ticketPrintingDialog?.dismiss()
            }
            is HomeUiEvent.TicketFormatEmpty -> {
                adapter.setClickable(true)
                showMessage(event.message)
            }
        }
    }



    private fun setupAdapter(){
        adapter = BusStoppageAdapter { busStoppageEntity ->
            adapter.setClickable(false)
            when {
                binding.normalRb.isChecked ||binding.studentRb.isChecked -> {
                    //action for multiple ticket dialog
                    if (binding.multipleRb.isChecked) printMultipleTicket(busStoppageEntity)
                    else if (showConfirmationActivated) showConfirmationDialog(busStoppageEntity){
                        viewModel.action(HomeUiAction.PrintAndInsertTicket(busStoppageEntity))
                    }
                    //action for normal and single ticket
                    else viewModel.action(HomeUiAction.PrintAndInsertTicket(busStoppageEntity))
                }
                binding.specialRb.isChecked -> specialFareCalculationAndInsertData(busStoppageEntity)
            }
        }
        adapter.setCardDataType(sharedPrefHelper.getString(SpKey.cardData))
        setUpGridRecyclerView(binding.stoppageListRv,adapter,3)

        // fare category radio ground according to it's item changes adapter will be changed respectively
        binding.fareCategoriesRg.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.studentRb -> adapter.apply {
                    setStudentFare(true)
                    notifyDataSetChanged()
                }
                R.id.normalRb, R.id.specialRb-> adapter.apply {
                    setStudentFare(false)
                    notifyDataSetChanged()
                }
            }
        }
    }

    private fun printMultipleTicket(busStoppageEntity: BusStoppageEntity) {
        MultipleTicketDialogFragment(
            numberOfTicketCallBack = { ticketQuantity ->
                if (showConfirmationActivated){
                    showConfirmationDialog(stoppageEntity = busStoppageEntity, ticketQuantity = ticketQuantity){
                        viewModel.action(HomeUiAction.PrintAndInsertTicket(busStoppageEntity, ticketQuantity = ticketQuantity))
                    }
                }
                else viewModel.action(HomeUiAction.PrintAndInsertTicket(busStoppageEntity, ticketQuantity = ticketQuantity))
            },
            cancelCallback = { adapter.setClickable(true) }
        ).show(supportFragmentManager,AppConstant.multipleTicketPrintDialog)
    }

    private fun specialFareCalculationAndInsertData(busStoppageEntity: BusStoppageEntity) {
        SpecialFareDialogFragment(
            specialFareCallBack = { newFare, phoneNo ->
                if (showConfirmationActivated){
                    showConfirmationDialog(stoppageEntity = busStoppageEntity.copy(fare = newFare)){
                        viewModel.action(HomeUiAction.PrintAndInsertTicket(busStoppageEntity.copy(fare = newFare ), phone = phoneNo))
                    }
                }
                else viewModel.action(HomeUiAction.PrintAndInsertTicket(busStoppageEntity.copy(fare = newFare ), phone = phoneNo))
            },
            cancelCallback = { adapter.setClickable(true) }
        ).show(supportFragmentManager,AppConstant.specialFareDialog)
    }


    private fun showConfirmationDialog(stoppageEntity: BusStoppageEntity,ticketQuantity : Int = 1, printTicket: () -> Unit) {
        val fare =  if(stoppageEntity.isStudentFareType) stoppageEntity.studentFare * ticketQuantity else stoppageEntity.fare * ticketQuantity
        val message = "From: ${sharedPrefHelper.getString(SpKey.selectedBusStoppageName)}\nTo: ${stoppageEntity.name}\nFare: $fare"
        showAlertDialog(positiveBtn = getString(co.example.assets.R.string.yes_text), negativeBtn = getString(
            co.example.assets.R.string.no_text
        ), title = getString(co.example.assets.R.string.confirm),
            message, false, positiveBtnCallback = printTicket,
            negativeBtnCallback = { adapter.setClickable(true) })
    }


    private fun setUpBluetoothPrinter(){
        checkBluetoothConnectionPermission()

        bluetoothPrinterHelper.connectionState.execute {
            if (it == STATE.DISCONNECTED) {
                // show error dialog
                showOnlyPositiveButtonDialog(
                    buttonText = getString(R.string.btn_reconnect),
                    message = getString(R.string.msg_please_connect_printer),
                    cancelable = false,
                    positiveBtnCallback = {
                        // check whole printer settings
                        setUpBluetoothPrinter()
                    }
                )
            }
        }

        //check printer is bluetooth
        //check bluetooth adapter is not available
        if (!bluetoothPrinterHelper.checkBluetoothAdapterIsAvailable()){
            showOnlyPositiveButtonDialog(
                buttonText = getString(R.string.btn_reconnect),
                message = getString(R.string.msg_your_device_does_not_support_bluetooth),
                cancelable = false,
                positiveBtnCallback = {
                    setUpBluetoothPrinter()
                }
            )
            return
        }

        // bluetooth adapter is okay. now check bluetooth not enable
        if (!bluetoothPrinterHelper.isBluetoothEnable()){
            launchSystemBluetoothEnableScreen()
            return
        }

        //all bluetooth settings okay now pair the device
        bluetoothPrinterHelper.pairDevices()

    }

    private fun registerActivityResult() {
        // Profile Activity Result Launcher
        profileActivityResultLauncher  = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if(it.resultCode == RESULT_OK){
                startActivity(Intent(this, EntryActivity::class.java))
                finish()
            }
        }
        if (sharedPrefHelper.getString(SpKey.printerType)==AppConstant.bluetooth){
            // Bluetooth Settings Result Launcher
            bluetoothSettingsResultLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    //all bluetooth settings okay now pair the device
                    bluetoothPrinterHelper.pairDevices()
                }else {
                    // if deny.. lunch bluetooth settings again
                    launchSystemBluetoothEnableScreen()
                }
            }
        }

    }

    /**
     * Check bluetooth connection permission
     * */
    private fun checkBluetoothConnectionPermission(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED ){
            launchSystemBluetoothEnableScreen()
        }else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.BLUETOOTH_CONNECT), bluetoothConnectionPermissionCode)
            }
        }
    }

    private fun launchSystemBluetoothEnableScreen(){
        if (bluetoothSettingsResultLauncher!=null)
            bluetoothSettingsResultLauncher!!.launch(Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE))
    }

    /**
     * Checking the external storage writing permission
     * if the permission is granted then data will be save to local file
     * */
    override fun onRequestPermissionsResult(requestCode: Int, vararg permissions: String?, grantResults: IntArray) {
        if (requestCode == bluetoothConnectionPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (bluetoothPrinterHelper.isBluetoothEnable()){
                    bluetoothPrinterHelper.pairDevices()
                }
                else launchSystemBluetoothEnableScreen()
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }



    override fun onDestroy() {
        super.onDestroy()
        WorkManager.getInstance(applicationContext).cancelAllWorkByTag(AppConstant.autoSyncWorkerTag)
        WorkManager.getInstance(applicationContext).cancelAllWork()
        disposable.clear()
        if(sharedPrefHelper.getString(SpKey.printerType) == AppConstant.bluetooth) {
            try { unregisterReceiver(bluetoothStateReceiver) }
            catch (e:IllegalArgumentException){ e.printStackTrace() }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        disposable.clear()
        finish()

    }

    private fun checkAutomaticTimeZoneIsEnable(application: Application):Boolean =
        Settings.Global.getInt(application.contentResolver, Settings.Global.AUTO_TIME) == 0
                || Settings.Global.getInt(application.contentResolver, Settings.Global.AUTO_TIME_ZONE) == 0

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_home, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.actionSettings ->  navigateToSettingsActivity()
            R.id.actionDashBoard -> navigateToDashboardActivity()
            R.id.actionProfile -> navigateProfileActivity(
                profileActivityResultLauncher,
                sharedPrefHelper.getString(SpKey.companyName),
                BuildConfig.VERSION_NAME,
                getBuildType()
            )
            R.id.actionPrintReport -> {
                if(unSyncedTicketCount <= 0){
                    showAlertDialog(positiveBtn = getString(co.example.assets.R.string.yes_text),
                        negativeBtn = getString(co.example.assets.R.string.no_text),
                        negativeBtnCallback = {},
                        positiveBtnCallback = {viewModel.action.invoke(HomeUiAction.ReportPrintAction)},
                        message = getString(R.string.msg_want_to_print_report),
                        title = getString(R.string.title_report_print_alert), cancelable = false)
                } else showToastMessage(getString(R.string.msg_sync_sold_ticket))
            }
        }
        return super.onOptionsItemSelected(item)
    }


    private fun autoSyncSoldTicket(){
        startAutoSyncToServer()
        connectivityManager.registerCallback()
        connectivityManager.result.observe(this) { networkResult->
            if (networkResult == NetworkResult.CONNECTED){
                if (networkManagerFlag) startAutoSyncToServer()
                networkManagerFlag = true
            }
        }
    }

    private fun startAutoSyncToServer(){
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()
        val request = OneTimeWorkRequest.Builder(AutoSyncWorker::class.java)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(applicationContext).enqueueUniqueWork(AppConstant.autoSyncWorkerTag,
            ExistingWorkPolicy.REPLACE,request)
    }


    private fun showTicketPrintingDialog(){
        ticketPrintingDialog?.show()
        disposable.addAll(
            Observable.timer(700, TimeUnit.MILLISECONDS)
                .subscribe {
                    adapter.setClickable(true)
                    ticketPrintingDialog?.dismiss()
                }
        )
    }
}