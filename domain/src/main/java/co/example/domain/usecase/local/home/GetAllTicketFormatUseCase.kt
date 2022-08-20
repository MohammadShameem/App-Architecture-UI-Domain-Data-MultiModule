package co.example.domain.usecase.local.home

import co.example.domain.base.RoomUseCaseNonParams
import co.example.domain.repository.local.DatabaseRepository
import co.example.entity.room.TicketFormatEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllTicketFormatUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
): RoomUseCaseNonParams<List<TicketFormatEntity>> {
    override suspend fun execute(): Flow<List<TicketFormatEntity>> {
        return databaseRepository.getAllTicketFormatEntity()
    }
}
