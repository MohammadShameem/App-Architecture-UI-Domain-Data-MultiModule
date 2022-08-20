package co.example.uidomaindatamultimodule

import co.example.di.qualifier.AppBaseUrl
import co.example.di.qualifier.AppImageBaseUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class BaseUrlModule{
    @Provides
    @AppBaseUrl
    fun provideBaseUrl():String = ""

    @Provides
    @AppImageBaseUrl
    fun provideImageBaseUrl():String = ""
}

