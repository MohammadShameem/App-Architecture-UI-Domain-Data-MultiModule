package co.example.domain.usecase.local.home

import co.example.domain.base.RoomUseCaseNonParams
import co.example.domain.repository.local.DatabaseRepository
import co.example.entity.room.SoldTicketEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetSoldTicketListUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
): RoomUseCaseNonParams<List<SoldTicketEntity>> {
    override suspend fun execute(): Flow<List<SoldTicketEntity>> {
        return databaseRepository.getSoldTicketList()
    }
}