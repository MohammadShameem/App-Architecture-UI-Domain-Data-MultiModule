package co.example.domain.usecase.remote.profile

import co.example.domain.base.ApiResult
import co.example.domain.base.ApiUseCaseNonParams
import co.example.domain.repository.remote.RemoteRepository
import co.example.entity.offlinedatasync.OfflineDataSyncEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class OfflineDataSyncApiUseCase @Inject constructor(
    private val remoteRepository: RemoteRepository
) : ApiUseCaseNonParams<OfflineDataSyncEntity>{
    override suspend fun execute(): Flow<ApiResult<OfflineDataSyncEntity>> {
        return remoteRepository.getOfflineSyncData()
    }
}