package co.example.data.mapper.credential

import co.example.apiresponse.offlinedatasync.OfflineDataSyncApiResponse
import co.example.data.mapper.Mapper
import co.example.entity.offlinedatasync.CompanyEntity
import co.example.entity.offlinedatasync.CountermanStoppageEntity
import co.example.entity.offlinedatasync.OfflineDataSyncEntity
import co.example.entity.room.BusStoppageEntity
import co.example.entity.room.FareModalityEntity
import co.example.entity.room.TicketFormatEntity
import co.example.entity.room.VehicleEntity
import javax.inject.Inject

class OfflineSyncDataApiMapper @Inject constructor() :
    Mapper<OfflineDataSyncApiResponse, OfflineDataSyncEntity> {
    override fun mapFromApiResponse(type: OfflineDataSyncApiResponse): OfflineDataSyncEntity {
        return OfflineDataSyncEntity(
            message = type.message?:"",
            company = CompanyEntity(
                id = type.company?.id ?:-1,
                name = type.company?.name ?:"",
                ticketFormatEntityList = type.company?.ticket_format?.map { ticketFormat ->
                        TicketFormatEntity(
                            image_url = ticketFormat.image_url?:"",
                            font_size = ticketFormat.font_size ?: 0,
                            is_bold = ticketFormat.is_bold ?: false,
                            is_center = ticketFormat.is_center,
                            leading_student_fare_text = ticketFormat.leading_student_fare_text ?: "",
                            leading_text = ticketFormat.leading_text ?: "",
                            name = ticketFormat.name ?: "",
                            text = ticketFormat.text ?: "",
                            trailing_text = ticketFormat.trailing_text ?: "",
                            type = ticketFormat.type ?: "",
                            bluetooth_font_size = ticketFormat.bluetooth_font_size ?: "",
                            qrcode_size = ticketFormat.qrcode_size ?: 5,
                            up_route_name = ticketFormat.up_route_name ?:"",
                            down_route_name = ticketFormat.down_route_name ?: ""
                        )
                    }?: emptyList(),

                contact = type.company?.contact?:"",
                address = type.company?.address?:"",
                student_fare = type.company?.student_fare?:-1,
                special_fare = type.company?.special_fare?:-1,
                multiple_ticket = type.company?.multiple_ticket?:-1,
                card_data = type.company?.card_data?:"",
                printer_type = type.company?.printer_type?:""
            ),
            stoppage_list = type.stoppage_list?.map { stoppage ->
                BusStoppageEntity(
                    id = stoppage.id?:-1,
                    name = stoppage.name?:""
                )

            }?: emptyList(),
            vehicle_list = type.vehicle_list?.map { vehicle ->
                VehicleEntity(
                    id = vehicle.id?:-1,
                    number = vehicle.number?:"",
                    seat_capacity = vehicle.seat_capacity?:-1,
                    company_id = vehicle.company_id?:-1
                )
            }?: emptyList(),
            fare_modality = type.fare_modality?.map { fareModality ->
                FareModalityEntity(
                    company_id = fareModality.company_id?:-1,
                    fare = fareModality.fare?:-1,
                    from_stoppage_id = fareModality.from_stoppage_id?:-1,
                    to_stoppage_id = fareModality.to_stoppage_id?:-1
                )
            }?: emptyList(),
            countermanStoppageEntity = CountermanStoppageEntity(
                id = type.counterman_stoppage?.id?: 0,
                name = type.counterman_stoppage?.name?: "",
                name_bn = type.counterman_stoppage?.name_bn?:""
            ),
            serial = type.serial?:-1,
            date_time = type.date_time?:""
        )
    }
}