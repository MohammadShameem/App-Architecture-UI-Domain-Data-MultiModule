package co.example.uidomaindatamultimodule.di.module

import co.example.data.repoimpl.CredentialRepoImpl
import co.example.data.repoimpl.RemoteRepoImpl
import co.example.data.repoimpl.DatabaseRepoImpl
import co.example.domain.repository.remote.credential.CredentialRepository
import co.example.domain.repository.remote.RemoteRepository
import co.example.domain.repository.local.DatabaseRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {
    @Binds
    fun bindCredentialRepository(credentialRepoImpl: CredentialRepoImpl): CredentialRepository

    @Binds
    fun bindRemoteRepository(remoteRepository: RemoteRepoImpl): RemoteRepository

    @Binds
    fun bindLocalRepository(localRepoImpl: DatabaseRepoImpl): DatabaseRepository


}