package co.example.domain.usecase.local.home

import co.example.domain.base.RoomUseCaseParamsNoReturn
import co.example.domain.repository.local.DatabaseRepository
import co.example.entity.room.SoldTicketEntity
import javax.inject.Inject

class InsertSoldTicketUseCase @Inject constructor(
    private val databaseRepository: DatabaseRepository
) : RoomUseCaseParamsNoReturn<InsertSoldTicketUseCase.Params> {
    data class Params(
        val soldTicketEntityList: List<SoldTicketEntity>
    )

    override suspend fun execute(params: Params) =
        databaseRepository.insertSoldTicketList(params.soldTicketEntityList)
}