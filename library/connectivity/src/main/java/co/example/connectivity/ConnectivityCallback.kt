package co.example.connectivity

import android.net.ConnectivityManager
import android.net.Network
import androidx.lifecycle.MutableLiveData


class ConnectivityCallback : ConnectivityManager.NetworkCallback() {
    val result = MutableLiveData<NetworkResult>()

    override fun onLost(network: Network) {
        result.postValue(NetworkResult.DISCONNECTED)
    }

    override fun onLosing(network: Network, maxMsToLive: Int) {
        super.onLosing(network, maxMsToLive)
    }

    override fun onAvailable(network: Network) {
        result.postValue(NetworkResult.CONNECTED)

    }
}