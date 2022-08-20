package co.example.domain.usecase.local.home

import co.example.domain.base.RoomUseCaseNonParams
import co.example.domain.repository.local.DatabaseRepository
import co.example.entity.room.BusStoppageEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import kotlin.math.roundToInt

class GetStoppageEntityListUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository,
    private val sharedPrefHelper: SharedPrefHelper
):RoomUseCaseNonParams<List<BusStoppageEntity>> {

    override suspend fun execute(): Flow<List<BusStoppageEntity>> {

        val selectedBusStoppageId = sharedPrefHelper.getInt(SpKey.selectedBusStoppageId)
        // Direction 1 = UP and 0 = down
        val busStoppageList :List<BusStoppageEntity>  = if(sharedPrefHelper.getInt(SpKey.selectedDirection)==0){
            databaseRepository.getBusStoppageBeforeSelectedCounter(selectedBusStoppageId)
        }else databaseRepository.getBusStoppageAfterSelectedCounter(selectedBusStoppageId)

        val toStoppageList = mutableListOf<BusStoppageEntity>()

        if (busStoppageList.isNotEmpty()){
             busStoppageList.forEach {

                val normalFare = databaseRepository.getStoppageFare(selectedBusStoppageId,
                     it.id,sharedPrefHelper.getInt(SpKey.companyId))

                 toStoppageList.add(BusStoppageEntity(
                    id = it.id,
                    name = it.name,
                    fare = normalFare,
                    studentFare = (normalFare/2.0).roundToInt()
                ))
            }
        }
        return flowOf(toStoppageList)
    }
}