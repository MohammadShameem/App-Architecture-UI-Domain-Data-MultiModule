package co.example.domain.base

import kotlinx.coroutines.flow.Flow

interface CoroutineUseCase<Params, Type>:BaseUseCase{
    suspend fun execute(params: Params? = null):Type
}

interface LocalLiveDataUseCase <Params, Type>:BaseUseCase{
    fun execute(params: Params? = null):Type
}

interface FlowUseCase <Params, Type>:BaseUseCase{
    fun execute(params: Params? = null):Flow<Type>
}