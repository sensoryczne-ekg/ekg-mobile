package com.pawlowski.datastore.di

import android.app.Application
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.pawlowski.datastore.serverAddress.ServerAddressDataStoreModel
import com.pawlowski.datastore.serverAddress.ServerAddressSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val SERVER_ADDRESS_DATA_STORE_FILE_NAME = "serverAddressDataStore"

@Module
@InstallIn(SingletonComponent::class)
internal object DataStoreModule {
    @Provides
    @Singleton
    fun serverAddressDataStore(
        authTokenSerializer: ServerAddressSerializer,
        application: Application,
    ): DataStore<ServerAddressDataStoreModel> =
        DataStoreFactory.create(
            serializer = authTokenSerializer,
            produceFile = {
                application.dataStoreFile(fileName = SERVER_ADDRESS_DATA_STORE_FILE_NAME)
            },
        )
}
