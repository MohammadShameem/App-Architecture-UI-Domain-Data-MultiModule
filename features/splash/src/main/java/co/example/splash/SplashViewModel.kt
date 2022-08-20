package co.example.splash


import co.example.common.base.BaseViewModel
import co.example.domain.base.ApiResult
import co.example.domain.usecase.local.dashboard.GetTotalUnSyncedSoldTicketCountUseCase
import co.example.domain.usecase.local.offlinedatasync.ClearCacheUseCase
import co.example.domain.usecase.remote.profile.ProfileApiUseCase
import co.example.entity.profile.ProfileApiEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class SplashViewModel @Inject constructor(
    private val profileApiUseCase: ProfileApiUseCase,
    private val totalUnSyncedSoldTicketCountUseCase: GetTotalUnSyncedSoldTicketCountUseCase,
    private val clearCacheUseCase: ClearCacheUseCase,
    private val sharedPrefHelper: SharedPrefHelper
) : BaseViewModel() {
    private val _uiState = MutableStateFlow<UserProfileUiState<*>>(UserProfileUiState.Loading)
    val uiState  get() = _uiState

    private val _uiStateEvent = Channel<UserProfileUiStateEvent<*>>()
    val uiStateEvent  get() = _uiStateEvent.receiveAsFlow()

    val action: (UserProfileUiAction) -> Unit

    init {
        checkLoginStatus()
        action = {
            when(it){
                is UserProfileUiAction.RefreshProfile -> {
                    fetchUserProfile()
                }
            }
        }
    }

    private fun checkLoginStatus(){
        viewModelScopeExecute {
            val isOfflineDataSynced = sharedPrefHelper.getBoolean(SpKey.offlineDataSyncStatus)
            if (sharedPrefHelper.getBoolean(SpKey.loginStatus)) {
                if (isOfflineDataSynced) fetchUserProfile()
                else _uiStateEvent.send(UserProfileUiStateEvent.NavigateToLogin)
            } else _uiStateEvent.send(UserProfileUiStateEvent.NavigateToLogin)
        }
    }

    private fun fetchUserProfile(){
        viewModelScopeExecute {
            profileApiUseCase.execute().collect{ apiResponse->
                when(apiResponse){
                    is ApiResult.Success -> _uiState.value = UserProfileUiState.Success
                    is ApiResult.Error -> {
                        if (apiResponse.code==400||apiResponse.code==401||apiResponse.code==402){
                            totalUnSyncedSoldTicketCountUseCase.execute().collect{
                                if (it>0) _uiState.value = UserProfileUiState.NetworkError(apiResponse.message)
                                else {
                                    withContext(Dispatchers.Default) {
                                        clearCacheUseCase.execute()
                                    }
                                    _uiStateEvent.send(UserProfileUiStateEvent.NavigateToLogin)
                                }
                            }
                        }else  _uiState.value = UserProfileUiState.NetworkError(apiResponse.message)
                    }
                    is ApiResult.Loading-> {}
                }

            }
        }
    }

}

sealed class UserProfileUiState<out R> {
    object Success : UserProfileUiState<ProfileApiEntity>()
    object Loading : UserProfileUiState<Loading>()
    data class NetworkError(val message: String) : UserProfileUiState<NetworkError>()
}

sealed class  UserProfileUiStateEvent<out R>{
    object  NavigateToLogin:UserProfileUiStateEvent<NavigateToLogin>()
    object NavigateToHome:UserProfileUiStateEvent<NavigateToLogin>()
}

sealed class UserProfileUiAction {
    object RefreshProfile : UserProfileUiAction()
}
