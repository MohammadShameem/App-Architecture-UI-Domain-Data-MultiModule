package co.example.cache.dao
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import co.example.entity.room.SoldTicketEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SoldTicketDao {
    @Insert
    suspend fun insertSoldTicketList(ticketList: List<SoldTicketEntity>)

    @Insert
    suspend fun insertSoldTicketListEntity(soldTicketList:List<SoldTicketEntity>)

    @Delete
    suspend fun deleteSoldTicketList(ticketList:List<SoldTicketEntity>)

    @Query("DELETE  FROM soldTicket")
    suspend fun deleteAllSoldTicket()

    @Query("SELECT * FROM soldTicket")
    fun getSoldTicketList():Flow<List<SoldTicketEntity>>

    @Query("SELECT IFNULL((SELECT COUNT(*) FROM soldTicket),0)")
    fun getTotalUnSyncSoldTicketCount():Flow<Int>

    @Query("SELECT IFNULL((SELECT SUM(amount) FROM soldTicket),0)")
    fun getTotalUnSyncSoldTicketAmount():Flow<Int>
}