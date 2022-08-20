package co.example.data.repoimpl

import co.example.data.apiservice.ApiServices
import co.example.data.mapper.credential.OfflineSyncDataApiMapper
import co.example.data.mapper.dashboard.TodaySyncedTicketApiMapper
import co.example.data.mapper.home.ReportPrintApiMapper
import co.example.data.mapper.home.SyncedSoldTicketApiMapper
import co.example.data.mapper.mapFromApiResponse
import co.example.data.wrapper.NetworkBoundResource
import co.example.dateutil.DateTimeFormat
import co.example.dateutil.DateTimeParser
import co.example.domain.base.ApiResult
import co.example.domain.repository.remote.RemoteRepository
import co.example.entity.dashboard.TodaySyncedTicketEntity
import co.example.entity.home.ReportPrintApiEntity
import co.example.entity.home.SyncSoldTicketApiEntity
import co.example.entity.home.SyncSoldTicketEntity
import co.example.entity.offlinedatasync.OfflineDataSyncEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RemoteRepoImpl @Inject constructor(
    private val apiServices: ApiServices,
    private val networkBoundResource: NetworkBoundResource,
    private val todaySyncedTicketApiMapper: TodaySyncedTicketApiMapper,
    private val offlineSyncDataApiMapper: OfflineSyncDataApiMapper,
    private val syncedSoldTicketApiMapper: SyncedSoldTicketApiMapper,
    private val reportPrintApiMapper: ReportPrintApiMapper,
    private val sharedPrefHelper: SharedPrefHelper
) : RemoteRepository {
    override suspend fun getTodaySyncedTicketData(): Flow<ApiResult<TodaySyncedTicketEntity>> {
       return mapFromApiResponse(
           apiResult = networkBoundResource.downloadData {
               apiServices.getTodaySyncedTicketData()
           },
           mapper = todaySyncedTicketApiMapper
       )
    }

    override suspend fun getOfflineSyncData(): Flow<ApiResult<OfflineDataSyncEntity>> {
        return mapFromApiResponse(
            apiResult = networkBoundResource.downloadData {
                apiServices.fetchOfflineDataSync()
            },
            mapper = offlineSyncDataApiMapper
        ).map { apiResult ->
                if (apiResult is ApiResult.Success) {
                    if (apiResult.data.company.student_fare == 1) sharedPrefHelper.putBool(SpKey.hasStudentFare,true)
                    else sharedPrefHelper.putBool(SpKey.hasStudentFare,false)
                    if (apiResult.data.company.special_fare == 1) sharedPrefHelper.putBool(SpKey.hasSpecialFare,true)
                    else sharedPrefHelper.putBool(SpKey.hasSpecialFare,false)
                    if (apiResult.data.company.multiple_ticket == 1) sharedPrefHelper.putBool(SpKey.hasMultipleTicket,true)
                    else sharedPrefHelper.putBool(SpKey.hasMultipleTicket,false)

                    sharedPrefHelper.putString(SpKey.cardData,apiResult.data.company.card_data)
                    sharedPrefHelper.putString(SpKey.printerType,apiResult.data.company.printer_type)
                    sharedPrefHelper.putString(SpKey.selectedBusStoppageName,apiResult.data.countermanStoppageEntity.name)
                    sharedPrefHelper.putString(SpKey.companyName,apiResult.data.company.name)
                    sharedPrefHelper.putInt(SpKey.soldTicketSerial,apiResult.data.serial)
                    sharedPrefHelper.putString(SpKey.soldTicketSerialCurrentDate, DateTimeParser.convertReadableDateTime(apiResult.data.date_time,
                                DateTimeFormat.sqlYMDHMS,DateTimeFormat.outputDMY))

                    if(apiResult.data.vehicle_list.isNotEmpty())
                        sharedPrefHelper.putInt(SpKey.vehicleId,apiResult.data.vehicle_list[0].id)
                }
                apiResult
        }
    }

    override suspend fun syncSoldTicketsToServer(syncSoldTicketEntity: SyncSoldTicketEntity): Flow<ApiResult<SyncSoldTicketApiEntity>> {
        return mapFromApiResponse(
            apiResult = networkBoundResource.downloadData {
                apiServices.syncSoldTicketsToServer(syncSoldTicketEntity)
            },
            mapper = syncedSoldTicketApiMapper
        )
    }

    override suspend fun fetchReportPrintData(currentDate: String): Flow<ApiResult<ReportPrintApiEntity>> {
        return mapFromApiResponse(
            apiResult = networkBoundResource.downloadData {
                apiServices.fetchPrintReport(currentDate)
            },
            mapper = reportPrintApiMapper
        )
    }

}