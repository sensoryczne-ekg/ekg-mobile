package com.pawlowski.datastore.di

import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import com.pawlowski.datastore.serverAddress.ServerAddressDataStoreModel
import com.pawlowski.datastore.serverAddress.ServerAddressSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

private const val SERVER_ADDRESS_DATA_STORE_FILE_NAME = "serverAddressDataStore"

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {
    @Provides
    @Singleton
    fun serverAddressDataStore(authTokenSerializer: ServerAddressSerializer): DataStore<ServerAddressDataStoreModel> =
        DataStoreFactory.create(
            serializer = authTokenSerializer,
            produceFile = {
                File(SERVER_ADDRESS_DATA_STORE_FILE_NAME)
            },
        )
}
