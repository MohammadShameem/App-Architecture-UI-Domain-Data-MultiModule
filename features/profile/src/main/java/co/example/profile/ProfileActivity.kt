package co.example.profile

import android.os.Bundle
import androidx.activity.viewModels
import co.example.assets.R
import co.example.common.base.BaseActivity
import co.example.common.constant.AppConstant
import co.example.common.constant.Intentkey
import co.example.common.extfun.navigateEntryActivity
import co.example.common.extfun.showAlertDialog
import co.example.common.extfun.toolBarWithBackBtn
import co.example.profile.databinding.ActivityProfileBinding
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ProfileActivity : BaseActivity<ActivityProfileBinding>() {

    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper
    private val viewModel by viewModels<ProfileViewModel>()

    override fun viewBindingLayout(): ActivityProfileBinding =
        ActivityProfileBinding.inflate(layoutInflater)

    override fun initializeView(savedInstanceState: Bundle?) {
        toolBarWithBackBtn(binding.toolBar.toolbar, getString(R.string.profile))
        val appInfo =
            "${intent.getStringExtra(Intentkey.APP_NAME)}, ${intent.getStringExtra(Intentkey.APP_VERSION)} - ${intent.getStringExtra(Intentkey.APP_TYPE)}"
        binding.appInfoValueTv.text = appInfo

        viewModel.uiState bindTo { state -> binding.bindUiState(state) }
        viewModel.uiEvent bindTo { action -> bindUiEvent(action) }

        binding.logoutBtn.setOnClickListener {
            showAlertDialog(
                getString(R.string.confirm),
                getString(R.string.no_text),
                getString(R.string.logout),
                getString(R.string.msg_are_you_sure),
                true,
                {viewModel.action.invoke(ProfileUiAction.LogoutAction)},
                {}
            )
        }

        binding.resetConfigBtn.setOnClickListener {
            showAlertDialog(
                getString(R.string.confirm),
                getString(R.string.no_text),
                getString(R.string.reset_config),
                getString(R.string.msg_are_you_sure),
                true,
                {viewModel.action.invoke(ProfileUiAction.ResetConfigAction)},
                {}

            )
        }
    }

    private fun ActivityProfileBinding.bindUiState(state: ProfileUiState) {
        val directionText= if(sharedPrefHelper.getInt(SpKey.selectedDirection)==0) AppConstant.down else AppConstant.up
        state.run {
            nameTV.text = counterManName
            mobileNumberTV.text = phoneNumber
            stoppageValueTv.text ="$stoppage ($directionText)"
        }
    }

    private fun bindUiEvent(uiEvent: ProfileUiEvent){
        when (uiEvent) {
            is ProfileUiEvent.ShowToast -> showMessage(uiEvent.message)
            is ProfileUiEvent.NavigateToLogin -> {
                setResult(RESULT_OK)
                finish()
            }
            is ProfileUiEvent.NavigateToEntryActivity -> navigateEntryActivity()
            is ProfileUiEvent.ToggleProgressbar -> showProgressBar(uiEvent.isLoading, binding.progressBar)
        }
    }
}