package co.example.domain.repository.remote.credential

import co.example.domain.base.ApiResult
import co.example.domain.usecase.remote.credential.LoginApiUseCase
import co.example.entity.credential.LoginApiEntity
import co.example.entity.credential.LogoutApiEntity
import co.example.entity.profile.UserDetailsApiEntity
import kotlinx.coroutines.flow.Flow

interface CredentialRepository {
    suspend fun login(params: LoginApiUseCase.Params): Flow<ApiResult<LoginApiEntity>>
    suspend fun logout(): Flow<ApiResult<LogoutApiEntity>>
    suspend fun fetchProfile(): Flow<ApiResult<UserDetailsApiEntity>>
}