package com.pawlowski.datastore.serverAddress

import androidx.datastore.core.DataStore
import com.pawlowski.datastore.IServerAddressRepository
import com.pawlowski.datastore.ServerAddress
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class ServerAddressRepository
    @Inject
    constructor(
        private val dataStore: DataStore<ServerAddressDataStoreModel>,
    ) : IServerAddressRepository {
        override suspend fun getServerAddress(): ServerAddress =
            dataStore.data.first().let {
                ServerAddress(
                    url = it.address,
                    port = it.port,
                )
            }

        override suspend fun changeServerAddress(newAddress: ServerAddress) {
            dataStore.updateData {
                ServerAddressDataStoreModel(
                    address = newAddress.url,
                    port = newAddress.port,
                )
            }
        }
    }
