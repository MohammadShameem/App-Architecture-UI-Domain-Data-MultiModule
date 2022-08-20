package co.example.entity.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fairModality")
data class FareModalityEntity(
    @PrimaryKey(autoGenerate = true)
    val primaryId: Int = 0,
    val company_id: Int,
    val fare: Int,
    val from_stoppage_id: Int,
    val to_stoppage_id: Int
)

