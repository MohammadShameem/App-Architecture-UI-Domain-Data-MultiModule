package co.example.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ticketFormat")
data class TicketFormatEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val font_size: Int,
    val is_bold: Boolean,
    val is_center: Boolean?,
    val leading_student_fare_text: String = "",
    val leading_text: String = "",
    val name: String = "",
    val text: String = "",
    val image_url: String = "",
    val trailing_text: String = "",
    val bluetooth_font_size:String = "NORMAL",
    val qrcode_size:Int = 5,
    val type: String,
    val up_route_name:String = "",
    val down_route_name: String = ""
)

/*
   //bluetooth text size
    NORMAL,
    DOUBLE,
    DOUBLE_HEIGHT,
    DOUBLE_WIDTH,
    DOUBLE_2_5,
    TRIPLE,
    TRIPLE_HEIGHT,
    TRIPLE_WIDTH,
    QUADRUPLE,
    QUADRUPLE_HEIGHT,
    QUADRUPLE_WIDTH

    //qr code text size
    bluetooth: 1-15, sunmi: 1-8, default : 5

*/