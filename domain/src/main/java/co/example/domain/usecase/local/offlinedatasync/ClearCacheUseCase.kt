package co.example.domain.usecase.local.offlinedatasync

import co.example.domain.base.RoomUseCaseNonParamsNoReturn
import co.example.domain.repository.local.DatabaseRepository
import co.example.sharedpref.SharedPrefHelper
import javax.inject.Inject

class ClearCacheUseCase @Inject constructor(
    private val repository: DatabaseRepository,
    private val sharedPrefHelper: SharedPrefHelper
) : RoomUseCaseNonParamsNoReturn {
    override suspend fun execute() {
        //CLean cache
        repository.deleteAllStoppage()
        repository.deleteAllFareModality()
        repository.deleteAllVehicle()
        repository.deleteAllTicketFormatEntity()
        sharedPrefHelper.clearAllCache()

    }
}