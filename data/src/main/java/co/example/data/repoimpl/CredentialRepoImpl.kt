package co.example.data.repoimpl

import co.example.data.apiservice.ApiServices
import co.example.data.mapper.credential.CacheProfile
import co.example.data.mapper.credential.LoginApiMapper
import co.example.data.mapper.credential.LogoutApiMapper
import co.example.data.mapper.credential.ProfileApiMapper
import co.example.data.mapper.mapFromApiResponse
import co.example.data.wrapper.NetworkBoundResource
import co.example.domain.base.ApiResult
import co.example.domain.repository.remote.credential.CredentialRepository
import co.example.domain.usecase.remote.credential.LoginApiUseCase
import co.example.entity.credential.LoginApiEntity
import co.example.entity.credential.LogoutApiEntity
import co.example.entity.profile.UserDetailsApiEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CredentialRepoImpl @Inject constructor(
    private val apiServices: ApiServices,
    private val loginApiMapper: LoginApiMapper,
    private val profileApiMapper: ProfileApiMapper,
    private val logoutApiMapper: LogoutApiMapper,
    private val cacheProfile: CacheProfile,
    private val networkBoundResources: NetworkBoundResource,
) : CredentialRepository {

    override suspend fun login(params: LoginApiUseCase.Params): Flow<ApiResult<LoginApiEntity>> {
        return mapFromApiResponse(
            apiResult = networkBoundResources.downloadData {
                apiServices.login(params.mobile, params.password)
            },
            mapper = loginApiMapper
        )
    }

    override suspend fun logout(): Flow<ApiResult<LogoutApiEntity>> {
        return mapFromApiResponse(
            apiResult = networkBoundResources.downloadData {
                apiServices.logout()
            },
            mapper = logoutApiMapper
        )
    }


    override suspend fun fetchProfile(): Flow<ApiResult<UserDetailsApiEntity>> {
        return mapFromApiResponse(
            apiResult = networkBoundResources.downloadData {
                apiServices.fetchUserProfileData()
            },
            mapper = profileApiMapper
        ).map {
            if (it is ApiResult.Success) cacheProfile.cacheProfileData(it.data)
            it
        }
    }
}