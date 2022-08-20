package co.example.domain.usecase.remote.credential

import co.example.domain.base.*
import co.example.domain.repository.remote.credential.CredentialRepository
import co.example.entity.credential.LoginApiEntity
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import javax.inject.Inject

class LoginApiUseCase @Inject constructor(
    private val credentialRepository: CredentialRepository,
    private val ioValidation: IoValidation
): ApiUseCase<LoginApiUseCase.Params, LoginApiEntity> {
    val ioError = Channel<LoginIoResult>()


    override suspend fun execute(params: Params): Flow<ApiResult<LoginApiEntity>> {
        return when (val result = ioValidation.loginDataValidation(params)) {
            is ValidationResultV2.Failure<*> -> {
                ioError.send(result.ioErrorResult as LoginIoResult)
                emptyFlow()
            }
            is ValidationResultV2.Success -> credentialRepository.login(params)
        }
    }

    data class Params(val mobile:String, val password:String)

}

