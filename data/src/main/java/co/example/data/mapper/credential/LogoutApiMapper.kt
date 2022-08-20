package co.example.data.mapper.credential


import co.example.apiresponse.credential.LogoutApiResponse
import co.example.data.mapper.Mapper
import co.example.entity.credential.LogoutApiEntity
import javax.inject.Inject


class LogoutApiMapper @Inject constructor() : Mapper<LogoutApiResponse, LogoutApiEntity> {
    override fun mapFromApiResponse(type: LogoutApiResponse): LogoutApiEntity {
        return LogoutApiEntity(
            code = type.code,
            message = type.message,
            status = type.status
        )
    }
}
