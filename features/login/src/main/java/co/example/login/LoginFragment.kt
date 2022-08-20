package co.example.login

import android.os.Bundle
import androidx.fragment.app.viewModels
import co.example.common.base.BaseFragment
import co.example.common.extfun.*
import co.example.domain.base.LoginIoResult
import co.example.domain.usecase.remote.credential.LoginApiUseCase
import co.example.login.databinding.ActivityLoginBinding
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import co.example.assets.R as Assets

@AndroidEntryPoint
class LoginFragment : BaseFragment<ActivityLoginBinding>() {
    override fun viewBindingLayout(): ActivityLoginBinding =
        ActivityLoginBinding.inflate(layoutInflater)

    private val viewModel by viewModels<LoginViewModel>()
    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper
    private lateinit var callback: CommunicatorCallback

    companion object {
        fun newInstance(): LoginFragment {
            return LoginFragment()
        }
    }

    override fun initializeView(savedInstanceState: Bundle?) {

        viewModel.uiState bindTo ::bindUiState
        viewModel.loginIoError bindTo ::bindIoError
        viewModel.uiStateEvent bindTo ::bindUiStateEvent

        val isLoggedIn = sharedPrefHelper.getBoolean(SpKey.loginStatus)
        val isOfflineDataSynced = sharedPrefHelper.getBoolean(SpKey.offlineDataSyncStatus)
        if (isLoggedIn && !isOfflineDataSynced) showErrorLayout()


        /** @clickWithDebounce will dispatch double click
         *  click login button
         *  check mobile and password input validation
         *  @call login api **/
        binding.loginBtn.clickWithDebounce {
            binding.loginBtn.hideKeyboard()
            viewModel.action(
                LoginUiAction.LoginAction(
                    LoginApiUseCase.Params(
                        mobile = binding.phoneNumberTIL.checkErrorToText(
                            errorMessage = getString(
                                Assets.string.give_right_phone_number
                            )
                        ),
                        password = binding.passwordTIL.checkErrorToText(
                            errorMessage = getString(
                                Assets.string.give_right_password
                            )
                        )
                    )
                )
            )
        }
    }

    private fun bindUiState(state: LoginUiState<*>) {
        when (state) {
            is LoginUiState.Loading -> showProgressBar(isLoading = state.loading,binding.progressBar)
            is LoginUiState.Error -> showErrorLayout(message = state.message)
        }
    }

    private fun bindUiStateEvent(state: LoginUiStateEvent<*>) {
        when (state) {
            is LoginUiStateEvent.LoginNetworkError -> showToastMessage(state.message)
            is LoginUiStateEvent.NavigateToHome -> this.callback.loadHomeScreen()
        }
    }

    private fun bindIoError(state: LoginIoResult){
        when(state){
            LoginIoResult.EmptyPassword ->binding.passwordTIL.error = "Empty Password"
            LoginIoResult.EmptyPhoneNumber -> binding.phoneNumberTIL.error = "Empty Phone Number"
            LoginIoResult.InvalidPassword ->binding.passwordTIL.error = "Invalid Password"
            LoginIoResult.InvalidPhoneNumber ->binding.phoneNumberTIL.error = "Invalid Phone Number"
        }
    }

    private fun showErrorLayout(message:String = "Offline data sync failed, refresh data.") {
        binding.root.showSimpleNetworkErrorLayout(
            message = message,
            refreshCallback = {
                viewModel.action(LoginUiAction.RefreshOfflineDataAction)
            }
        )
    }

    interface CommunicatorCallback {
        fun loadHomeScreen()
    }

    fun setCommunicatorCallback(callback: CommunicatorCallback) {
        this.callback = callback
    }

}
