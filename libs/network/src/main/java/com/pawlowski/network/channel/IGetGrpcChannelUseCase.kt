package com.pawlowski.network.channel

import io.grpc.Channel

internal interface IGetGrpcChannelUseCase {
    operator fun invoke(
        url: String,
        port: Int,
    ): Channel
}
