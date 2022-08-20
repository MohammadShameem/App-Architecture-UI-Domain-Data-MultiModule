package co.example.entity.home
import co.example.entity.room.SoldTicketEntity

data class SyncSoldTicketEntity(
    val vehicle_id: Int,
    val item_count: Int,
    val bookings: List<SoldTicketEntity>
)
