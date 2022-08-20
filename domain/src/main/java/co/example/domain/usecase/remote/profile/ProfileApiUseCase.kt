package co.example.domain.usecase.remote.profile

import co.example.domain.base.ApiResult
import co.example.domain.base.ApiUseCaseNonParams
import co.example.domain.repository.remote.credential.CredentialRepository
import co.example.entity.profile.UserDetailsApiEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ProfileApiUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository
): ApiUseCaseNonParams<UserDetailsApiEntity> {
    
    override suspend fun execute(): Flow<ApiResult<UserDetailsApiEntity>> {
        return credentialRepository.fetchProfile()
    }

}
