package co.example.connectivity

import android.app.Application
import android.content.Context.CONNECTIVITY_SERVICE
import androidx.lifecycle.LiveData
import android.net.ConnectivityManager


class ConnectivityManager constructor(
    application: Application,
    private val connectivityFactory: ConnectivityFactory = ConnectivityFactory(),
    private val connectivityCallback: ConnectivityCallback = ConnectivityCallback()
){

    private val connectivityManager =
        application.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager

    val result : LiveData<NetworkResult>
        get() = connectivityCallback.result

    fun registerCallback() {
        val request = connectivityFactory.networkRequest()
        connectivityManager.registerNetworkCallback(request,connectivityCallback)
    }

    fun unregisterCallback() {
        connectivityManager.unregisterNetworkCallback(connectivityCallback)
    }

}