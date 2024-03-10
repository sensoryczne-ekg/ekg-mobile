package com.pawlowski.network.channel

import android.app.Application
import io.grpc.Channel
import io.grpc.android.AndroidChannelBuilder
import javax.inject.Inject

internal class GetGrpcChannelUseCase
    @Inject
    constructor(
        private val context: Application,
    ) : IGetGrpcChannelUseCase {
        override operator fun invoke(url: String): Channel =
            AndroidChannelBuilder
                .forAddress(url, 6001)
                .context(context)
                .usePlaintext()
                .build()
    }
