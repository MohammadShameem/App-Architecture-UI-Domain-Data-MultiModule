package co.example.login

import co.example.common.base.BaseViewModel
import co.example.domain.base.ApiResult
import co.example.domain.usecase.local.offlinedatasync.CacheOfflineDataUseCase
import co.example.domain.usecase.remote.credential.LoginApiUseCase
import co.example.domain.usecase.remote.profile.OfflineDataSyncApiUseCase
import co.example.domain.usecase.remote.profile.ProfileApiUseCase
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginApiUseCase: LoginApiUseCase,
    private val profileApiUseCase: ProfileApiUseCase,
    private val sharedPrefHelper: SharedPrefHelper,
    private val offlineDataSyncApiUseCase: OfflineDataSyncApiUseCase,
    private val cacheOfflineDataUseCase: CacheOfflineDataUseCase,
) : BaseViewModel() {
    val action: (LoginUiAction) -> Unit
    val loginIoError get() = loginApiUseCase.ioError.receiveAsFlow()


    private val _uiState = MutableStateFlow<LoginUiState<*>>(LoginUiState.Loading(false))
    val uiState get() = _uiState

    private val _uiStateEvent = Channel<LoginUiStateEvent<*>>()
    val uiStateEvent get() = _uiStateEvent.receiveAsFlow()

    init {
        action = {
            when (it) {
                is LoginUiAction.LoginAction -> login(it.params)
                is LoginUiAction.RefreshOfflineDataAction ->  fetchProfile()
            }
        }
    }

    private fun login(params: LoginApiUseCase.Params) {
        launchJob {
            loginApiUseCase.execute(params).collect { result ->
                when (result) {
                    is ApiResult.Success -> {
                        sharedPrefHelper.putBool(SpKey.loginStatus, true)
                        sharedPrefHelper.putString(SpKey.userAuthKey, result.data.token)
                        fetchProfile()
                    }
                    is ApiResult.Error -> _uiStateEvent.send(LoginUiStateEvent.LoginNetworkError(result.message))
                    is ApiResult.Loading -> _uiState.value = LoginUiState.Loading(result.loading)
                }
            }
        }
    }

    private  fun fetchProfile() {
        viewModelScopeExecute {
            profileApiUseCase.execute().collect { result ->
                when (result) {
                    is ApiResult.Success -> syncOfflineData()
                    is ApiResult.Error -> _uiState.value = LoginUiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = LoginUiState.Loading(result.loading)
                }
            }
        }

    }



    private  fun syncOfflineData() {
        viewModelScopeExecute {
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
                        _uiStateEvent.send(LoginUiStateEvent.NavigateToHome)
                    }
                    is ApiResult.Error -> _uiState.value = LoginUiState.Error(result.message)
                    is ApiResult.Loading -> _uiState.value = LoginUiState.Loading(result.loading)

                }
            }
        }
    }
}

sealed class LoginUiState<out R> {
    data class Loading(val loading: Boolean) : LoginUiState<Boolean>()
    data class Error(val message: String) : LoginUiState<Error>()

}

sealed class LoginUiStateEvent<out R>{
    object NavigateToHome : LoginUiStateEvent<NavigateToHome>()
    data class LoginNetworkError(val message: String) : LoginUiStateEvent<LoginNetworkError>()
}


sealed class LoginUiAction {
    data class LoginAction(val params: LoginApiUseCase.Params) : LoginUiAction()
    object RefreshOfflineDataAction : LoginUiAction()
}

