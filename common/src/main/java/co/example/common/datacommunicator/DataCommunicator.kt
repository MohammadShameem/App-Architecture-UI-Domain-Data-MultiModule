package co.example.common.datacommunicator

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DataCommunicator: ViewModel() {

    private val _activeStatus = MutableLiveData<Int>()
    val getMasterActivityStatusObserver get() = _activeStatus
    fun sendMasterActivityStatus(isOnline:Int){
        _activeStatus.value = isOnline
    }

}