package co.example.domain.repository.local
import co.example.entity.room.*
import kotlinx.coroutines.flow.Flow


interface DatabaseRepository{
    //soldTicket Table
    suspend fun insertSoldTicketList(soldTicketEntityList: List<SoldTicketEntity>)

    suspend fun getTotalUnSyncSoldTicketCount() : Flow<Int>
    suspend fun getTotalUnSyncSoldTicketAmount() : Flow<Int>

    suspend fun insertStoppageList(stoppageList: List<BusStoppageEntity>)
    suspend fun getAllStoppage(): Flow<List<BusStoppageEntity>>
    suspend fun deleteAllStoppage()

    suspend fun insertFareModalityList(fareModalityList: List<FareModalityEntity>)
    suspend fun getAllFareModality() : Flow<List<FareModalityEntity>>
    suspend fun deleteAllFareModality()

    suspend fun insertVehicleList(vehicleList: List<VehicleEntity>)
    suspend fun getAllVehicle() : Flow<List<VehicleEntity>>
    suspend fun deleteAllVehicle()
    suspend fun getSoldTicketList() : Flow<List<SoldTicketEntity>>
    suspend fun deleteSoldTicketList(soldTicketList: List<SoldTicketEntity>)

    suspend fun getStoppageFare(fromStoppageId:Int, toStoppageId:Int, companyId:Int): Int
    suspend fun getBusStoppageAfterSelectedCounter(busStoppageId:Int): List<BusStoppageEntity>
    suspend fun getBusStoppageBeforeSelectedCounter(busStoppageId:Int): List<BusStoppageEntity>


    // for ticket format
    suspend fun insertTicketFormatEntityList(ticketFormatEntityList: List<TicketFormatEntity>)
    suspend fun deleteAllTicketFormatEntity()
    suspend fun getAllTicketFormatEntity() : Flow<List<TicketFormatEntity>>

}