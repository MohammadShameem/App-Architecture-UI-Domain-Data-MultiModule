package co.example.dashboard

import android.os.Bundle
import androidx.activity.viewModels
import co.example.common.base.BaseActivity
import co.example.common.extfun.toolBarSubTitleWithBackBtn
import co.example.dashboard.databinding.ActivityDashboardBinding
import co.example.dateutil.DateTimeParser
import co.example.dateutil.DateTimeFormat
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardActivity : BaseActivity<ActivityDashboardBinding>() {

    override fun viewBindingLayout(): ActivityDashboardBinding = ActivityDashboardBinding.inflate(layoutInflater)

    private val viewModel by viewModels<DashboardViewModel>()

    override fun initializeView(savedInstanceState: Bundle?) {
        val todayFormattedDate = DateTimeParser.getCurrentDeviceDateTime(DateTimeFormat.outputDMY)
        toolBarSubTitleWithBackBtn(binding.toolbar,getString(R.string.dashboard_title),todayFormattedDate)

        dashboardUiStateObserver()
        dashboardUiStateEventObserver()
    }


    private fun dashboardUiStateObserver() {
        viewModel.uiState.execute {
            when(it){
                is DashboardUiState.Loading -> showProgressBar(it.isLoading,binding.progressBar)
                is DashboardUiState.Success -> {
                    binding.totalSyncSoldTicketTv.text = getString(R.string.ticket_count,it.syncedTicketData.totalTicketCount)
                    binding.totalSyncTicketFareTv.text = getString(R.string.ticket_fare,it.syncedTicketData.totalTicketAmount)
                    binding.totalCollectableAmountTv.text = getString(R.string.ticket_fare,it.totalCollectableAmount)
                    binding.root.hideNetworkErrorLayout()
                }
                is DashboardUiState.NetworkError -> {
                    binding.root.showSimpleNetworkErrorLayout(
                        message = it.message,
                        refreshCallback = {
                            viewModel.action(DashboardUiAction.RetryAction)
                        }
                    )
                }
                is DashboardUiState.UnSyncedTicketCount -> {
                    binding.totalUnsyncSoldTicketTv.text = getString(R.string.ticket_count,it.totalUnSyncedTicketCount)
                }
                is DashboardUiState.UnSyncedTicketFare -> {
                    binding.totalUnsyncTicketFareTv.text = getString(R.string.ticket_fare,it.totalUnSyncedTicketFare)
                }
            }
        }
    }

    private fun dashboardUiStateEventObserver(){
         viewModel.uiEvent.execute {
             if(it is DashBoardUiEvent.ShowToastMessage){
                 showToastMessage(it.message)
             }
         }
    }
}
