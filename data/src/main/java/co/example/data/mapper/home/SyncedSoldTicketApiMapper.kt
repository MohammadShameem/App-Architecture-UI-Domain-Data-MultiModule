package co.example.data.mapper.home

import co.example.apiresponse.syncsoldticket.SyncSoldTicketApiResponse
import co.example.data.mapper.Mapper
import co.example.entity.home.SyncSoldTicketApiEntity
import javax.inject.Inject

class SyncedSoldTicketApiMapper @Inject constructor(): Mapper<SyncSoldTicketApiResponse, SyncSoldTicketApiEntity> {
    override fun mapFromApiResponse(type: SyncSoldTicketApiResponse): SyncSoldTicketApiEntity {
         return SyncSoldTicketApiEntity(
             message = type.message
         )
    }
}