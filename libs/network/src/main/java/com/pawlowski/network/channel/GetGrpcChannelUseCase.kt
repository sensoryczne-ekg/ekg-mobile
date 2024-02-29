package com.pawlowski.network.channel

import android.app.Application
import io.grpc.Channel
import io.grpc.android.AndroidChannelBuilder
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class GetGrpcChannelUseCase
    @Inject
    constructor(
        private val context: Application,
    ) : IGetGrpcChannelUseCase {
        override operator fun invoke(): Channel =
            AndroidChannelBuilder
                .forAddress("srv3.enteam.pl", 3010)
                .build()
    }
