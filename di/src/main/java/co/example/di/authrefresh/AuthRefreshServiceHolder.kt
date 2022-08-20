package co.example.di.authrefresh

import retrofit2.Call
import retrofit2.http.GET


data class RefreshTokenApiResponse(val token:String)
interface AuthRefreshApiService{
    @GET("/v1/refresh-token")
    fun refreshToken(): Call<RefreshTokenApiResponse>
}

class AuthRefreshServiceHolder{
    private var authRefreshApi: AuthRefreshApiService? = null
    fun getAuthRefreshApi(): AuthRefreshApiService? {
        return authRefreshApi
    }

    fun setAuthRefreshApi(authRefreshApi: AuthRefreshApiService) {
        this.authRefreshApi = authRefreshApi
    }
}