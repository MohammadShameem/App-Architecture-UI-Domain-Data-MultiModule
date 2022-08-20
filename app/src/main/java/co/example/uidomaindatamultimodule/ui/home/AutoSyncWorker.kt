package co.example.uidomaindatamultimodule.ui.home

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.*
import co.example.domain.base.ApiResult
import co.example.domain.usecase.local.home.DeleteSoldTicketListUseCase
import co.example.domain.usecase.local.home.GetSoldTicketListUseCase
import co.example.domain.usecase.remote.home.SyncSoldTicketApiUseCase
import co.example.entity.home.SyncSoldTicketEntity
import co.example.entity.room.SoldTicketEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay
import timber.log.Timber

@HiltWorker
class AutoSyncWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted val workerParams: WorkerParameters,
    private val SoldTicketListUseCase: GetSoldTicketListUseCase,
    private val deleteSyncedSoldTicketListUseCase: DeleteSoldTicketListUseCase,
    private val syncSoldTicketApiUseCase: SyncSoldTicketApiUseCase,
    private val sharedPrefHelper: SharedPrefHelper
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        SoldTicketListUseCase.execute().collect { soldTicketList ->
            syncDataToServer(soldTicketList)
        }
        return Result.success()
    }

    private suspend fun syncDataToServer(ticketList: List<SoldTicketEntity>) {
        if (ticketList.isNotEmpty()) {
            syncSoldTicketApiUseCase.execute(
                SyncSoldTicketEntity(
                    vehicle_id = sharedPrefHelper.getInt(SpKey.vehicleId),
                    item_count = ticketList.size,
                    bookings = ticketList
                )
            ).collect {
                when (it) {
                    is ApiResult.Success -> deleteSyncedSoldTicketListUseCase.execute(ticketList)
                    is ApiResult.Error -> Timber.d("${it.message}  ${it.code}")
                    is ApiResult.Loading -> {}
                }
            }
        }
        delay(180000)
       val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val request = OneTimeWorkRequest.Builder(AutoSyncWorker::class.java)
            .setConstraints(constraints)
            .build()
        WorkManager.getInstance(appContext).enqueueUniqueWork(
            "auto_sync",
            ExistingWorkPolicy.REPLACE, request
        )
    }

}