package co.example.uidomaindatamultimodule.application

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import co.example.assets.FontsOverride
import co.example.uidomaindatamultimodule.BuildConfig
import co.example.sharedpref.SharedPrefHelper
import co.example.sunmibanglaprinter.SunmiPrintHelper
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber
import javax.inject.Inject

@HiltAndroidApp
class IntraCityCounterAutoSyncApplication : Application(), Configuration.Provider {
        @Inject lateinit var sharedPrefHelper: SharedPrefHelper
        @Inject lateinit var workerFactory: HiltWorkerFactory

    override fun attachBaseContext(context: Context?) {
        super.attachBaseContext(context)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }

    override fun onCreate() {
        super.onCreate()
        SunmiPrintHelper.instance.initSunmiPrinterService(this)
        FontsOverride.setDefaultFont(this, "MONOSPACE",
            FontsOverride.regularFont)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        if (BuildConfig.DEBUG){
            Timber.plant(Timber.DebugTree())
        }
    }



}