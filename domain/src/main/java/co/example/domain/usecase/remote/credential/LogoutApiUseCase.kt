package co.example.domain.usecase.remote.credential

import co.example.domain.base.ApiResult
import co.example.domain.base.ApiUseCaseNonParams
import co.example.domain.repository.remote.credential.CredentialRepository
import co.example.entity.credential.LogoutApiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LogoutApiUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository
) : ApiUseCaseNonParams<LogoutApiEntity> {
    override suspend fun execute(): Flow<ApiResult<LogoutApiEntity>> {
        return credentialRepository.logout()
    }
}