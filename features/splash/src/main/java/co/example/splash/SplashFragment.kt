package co.example.splash

import android.os.Bundle
import androidx.fragment.app.viewModels
import co.example.common.base.BaseFragment
import co.example.common.constant.AppConstant
import co.example.sharedpref.SharedPrefHelper
import co.example.splash.databinding.FragmentSplashBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    override fun viewBindingLayout(): FragmentSplashBinding =
        FragmentSplashBinding.inflate(layoutInflater)

    private val viewModel by viewModels<SplashViewModel>()
    private lateinit var callback: CommunicatorCallback

    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper

    companion object {
        fun newInstance(): SplashFragment {
            return SplashFragment()
        }
    }

    override fun initializeView(savedInstanceState: Bundle?) {
        val appVersion = requireActivity().packageManager.getPackageInfo(
            AppConstant.APP_PACKAGE_NAME, 0).versionName

        binding.appVersionTv.text = if (BuildConfig.DEBUG) "$appVersion (DEV)" else appVersion

        observeUiState()
        observeUiStateEvent()
    }

    private fun observeUiState() {
        viewModel.uiState.execute { uiState ->
            when (uiState) {
                is UserProfileUiState.Success -> callback.loadHomeScreen()
                is UserProfileUiState.NetworkError -> binding.root.showSimpleNetworkErrorLayout(
                    message = uiState.message,
                    refreshCallback = {
                        viewModel.action(UserProfileUiAction.RefreshProfile)
                    }
                )
                UserProfileUiState.Loading ->{}
            }
        }
    }

    private fun observeUiStateEvent(){
        viewModel.uiStateEvent.execute { uiStateEvent ->
            when(uiStateEvent){
                is UserProfileUiStateEvent.NavigateToHome -> callback.loadHomeScreen()
                is UserProfileUiStateEvent.NavigateToLogin ->callback.loadLoginScreen()
            }
        }
    }

    interface CommunicatorCallback{
        fun loadLoginScreen()
        fun loadHomeScreen()
    }
    fun setCommunicatorCallback(callback: CommunicatorCallback){
        this.callback = callback
    }


}