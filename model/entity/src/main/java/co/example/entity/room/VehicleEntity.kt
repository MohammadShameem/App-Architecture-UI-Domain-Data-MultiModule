package co.example.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vehicleList")
data class VehicleEntity(
    @PrimaryKey(autoGenerate = true)
    val primaryId:Int = 0,
    val id: Int,
    val number: String,
    val seat_capacity: Int,
    val company_id: Int
)