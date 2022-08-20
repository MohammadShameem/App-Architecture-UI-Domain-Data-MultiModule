package co.example.settings

import android.os.Bundle
import co.example.common.base.BaseActivity
import co.example.common.extfun.toolBarWithBackBtn
import co.example.settings.databinding.ActivitySettingsBinding
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsActivity : BaseActivity<ActivitySettingsBinding>() {

    override fun viewBindingLayout(): ActivitySettingsBinding =
        ActivitySettingsBinding.inflate(layoutInflater)

    @Inject
    lateinit var sharedPrefHelper: SharedPrefHelper

    override fun initializeView(savedInstanceState: Bundle?) {

        toolBarWithBackBtn(binding.toolbar, getString(R.string.settings_title))

        binding.ticketConfirmationDialogCb.isChecked = sharedPrefHelper.getBoolean(SpKey.isTicketConfirmationDialogEnable)

        binding.ticketConfirmationDialogCb.setOnCheckedChangeListener { _, isChecked ->
            sharedPrefHelper.putBool(SpKey.isTicketConfirmationDialogEnable,isChecked)
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}
