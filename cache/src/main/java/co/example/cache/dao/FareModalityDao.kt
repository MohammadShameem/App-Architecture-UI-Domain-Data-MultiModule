package co.example.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.example.entity.room.FareModalityEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FareModalityDao {
    @Insert
    suspend fun insertFareModalityEntityList(fareModalityList: List<FareModalityEntity>)

    @Query("DELETE  FROM fairModality")
    suspend fun deleteAllFareModality()

    @Query("SELECT * FROM fairModality")
    fun getAllFareModality(): Flow<List<FareModalityEntity>>

    @Query("SELECT IFNULL((SELECT fare FROM fairModality WHERE from_stoppage_id=:fromStoppageId AND" +
            " to_stoppage_id =:toStoppageId AND company_id=:companyId),0)")
    suspend fun getFare(fromStoppageId:Int, toStoppageId:Int, companyId:Int):Int
}