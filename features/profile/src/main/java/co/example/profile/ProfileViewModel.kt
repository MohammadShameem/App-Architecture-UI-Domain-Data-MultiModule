package co.example.profile

import co.example.common.base.BaseViewModel
import co.example.domain.base.ApiResult
import co.example.domain.usecase.local.offlinedatasync.CacheOfflineDataUseCase
import co.example.domain.usecase.local.offlinedatasync.ClearCacheUseCase
import co.example.domain.usecase.local.dashboard.GetTotalUnSyncedSoldTicketCountUseCase
import co.example.domain.usecase.remote.credential.LogoutApiUseCase
import co.example.domain.usecase.remote.profile.OfflineDataSyncApiUseCase
import co.example.domain.usecase.remote.profile.ProfileApiUseCase
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val logoutApiUseCase: LogoutApiUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val sharedPrefHelper: SharedPrefHelper,
    private val totalUnSyncedSoldTicketCountUseCase: GetTotalUnSyncedSoldTicketCountUseCase,
    private val offlineDataSyncApiUseCase: OfflineDataSyncApiUseCase,
    private val profileApiUseCase: ProfileApiUseCase,
    private val cacheOfflineDataUseCase: CacheOfflineDataUseCase,
) : BaseViewModel() {

    private val _uiState = MutableStateFlow(
        ProfileUiState(
            counterManName = sharedPrefHelper.getString(SpKey.userName),
            phoneNumber = sharedPrefHelper.getString(SpKey.phone),
            stoppage = sharedPrefHelper.getString(SpKey.selectedBusStoppageName),
            appInfo = sharedPrefHelper.getString(SpKey.companyName),
        )
    )
    val uiState = _uiState.asStateFlow()

    private val _uiEvent = Channel<ProfileUiEvent>()
    val uiEvent get() = _uiEvent.receiveAsFlow()

    val action: (ProfileUiAction) -> Unit

    init {
        action = {
            when (it) {
                is ProfileUiAction.LogoutAction -> logout()
                is ProfileUiAction.ResetConfigAction -> resetConfig()
            }
        }
    }

    private fun logout() {
        launchJob {
            totalUnSyncedSoldTicketCountUseCase.execute().collect { unSyncedTicketCount ->
                if (unSyncedTicketCount > 0)
                    _uiEvent.send(ProfileUiEvent.ShowToast("Please sync all sold ticket first..."))
                else {
                    logoutApiUseCase.execute().collect { result ->
                        when (result) {
                            is ApiResult.Success -> {
                                clearCacheUseCase.execute()
                                sharedPrefHelper.clearAllCache()
                                _uiEvent.send(ProfileUiEvent.NavigateToLogin(result.data.message))
                            }
                            is ApiResult.Error -> _uiEvent.send(ProfileUiEvent.ShowToast(result.message))
                            is ApiResult.Loading -> _uiEvent.send(ProfileUiEvent.ToggleProgressbar(result.loading))
                        }
                    }
                }
            }
        }
    }

    private fun resetConfig() {
        launchJob {
            totalUnSyncedSoldTicketCountUseCase.execute().collect { unSyncedTicketCount ->
                if (unSyncedTicketCount > 0)
                    _uiEvent.send(ProfileUiEvent.ShowToast("Please sync all sold tickets first..."))
                else {
                    _uiEvent.send(ProfileUiEvent.ToggleProgressbar(true))
                    fetchProfile()
                    _uiEvent.send(ProfileUiEvent.ToggleProgressbar(false))
                }
            }
        }
    }



    private suspend fun fetchProfile() {
        profileApiUseCase.execute().collect { result ->
            when (result) {
                is ApiResult.Success -> syncOfflineData()
                is ApiResult.Error -> _uiEvent.send(ProfileUiEvent.ShowToast(result.message))
                is ApiResult.Loading -> {}
            }
        }
    }

    private suspend fun syncOfflineData() {
        offlineDataSyncApiUseCase.execute().collect { result ->
            when (result) {
                is ApiResult.Success -> {
                    result.data.let {
                        cacheOfflineDataUseCase.execute(
                            CacheOfflineDataUseCase.Params(
                                it.stoppage_list,
                                it.fare_modality,
                                it.vehicle_list,
                                it.company.ticketFormatEntityList
                            )
                        )
                    }
                    sharedPrefHelper.putBool(SpKey.offlineDataSyncStatus, true)
                    _uiEvent.send(ProfileUiEvent.NavigateToEntryActivity)
                }
                is ApiResult.Error -> _uiEvent.send(ProfileUiEvent.ShowToast(result.message))
                is ApiResult.Loading -> {}
            }
        }
    }
}

data class ProfileUiState (
    val counterManName: String = "",
    val phoneNumber : String = "",
    val stoppage: String = "",
    val appInfo: String = "",
)

sealed class ProfileUiAction{
    object LogoutAction : ProfileUiAction()
    object ResetConfigAction : ProfileUiAction()
}

sealed class ProfileUiEvent {
    data class ShowToast(val message: String ) : ProfileUiEvent()
    data class ToggleProgressbar(val isLoading: Boolean ) : ProfileUiEvent()
    data class NavigateToLogin(val message: String) : ProfileUiEvent()
    object NavigateToEntryActivity : ProfileUiEvent()
}

