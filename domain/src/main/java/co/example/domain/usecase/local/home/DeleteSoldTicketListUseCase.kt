package co.example.domain.usecase.local.home

import co.example.domain.base.RoomUseCaseParamsNoReturn
import co.example.domain.repository.local.DatabaseRepository
import co.example.entity.room.SoldTicketEntity
import javax.inject.Inject

class DeleteSoldTicketListUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
): RoomUseCaseParamsNoReturn<List<SoldTicketEntity>> {
    override suspend fun execute(params: List<SoldTicketEntity>) {
        databaseRepository.deleteSoldTicketList(params)
    }
}