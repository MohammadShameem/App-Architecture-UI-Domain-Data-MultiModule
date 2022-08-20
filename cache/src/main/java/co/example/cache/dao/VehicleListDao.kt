package co.example.cache.dao

import androidx.room.*
import co.example.entity.room.VehicleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface VehicleListDao {
    @Insert
    suspend fun insertVehicleListEntity(vehicleList: List<VehicleEntity>)

    @Query("DELETE  FROM vehicleList")
    suspend fun deleteAllVehicle()

    @Query("SELECT * FROM vehicleList")
    fun getAllVehicle(): Flow<List<VehicleEntity>>

}