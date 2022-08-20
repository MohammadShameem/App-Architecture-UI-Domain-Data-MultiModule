package co.example.uidomaindatamultimodule.ui.entry

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import co.example.uidomaindatamultimodule.R
import co.example.uidomaindatamultimodule.ui.home.HomeActivity
import co.example.login.LoginFragment
import co.example.sharedpref.SharedPrefHelper
import co.example.splash.SplashFragment
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EntryActivity : AppCompatActivity(),SplashFragment.CommunicatorCallback,
    LoginFragment.CommunicatorCallback{

    @Inject lateinit var sharedPrefHelper: SharedPrefHelper

    private fun onAttachFragmentListener() {
        supportFragmentManager.addFragmentOnAttachListener { _, fragment ->
            if (fragment is SplashFragment) {
                fragment.setCommunicatorCallback(this)
            } else if (fragment is LoginFragment) {
               fragment.setCommunicatorCallback(this)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        onAttachFragmentListener()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry)

        if (savedInstanceState == null){
         supportFragmentManager.beginTransaction().add(R.id.screenContainer,
             SplashFragment.newInstance())
             .commit()
        }

    }

    override fun loadLoginScreen() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.screenContainer, LoginFragment.newInstance())
            .commitAllowingStateLoss()
    }

    override fun loadHomeScreen() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }
}