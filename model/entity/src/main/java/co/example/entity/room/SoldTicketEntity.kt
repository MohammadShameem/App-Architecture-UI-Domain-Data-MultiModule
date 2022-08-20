package co.example.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import co.example.dateutil.DateTimeParser
import co.example.dateutil.DateTimeFormat


@Entity(tableName = "soldTicket")
data class SoldTicketEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val serial: Int,
    val from_stoppage: String,
    val to_stoppage: String,
    val amount: Int,
    val created_at: String= DateTimeParser.getCurrentDeviceDateTime(DateTimeFormat.sqlYMDHMS)
)
