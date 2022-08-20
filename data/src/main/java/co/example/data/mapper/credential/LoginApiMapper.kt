package co.example.data.mapper.credential

import co.example.apiresponse.credential.LoginApiResponse
import co.example.data.mapper.Mapper
import co.example.entity.credential.LoginApiEntity
import javax.inject.Inject

class LoginApiMapper @Inject constructor() : Mapper<LoginApiResponse, LoginApiEntity> {
    override fun mapFromApiResponse(type: LoginApiResponse) =
        LoginApiEntity(message = type.message ?: "", token = type.token ?: "")
}