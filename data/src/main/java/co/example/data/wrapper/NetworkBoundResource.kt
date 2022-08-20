package co.example.data.wrapper

import co.example.domain.base.ApiResult
import com.google.gson.JsonParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException
import java.net.SocketTimeoutException
import javax.inject.Inject


class NetworkBoundResource @Inject constructor(){
    private  val ioDispatcher: CoroutineDispatcher = Dispatchers.IO

    suspend fun<ResultType> downloadData(api : suspend () -> Response<ResultType>):Flow<ApiResult<ResultType>>{
        return withContext(ioDispatcher) {
            flow {
                emit(ApiResult.Loading(true))
                val response:Response<ResultType> = api.invoke()
                emit(ApiResult.Loading(false))
                if (response.isSuccessful){
                    response.body()?.let {
                        emit(ApiResult.Success(data = it))
                    }?: emit(ApiResult.Error(message = ERROR_UNKNOWN, code = 0))
                }else{
                    emit(ApiResult.Error(message = parserErrorBody(response.errorBody()),
                        code = response.code()))
                }
            }.catch { exception->
                emit(ApiResult.Loading(false))
                delay(1)
                emit(ApiResult.Error(getExceptionMessage(exception),getExceptionCode(exception)))
            }
        }
    }

    private fun parserErrorBody(response: ResponseBody?): String {
        return response?.let {
            val errorMessage = JsonParser.parseString(it.string()).asJsonObject["message"].asString
            errorMessage.ifEmpty { ERROR_SOMETHING_WENT_WRONG }
            errorMessage
        } ?: ERROR_UNKNOWN
    }
    private fun getExceptionMessage(throwable: Throwable?): String {
        when (throwable) {
            is SocketTimeoutException -> return ERROR_CONNECTION_TIMEOUT
            is IOException -> return ERROR_NO_INTERNET
            is HttpException -> return try {
                val errorJsonString = throwable.response()?.errorBody()?.string()
                val errorMessage =
                    JsonParser.parseString(errorJsonString).asJsonObject["message"].asString
                errorMessage.ifEmpty { ERROR_SOMETHING_WENT_WRONG }
            } catch (e: Exception) {
                e.printStackTrace()
                ERROR_UNKNOWN
            }
        }
        return ERROR_UNKNOWN
    }
    private fun getExceptionCode(throwable: Throwable?): Int {
        return if (throwable is HttpException) (throwable).code()
        else 0
    }
}
const val ERROR_NO_INTERNET = "No internet connection, please try again!"
const val ERROR_CONNECTION_TIMEOUT = "Connection time out, please try again!"
const val ERROR_UNKNOWN = "Unknown error occurred, please try again!"
const val ERROR_SOMETHING_WENT_WRONG = "Whoops! Something went wrong"