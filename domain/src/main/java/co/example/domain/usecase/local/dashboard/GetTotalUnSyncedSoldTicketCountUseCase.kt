package co.example.domain.usecase.local.dashboard

import co.example.domain.base.RoomUseCaseNonParams
import co.example.domain.repository.local.DatabaseRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTotalUnSyncedSoldTicketCountUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
): RoomUseCaseNonParams<Int> {
    override suspend fun execute(): Flow<Int> {
       return databaseRepository.getTotalUnSyncSoldTicketCount()
    }
}