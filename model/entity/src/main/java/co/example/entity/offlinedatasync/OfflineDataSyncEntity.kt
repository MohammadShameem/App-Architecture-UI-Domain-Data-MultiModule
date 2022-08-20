package co.example.entity.offlinedatasync

import co.example.entity.room.BusStoppageEntity
import co.example.entity.room.FareModalityEntity
import co.example.entity.room.TicketFormatEntity
import co.example.entity.room.VehicleEntity

data class OfflineDataSyncEntity(
    val message: String,
    val company: CompanyEntity,
    val stoppage_list: List<BusStoppageEntity>,
    val vehicle_list: List<VehicleEntity>,
    val fare_modality: List<FareModalityEntity>,
    val countermanStoppageEntity: CountermanStoppageEntity,
    val serial: Int,
    val date_time:String
)

data class CompanyEntity(
    val id: Int,
    val name: String,
    val ticketFormatEntityList: List<TicketFormatEntity>,
    val contact: String,
    val address: String,
    val student_fare: Int,
    val special_fare: Int,
    val multiple_ticket: Int,
    val card_data: String,
    val printer_type: String,
)

