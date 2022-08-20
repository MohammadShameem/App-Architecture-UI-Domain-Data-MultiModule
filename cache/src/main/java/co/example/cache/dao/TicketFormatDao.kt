package co.example.cache.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import co.example.entity.room.TicketFormatEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TicketFormatDao {
    @Insert
    suspend fun insertTicketFormatEntityList(ticketFormatEntityList: List<TicketFormatEntity>)

    @Query("DELETE  FROM ticketFormat")
    suspend fun deleteAllTicketFormatEntity()

    @Query("SELECT * FROM ticketFormat")
    fun getAllTicketFormatEntity() : Flow<List<TicketFormatEntity>>
}