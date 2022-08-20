package co.example.data.apiservice

import co.example.di.authrefresh.AuthRefreshApiService
import co.example.di.authrefresh.AuthRefreshServiceHolder
import co.example.di.qualifier.AppBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiServicesModule {

    @Provides
    @Singleton
    fun provideApiServices(
        @AppBaseUrl retrofit: Retrofit,
        authRefreshServiceHolder: AuthRefreshServiceHolder
    ): ApiServices {
        authRefreshServiceHolder.setAuthRefreshApi(retrofit.create(AuthRefreshApiService::class.java))
        return retrofit.create(ApiServices::class.java)
    }
}