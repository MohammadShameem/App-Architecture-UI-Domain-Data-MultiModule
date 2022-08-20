package co.example.apiresponse.offlinedatasync

data class OfflineDataSyncApiResponse(
    val code: Int?,
    val status: String?,
    val message: String?,
    val company: Company?,
    val stoppage_list: List<Stoppage>?,
    val vehicle_list: List<Vehicle>?,
    val fare_modality: List<FareModality>?,
    val counterman_stoppage: CountermanStoppage?,
    val serial: Int?,
    val date_time: String?
)
data class TicketFormat(
    val font_size: Int?,
    val is_bold: Boolean?,
    val is_center: Boolean?,
    val is_dynamic: Boolean?,
    val leading_student_fare_text: String?,
    val leading_text: String?,
    val name: String?,
    val text: String?,
    val image_url : String?,
    val trailing_text: String?,
    val type: String?,
    val bluetooth_font_size:String?,
    val qrcode_size:Int?,
    val up_route_name: String?,
    val down_route_name: String?
)

data class Company(
    val id: Int?,
    val name: String?,
    val ticket_format: List<TicketFormat>?,
    val contact: String?,
    val address: String?,
    val student_fare: Int?,
    val special_fare: Int?,
    val multiple_ticket: Int?,
    val card_data: String?,
    val printer_type:String?
)

data class Stoppage(
    val id: Int?,
    val name: String?,
    val company_id: Int?
)

data class Vehicle(
    val id: Int?,
    val number: String?,
    val seat_capacity: Int?,
    val company_id: Int?
)

data class FareModality(
    val id: Int?,
    val from_stoppage_id: Int?,
    val to_stoppage_id: Int?,
    val company_id: Int?,
    val fare: Int?,
)

data class CountermanStoppage(
    val id: Int?,
    val name: String?,
    val name_bn: String?,
)