package com.pawlowski.datastore.serverAddress

import com.pawlowski.datastore.KotlinxJsonSerializer
import javax.inject.Inject

internal class ServerAddressSerializer
    @Inject
    constructor() :
    KotlinxJsonSerializer<ServerAddressDataStoreModel>(
            kSerializer = ServerAddressDataStoreModel.serializer(),
        ) {
        override val defaultValue: ServerAddressDataStoreModel =
            ServerAddressDataStoreModel(
                address = "srv3.enteam.pl",
            )
    }
