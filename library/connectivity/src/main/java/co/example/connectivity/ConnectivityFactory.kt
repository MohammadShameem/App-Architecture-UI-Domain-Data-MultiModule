package co.example.connectivity

import android.net.NetworkCapabilities
import android.net.NetworkRequest

class ConnectivityFactory {
    fun networkRequest() : NetworkRequest =
        NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_RESTRICTED)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .build()
}