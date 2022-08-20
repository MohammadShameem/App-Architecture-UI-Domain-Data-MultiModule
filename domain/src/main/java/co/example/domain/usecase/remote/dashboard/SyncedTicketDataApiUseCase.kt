package co.example.domain.usecase.remote.dashboard

import co.example.domain.base.ApiResult
import co.example.domain.base.ApiUseCaseNonParams
import co.example.domain.repository.remote.RemoteRepository
import co.example.entity.dashboard.TodaySyncedTicketEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SyncedTicketDataApiUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
): ApiUseCaseNonParams<TodaySyncedTicketEntity> {
    override suspend fun execute(): Flow<ApiResult<TodaySyncedTicketEntity>> {
       return remoteRepository.getTodaySyncedTicketData()
    }
}