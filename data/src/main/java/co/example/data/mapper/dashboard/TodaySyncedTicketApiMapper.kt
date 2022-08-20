package co.example.data.mapper.dashboard

import co.example.apiresponse.dashboard.TodaySyncedTicketApiResponse
import co.example.data.mapper.Mapper
import co.example.entity.dashboard.TodaySyncedTicketEntity
import javax.inject.Inject

class TodaySyncedTicketApiMapper @Inject constructor() : Mapper<TodaySyncedTicketApiResponse, TodaySyncedTicketEntity> {
    override fun mapFromApiResponse(type: TodaySyncedTicketApiResponse): TodaySyncedTicketEntity {
        return TodaySyncedTicketEntity(
           message = type.message,
           totalTicketCount = type.synced_today_count,
           totalTicketAmount = type.synced_today_amount
        )
    }

}