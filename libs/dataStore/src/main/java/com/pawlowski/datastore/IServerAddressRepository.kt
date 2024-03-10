package com.pawlowski.datastore

interface IServerAddressRepository {
    suspend fun getServerAddress(): String

    suspend fun changeServerAddress(newAddress: String)
}
