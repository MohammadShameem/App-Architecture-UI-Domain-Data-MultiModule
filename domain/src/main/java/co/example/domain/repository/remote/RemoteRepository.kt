package co.example.domain.repository.remote

import co.example.domain.base.ApiResult
import co.example.entity.dashboard.TodaySyncedTicketEntity
import co.example.entity.home.ReportPrintApiEntity
import co.example.entity.home.SyncSoldTicketApiEntity
import co.example.entity.home.SyncSoldTicketEntity
import co.example.entity.offlinedatasync.OfflineDataSyncEntity
import kotlinx.coroutines.flow.Flow

interface RemoteRepository {
    suspend fun getTodaySyncedTicketData() : Flow<ApiResult<TodaySyncedTicketEntity>>
    suspend fun getOfflineSyncData() : Flow<ApiResult<OfflineDataSyncEntity>>
    suspend fun syncSoldTicketsToServer(syncSoldTicketEntity: SyncSoldTicketEntity): Flow<ApiResult<SyncSoldTicketApiEntity>>
    suspend fun fetchReportPrintData(currentDate: String): Flow<ApiResult<ReportPrintApiEntity>>
}