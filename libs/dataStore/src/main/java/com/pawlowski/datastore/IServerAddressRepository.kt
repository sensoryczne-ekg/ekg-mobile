package com.pawlowski.datastore

interface IServerAddressRepository {
    suspend fun getServerAddress(): ServerAddress

    suspend fun changeServerAddress(newAddress: ServerAddress)
}
