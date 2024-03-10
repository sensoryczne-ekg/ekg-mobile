package com.pawlowski.datastore.di

import com.pawlowski.datastore.IServerAddressRepository
import com.pawlowski.datastore.serverAddress.ServerAddressRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataStoreModuleBinds {
    @Binds
    abstract fun serverAddressRepository(serverAddressRepository: ServerAddressRepository): IServerAddressRepository
}
