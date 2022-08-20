package co.example.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

open class BaseViewModel() : ViewModel() {
    fun viewModelScopeExecute(job: suspend () -> Unit) {
        viewModelScope.launch {
            job.invoke()
        }
    }
    /**
     * @param job A suspend function that you want to launch in viewModelScope
     * @return A coroutine Job
     * @author Srizan
     * */
    fun launchJob(job: suspend () -> Unit) = viewModelScope.launch { job.invoke() }

    fun execute(job: suspend () -> Unit) {
        viewModelScope.launch {
            job.invoke()
        }
    }
}