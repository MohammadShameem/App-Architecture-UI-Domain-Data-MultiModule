package co.example.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.example.entity.room.BusStoppageEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BusStoppageDao {
    @Insert
    suspend fun insertBusStoppageListEntity(busStoppageList: List<BusStoppageEntity>)

    @Query("DELETE  FROM busStoppageList")
    suspend fun deleteAllBusStoppage()

    @Query("SELECT * FROM busStoppageList")
    fun getAllBusStoppage(): Flow<List<BusStoppageEntity>>

    @Query("SELECT * FROM busStoppageList WHERE primaryId > (SELECT primaryId FROM busStoppageList" +
            " WHERE id=:busStoppageId)")
    suspend fun getBusStoppageAfterSelectedCounter(busStoppageId:Int): List<BusStoppageEntity>

    @Query("SELECT * FROM busStoppageList WHERE primaryId<(SELECT primaryId FROM busStoppageList " +
            "WHERE id=:busStoppageId)")
    suspend fun getBusStoppageBeforeSelectedCounter(busStoppageId:Int): List<BusStoppageEntity>

}