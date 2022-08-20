package co.example.domain.usecase.remote.home

import co.example.domain.base.ApiResult
import co.example.domain.base.ApiUseCase
import co.example.domain.repository.remote.RemoteRepository
import co.example.entity.home.SyncSoldTicketApiEntity
import co.example.entity.home.SyncSoldTicketEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncSoldTicketApiUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
): ApiUseCase<SyncSoldTicketEntity,SyncSoldTicketApiEntity> {
    override suspend fun execute(params: SyncSoldTicketEntity): Flow<ApiResult<SyncSoldTicketApiEntity>> {
      return remoteRepository.syncSoldTicketsToServer(params)
    }
}