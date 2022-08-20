package co.example.domain.usecase.local.offlinedatasync

import co.example.domain.base.RoomUseCaseParamsNoReturn
import co.example.domain.repository.local.DatabaseRepository
import co.example.entity.room.BusStoppageEntity
import co.example.entity.room.FareModalityEntity
import co.example.entity.room.TicketFormatEntity
import co.example.entity.room.VehicleEntity
import co.example.sharedpref.SharedPrefHelper
import co.example.sharedpref.SpKey
import javax.inject.Inject

class CacheOfflineDataUseCase @Inject constructor(
    private val repository: DatabaseRepository,
    private val sharedPrefHelper: SharedPrefHelper
) : RoomUseCaseParamsNoReturn<CacheOfflineDataUseCase.Params> {
    data class Params(
        val stoppageList: List<BusStoppageEntity>,
        val fareModalityList: List<FareModalityEntity>,
        val vehicleList: List<VehicleEntity>,
        val ticketFormatEntityList: List<TicketFormatEntity>
    )

    override suspend fun execute(params: Params) {
        //CLean cache
        repository.deleteAllStoppage()
        repository.deleteAllFareModality()
        repository.deleteAllTicketFormatEntity()

        sharedPrefHelper.putBool(SpKey.isTicketConfirmationDialogEnable,false)

        //Insert new data
        repository.insertStoppageList(params.stoppageList)
        repository.insertFareModalityList(params.fareModalityList)
        repository.insertTicketFormatEntityList(params.ticketFormatEntityList)
    }
}