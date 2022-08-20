package co.example.uidomaindatamultimodule.di.module

import android.app.Application
import android.content.Context
import co.example.di.authrefresh.AuthRefreshServiceHolder
import co.example.sharedpref.SharedPrefHelper
import co.example.sunmibanglaprinter.SunmiTicketPrintHelper
import co.example.bluetoothprinter.BluetoothThreadPrinterHelper
import co.example.bluetoothprinter.BluetoothTicketPrintHelper
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApplicationModule {

    @Provides
    @Singleton
    fun sharePrefHelper(@ApplicationContext context: Context): SharedPrefHelper = SharedPrefHelper(context)

    @Provides
    @Singleton
    fun provideAuthRefreshServiceHolder() : AuthRefreshServiceHolder = AuthRefreshServiceHolder()


    @Provides
    @Singleton
    fun provideBluetoothThreadPrinterHelperHolder(@ApplicationContext context: Context) : BluetoothThreadPrinterHelper
        = BluetoothThreadPrinterHelper(context)

    @Provides
    @Singleton
    fun provideSunmiTicketPrinterHelper(application: Application,sharedPrefHelper: SharedPrefHelper) =
        SunmiTicketPrintHelper(application,sharedPrefHelper)

    @Provides
    @Singleton
    fun provideBluetoothTicketPrinterHelper(bluetoothPrinterHelper: BluetoothThreadPrinterHelper,sharedPrefHelper: SharedPrefHelper) =
        BluetoothTicketPrintHelper(bluetoothPrinterHelper,sharedPrefHelper)

}