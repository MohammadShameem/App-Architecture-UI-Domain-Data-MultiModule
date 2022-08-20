package co.example.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "busStoppageList")
data class BusStoppageEntity(
    @PrimaryKey(autoGenerate = true) val primaryId: Int=0,
    val id: Int,
    val name: String,
    val fare: Int = 0,
    val studentFare: Int = 0,
    val isStudentFareType: Boolean = false,
)

