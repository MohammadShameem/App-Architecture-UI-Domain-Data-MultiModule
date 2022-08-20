package co.example.uidomaindatamultimodule.ui.home


import android.app.Application
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import co.example.common.base.BaseViewModel
import co.example.common.constant.AppConstant
import co.example.domain.base.ApiResult
import co.example.domain.usecase.local.dashboard.GetTotalUnSyncedSoldTicketAmountUseCase
import co.example.domain.usecase.local.dashboard.GetTotalUnSyncedSoldTicketCountUseCase
import co.example.domain.usecase.local.home.GetAllTicketFormatUseCase
import co.example.domain.usecase.local.home.GetStoppageEntityListUseCase
import co.example.domain.usecase.local.home.InsertSoldTicketUseCase
import co.example.domain.usecase.remote.home.ReportPrintApiUseCase
import co.example.entity.home.AmountWiseDistributionEntity
import co.example.entity.room.BusStoppageEntity
import co.example.entity.room.SoldTicketEntity
import co.example.entity.room.TicketFormatEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import co.example.sunmibanglaprinter.SunmiPrintHelper
import co.example.sunmibanglaprinter.SunmiTicketPrintHelper
import co.example.bluetoothprinter.BluetoothTicketPrintHelper
import com.squareup.picasso.Picasso
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject


sealed class HomeUiState<out R>{
    data class SoldTicketCount(val count: Int = 0):HomeUiState<SoldTicketCount>()
    data class SoldTicketFare(val fare: Int = 0):HomeUiState<SoldTicketFare>()
    data class ConfigData(
        val stoppageList: List<BusStoppageEntity> = listOf(),
        val normalFareStatus: Boolean = false,
        val studentFareStatus: Boolean = false,
        val specialFareStatus: Boolean = false,
        val multipleTicketStatus: Boolean = false,
        val sellCategoryStatus: Boolean = false,
    ):HomeUiState<ConfigData>()
}

sealed class HomeUiEvent<out R>{
    data class ShowToast(val message: String ) : HomeUiEvent<ShowToast>()
    data class TicketFormatEmpty(val message: String ) : HomeUiEvent<TicketFormatEmpty>()
    object ShowTicketPrintingDialog : HomeUiEvent<ShowTicketPrintingDialog>()
    object DismissTicketPrintingDialog : HomeUiEvent<DismissTicketPrintingDialog>()
}

sealed class HomeUiAction {
    data class PrintAndInsertTicket(
        val busStoppageEntity: BusStoppageEntity,
        val ticketQuantity: Int = 1,
        val phone: String = ""
    ) : HomeUiAction()
    object ReportPrintAction : HomeUiAction()
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val unSyncedSoldTicketCountUseCase: GetTotalUnSyncedSoldTicketCountUseCase,
    private val unSyncedSoldTicketAmountUseCase: GetTotalUnSyncedSoldTicketAmountUseCase,
    private val stoppageListUseCase: GetStoppageEntityListUseCase,
    private val insertSoldTicketUseCase: InsertSoldTicketUseCase,
    private val reportPrintApiUseCase: ReportPrintApiUseCase,
    private val sharedPrefHelper: SharedPrefHelper,
    private val getAllTicketFormatUseCase: GetAllTicketFormatUseCase,
    private val application: Application,
    private val sunmiTicketPrintHelper: SunmiTicketPrintHelper,
    private val bluetoothTicketPrintHelper: BluetoothTicketPrintHelper

) : BaseViewModel(){

    private val _configDataState = MutableStateFlow<HomeUiState<HomeUiState.ConfigData>>(HomeUiState.ConfigData())
    val configDataState = _configDataState

    private val _soldTicketCountData = MutableStateFlow<HomeUiState<HomeUiState.SoldTicketCount>>(HomeUiState.SoldTicketCount(0))
    val soldTicketCountData = _soldTicketCountData

    private val _soldTicketAmountData = MutableStateFlow<HomeUiState<HomeUiState.SoldTicketFare>>(HomeUiState.SoldTicketFare(0))
    val soldTicketAmountData = _soldTicketAmountData



    private val _homeUiEvent = Channel<HomeUiEvent<Any>>()
    val homeUiEvent  get() = _homeUiEvent.receiveAsFlow()

    val action: (HomeUiAction) -> Unit

    private var ticketTicketFormatList = listOf<TicketFormatEntity>()
    private val soldTicketList = mutableListOf<SoldTicketEntity>()
    private var bitmapList = mutableListOf<Bitmap>()
    private var ticketSerial = 100

    init {
        action = {
            when (it) {
                is HomeUiAction.ReportPrintAction -> getReportPrintData()
                is HomeUiAction.PrintAndInsertTicket -> {
                    insertSoldTicket(
                        busStoppageEntity = it.busStoppageEntity,
                        ticketQuantity = it.ticketQuantity,
                        passengerNumber = it.phone
                    )
                }
            }
        }
        loadInitialData()
        getAllTicketFormatEntityList()
    }

    private fun loadInitialData() {
        val hasStudentFareFeature = sharedPrefHelper.getBoolean(SpKey.hasStudentFare)
        val hasSpecialFareFeature = sharedPrefHelper.getBoolean(SpKey.hasSpecialFare)
        val hasMultipleFareFeature = sharedPrefHelper.getBoolean(SpKey.hasMultipleTicket)
        val normalFare = !hasStudentFareFeature && !hasSpecialFareFeature

        execute {
            unSyncedSoldTicketCountUseCase.execute().collect{ count ->
                _soldTicketCountData.value = HomeUiState.SoldTicketCount(count)

            }
        }
        execute{
            unSyncedSoldTicketAmountUseCase.execute().collect{ fare ->
                _soldTicketAmountData.value= HomeUiState.SoldTicketFare(fare)
            }
        }
        execute{
            stoppageListUseCase.execute().collect { stoppageList ->
                _configDataState.value = HomeUiState.ConfigData(
                     stoppageList = stoppageList,
                     normalFareStatus = normalFare,
                     studentFareStatus = hasStudentFareFeature,
                     specialFareStatus = hasSpecialFareFeature,
                     multipleTicketStatus = hasMultipleFareFeature,
                     sellCategoryStatus = hasMultipleFareFeature
                )
            }
        }

    }

    private fun insertSoldTicket(busStoppageEntity: BusStoppageEntity, ticketQuantity: Int = 1,passengerNumber:String){
        if(ticketTicketFormatList.isNotEmpty()){
            setUiEvent(HomeUiEvent.ShowTicketPrintingDialog)
            val totalFare = if(busStoppageEntity.isStudentFareType) ticketQuantity * busStoppageEntity.studentFare
            else ticketQuantity * busStoppageEntity.fare

            soldTicketList.clear()
            ticketSerial = sharedPrefHelper.getInt(SpKey.soldTicketSerial)
            val fromStoppage = sharedPrefHelper.getString(SpKey.selectedBusStoppageName)
            repeat(ticketQuantity) {
                soldTicketList.add(
                    SoldTicketEntity(
                        serial = ++ticketSerial,
                        from_stoppage = fromStoppage,
                        to_stoppage = busStoppageEntity.name,
                        amount = if (busStoppageEntity.isStudentFareType) busStoppageEntity.studentFare
                        else busStoppageEntity.fare
                    )
                )
            }

            //print ticket by sunmi
            if(sharedPrefHelper.getString(SpKey.printerType)==AppConstant.sunmi){
                sunmiTicketPrintHelper.printTicket(
                    busStoppageEntity = busStoppageEntity,
                    ticketFormatList= ticketTicketFormatList,
                    ticketFare = totalFare,
                    ticketQuantity= ticketQuantity,
                    passengerPhone = passengerNumber,
                    ticketSerial = ticketSerial,
                    bitmapList = bitmapList,
                    ticketPrintErrorCallback = {
                        setUiEvent(HomeUiEvent.ShowToast( "Print device is offline"))
                        setUiEvent(HomeUiEvent.DismissTicketPrintingDialog)
                    },
                    ticketPrintSuccessCallback = {
                        sharedPrefHelper.putInt(SpKey.soldTicketSerial,ticketSerial)
                        execute {
                            insertSoldTicketUseCase.execute(InsertSoldTicketUseCase.Params(soldTicketList))
                        }
                    }
                )
            }
            else {
                bluetoothTicketPrintHelper.printTicket(
                    busStoppageEntity = busStoppageEntity,
                    ticketFormatList= ticketTicketFormatList,
                    ticketFare = totalFare,
                    ticketSerial = ticketSerial,
                    ticketQuantity = ticketQuantity,
                    passengerNumber = passengerNumber,
                    bitmapList = bitmapList,
                    ticketPrintErrorCallback = {
                        setUiEvent(HomeUiEvent.ShowToast( "Print device is offline"))
                        setUiEvent(HomeUiEvent.DismissTicketPrintingDialog)
                    },
                    ticketPrintSuccessCallback = {
                        sharedPrefHelper.putInt(SpKey.soldTicketSerial,ticketSerial)
                        execute {
                            insertSoldTicketUseCase.execute(InsertSoldTicketUseCase.Params(soldTicketList))
                        }
                    }
                )
            }
        }else setUiEvent(HomeUiEvent.TicketFormatEmpty("Ticket Format Not Found"))
    }

    private fun setUiEvent(event: HomeUiEvent<Any>){
        execute {
            _homeUiEvent.send(event)
        }
    }


    private fun getAllTicketFormatEntityList(){
        execute {
            getAllTicketFormatUseCase.execute().collect{
                if(it.isNotEmpty()) {
                    ticketTicketFormatList  = it
                    generateImageBitmapListFromUrl()
                }
            }
        }
    }

    private fun generateImageBitmapListFromUrl() {
        if (bitmapList.isNotEmpty()) bitmapList.clear()
        ticketTicketFormatList.forEach {
            if (it.type == AppConstant.image) {
                if (it.image_url.isNotEmpty()) {
                    Picasso.get().load(it.image_url).into(object : com.squareup.picasso.Target {
                        override fun onBitmapLoaded(bitmap: Bitmap?, from: Picasso.LoadedFrom?) {
                            if (bitmap != null) bitmapList.add(bitmap)
                        }
                        override fun onPrepareLoad(placeHolderDrawable: Drawable?) {}
                        override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {}
                    })
                }
            }
        }
    }



    private fun getReportPrintData(){
        execute {
            reportPrintApiUseCase.execute().collect{
                when(it){
                    is ApiResult.Success -> {
                        if (sharedPrefHelper.getString(SpKey.printerType)==AppConstant.sunmi){
                            if (SunmiPrintHelper.instance.showPrinterStatus(application)) {
                                printReportBySunmi(
                                    ticketCount = it.data.synced_today.paid_count,
                                    ticketAmount = it.data.synced_today.paid_amount,
                                    amountWiseDistribution = it.data.amount_wise_distributions)
                            }
                        }else{
                            printReportByBluetooth(
                                ticketCount = it.data.synced_today.paid_count,
                                ticketAmount = it.data.synced_today.paid_amount,
                                amountWiseDistribution = it.data.amount_wise_distributions)
                        }
                    }
                    is ApiResult.Error -> {_homeUiEvent.send(HomeUiEvent.ShowToast(it.message))}
                    is ApiResult.Loading -> {}
                }
            }
        }

    }


    private fun printReportBySunmi(
        ticketCount:Int,
        ticketAmount:Int,
        amountWiseDistribution: List<AmountWiseDistributionEntity>)
    {
        sunmiTicketPrintHelper.printReport(
            ticketCount = ticketCount,
            ticketAmount = ticketAmount,
            ticketFormatList = ticketTicketFormatList,
            amountWiseDistribution = amountWiseDistribution
        )
    }


    private fun printReportByBluetooth(
        ticketCount:Int,
        ticketAmount:Int,
        amountWiseDistribution: List<AmountWiseDistributionEntity>
    ){
        bluetoothTicketPrintHelper.printReport(
            ticketCount = ticketCount,
            ticketAmount = ticketAmount,
            ticketFormatList = ticketTicketFormatList,
            amountWiseDistribution = amountWiseDistribution
        )
    }
}
