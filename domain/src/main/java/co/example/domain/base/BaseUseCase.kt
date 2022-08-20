package co.example.domain.base

import kotlinx.coroutines.flow.Flow


interface BaseUseCase

interface ApiUseCase<Params, Type> : BaseUseCase {
    suspend fun execute(params: Params): Flow<ApiResult<Type>>
}

interface ApiUseCaseNonParams<Type> : BaseUseCase {
    suspend fun execute(): Flow<ApiResult<Type>>
}

interface RoomUseCaseNonParams<Type> : BaseUseCase {
    suspend fun execute(): Flow<Type>
}
interface RoomUseCaseParamsNoReturn<Params> : BaseUseCase {
    suspend fun execute(params: Params)
}

interface RoomUseCaseParams<Params,Type> : BaseUseCase {
    suspend fun execute(params: Params): Flow<Type>
}

interface RoomUseCaseNonParamsNoReturn : BaseUseCase {
    suspend fun execute()
}
