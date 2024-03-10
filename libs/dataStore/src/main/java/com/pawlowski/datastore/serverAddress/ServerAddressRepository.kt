package com.pawlowski.datastore.serverAddress

import androidx.datastore.core.DataStore
import com.pawlowski.datastore.IServerAddressRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class ServerAddressRepository
    @Inject
    constructor(
        private val dataStore: DataStore<ServerAddressDataStoreModel>,
    ) : IServerAddressRepository {
        override suspend fun getServerAddress(): String = dataStore.data.first().address

        override suspend fun changeServerAddress(newAddress: String) {
            dataStore.updateData {
                ServerAddressDataStoreModel(address = newAddress)
            }
        }
    }
