package co.example.data.repoimpl
import co.example.cache.dao.*
import co.example.domain.repository.local.DatabaseRepository
import co.example.entity.room.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DatabaseRepoImpl @Inject constructor(
    private val soldTicketDao: SoldTicketDao,
    private val fareModalityDao: FareModalityDao,
    private val busStoppageDao: BusStoppageDao,
    private val vehicleListDao: VehicleListDao,
    private val ticketFormatDao: TicketFormatDao
): DatabaseRepository {
    override suspend fun insertSoldTicketList(soldTicketEntityList: List<SoldTicketEntity>) = soldTicketDao.insertSoldTicketList(soldTicketEntityList)

    override suspend fun getTotalUnSyncSoldTicketCount(): Flow<Int> {
        return soldTicketDao.getTotalUnSyncSoldTicketCount()
    }

    override suspend fun getTotalUnSyncSoldTicketAmount(): Flow<Int> {
        return soldTicketDao.getTotalUnSyncSoldTicketAmount()
    }

    override suspend fun getStoppageFare(fromStoppageId:Int,
        toStoppageId:Int, companyId:Int): Int {

        return fareModalityDao.getFare(fromStoppageId,toStoppageId,companyId)
    }

    override suspend fun getBusStoppageAfterSelectedCounter(busStoppageId:Int):
            List<BusStoppageEntity>{
        return busStoppageDao.getBusStoppageAfterSelectedCounter(busStoppageId)
    }

    override suspend fun getBusStoppageBeforeSelectedCounter(busStoppageId: Int): List<BusStoppageEntity> {
        return busStoppageDao.getBusStoppageBeforeSelectedCounter(busStoppageId)
    }


    /**
     * For ticket format
     * */

    override suspend fun insertTicketFormatEntityList(ticketFormatEntityList: List<TicketFormatEntity>) {
       ticketFormatDao.insertTicketFormatEntityList(ticketFormatEntityList)
    }

    override suspend fun deleteAllTicketFormatEntity() {
       ticketFormatDao.deleteAllTicketFormatEntity()
    }

    override suspend fun getAllTicketFormatEntity(): Flow<List<TicketFormatEntity>> {
       return ticketFormatDao.getAllTicketFormatEntity()
    }



    override suspend fun insertStoppageList(stoppageList: List<BusStoppageEntity>) =
        busStoppageDao.insertBusStoppageListEntity(stoppageList)

    override suspend fun getAllStoppage() = busStoppageDao.getAllBusStoppage()

    override suspend fun deleteAllStoppage() = busStoppageDao.deleteAllBusStoppage()

    override suspend fun insertFareModalityList(fareModalityList: List<FareModalityEntity>) =
        fareModalityDao.insertFareModalityEntityList(fareModalityList)

    override suspend fun getAllFareModality() = fareModalityDao.getAllFareModality()

    override suspend fun deleteAllFareModality() = fareModalityDao.deleteAllFareModality()

    override suspend fun insertVehicleList(vehicleList: List<VehicleEntity>) =
        vehicleListDao.insertVehicleListEntity(vehicleList)

    override suspend fun getAllVehicle(): Flow<List<VehicleEntity>> = vehicleListDao.getAllVehicle()

    override suspend fun deleteAllVehicle() = vehicleListDao.deleteAllVehicle()

    override suspend fun getSoldTicketList(): Flow<List<SoldTicketEntity>> {
        return soldTicketDao.getSoldTicketList()
    }

    override suspend fun deleteSoldTicketList(soldTicketList: List<SoldTicketEntity>) {
        soldTicketDao.deleteSoldTicketList(soldTicketList)
    }

}