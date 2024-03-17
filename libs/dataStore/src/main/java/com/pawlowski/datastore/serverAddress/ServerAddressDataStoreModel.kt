package com.pawlowski.datastore.serverAddress

import kotlinx.serialization.Serializable

@Serializable
internal data class ServerAddressDataStoreModel(
    val address: String = "srv3.enteam.pl",
    val port: Int = 6001,
)
