package co.example.dashboard

import co.example.common.base.BaseViewModel
import co.example.domain.base.ApiResult
import co.example.domain.usecase.local.dashboard.GetTotalUnSyncedSoldTicketAmountUseCase
import co.example.domain.usecase.local.dashboard.GetTotalUnSyncedSoldTicketCountUseCase
import co.example.domain.usecase.remote.dashboard.SyncedTicketDataApiUseCase
import co.example.entity.dashboard.TodaySyncedTicketEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject


sealed class DashboardUiState<out R>{
    data class Loading(val isLoading: Boolean=false): DashboardUiState<Loading>()
    data class Success(val syncedTicketData: TodaySyncedTicketEntity, val totalCollectableAmount: Int): DashboardUiState<Success>()
    data class NetworkError(val message: String): DashboardUiState<NetworkError>()
    data class UnSyncedTicketCount(val totalUnSyncedTicketCount: Int) : DashboardUiState<UnSyncedTicketCount>()
    data class UnSyncedTicketFare(val totalUnSyncedTicketFare: Int) : DashboardUiState<UnSyncedTicketFare>()
}

sealed class DashBoardUiEvent{
    data class ShowToastMessage(val message: String):DashBoardUiEvent()
}

sealed class DashboardUiAction{
    object RetryAction: DashboardUiAction()
}


@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val syncedTicketDataApiUseCase: SyncedTicketDataApiUseCase,
    private val totalUnSyncedSoldTicketCountUseCase: GetTotalUnSyncedSoldTicketCountUseCase,
    private val totalUnSyncedSoldTicketAmountUseCase: GetTotalUnSyncedSoldTicketAmountUseCase
):BaseViewModel(){
    private val _uiState = MutableStateFlow<DashboardUiState<*>>(DashboardUiState.Loading())
    val uiState = _uiState.asStateFlow()
    var totalCollectableAmount = 0

    private val _uiEvent = Channel<DashBoardUiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    val action: (DashboardUiAction) -> Unit

    init {
        action = {
            when(it){
                is DashboardUiAction.RetryAction -> todaySyncedTicketData()
            }
        }
        getTotalUnSyncedTicketFare()
        getTotalUnSyncedTicketCount()
        todaySyncedTicketData()
    }

    private fun getTotalUnSyncedTicketCount(){
        viewModelScopeExecute {
            totalUnSyncedSoldTicketCountUseCase.execute().collect{ unSyncedTicketCount ->
                _uiState.value=(DashboardUiState.UnSyncedTicketCount(unSyncedTicketCount))
            }
        }
    }

    private fun getTotalUnSyncedTicketFare(){
        viewModelScopeExecute {
            totalUnSyncedSoldTicketAmountUseCase.execute().collect{ unSyncedTicketFare ->
                totalCollectableAmount+= unSyncedTicketFare
                _uiState.value=(DashboardUiState.UnSyncedTicketFare(unSyncedTicketFare))
            }
        }
    }

    private fun todaySyncedTicketData(){
        viewModelScopeExecute {
             syncedTicketDataApiUseCase.execute().collect{ response ->
                when(response){
                    is ApiResult.Success -> {
                        totalCollectableAmount+= response.data.totalTicketAmount
                        _uiState.value=(DashboardUiState.Success(syncedTicketData = response.data,
                            totalCollectableAmount))
                        _uiEvent.send(DashBoardUiEvent.ShowToastMessage(response.data.message))
                    }
                    is ApiResult.Error -> _uiState.value=(DashboardUiState.NetworkError(message = response.message))
                    is ApiResult.Loading ->  _uiState.value=(DashboardUiState.Loading(response.loading))
                }
             }
        }
    }

}



