package com.pawlowski.network.service

import com.ekg.proto.ElectrocardiogramGrpcKt
import com.pawlowski.network.channel.IGetGrpcChannelUseCase
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EkgServiceProvider
    @Inject
    constructor(
        private val getGrpcChannelUseCase: IGetGrpcChannelUseCase,
    ) : IEkgServiceProvider {
        private val service by lazy {
            getGrpcChannelUseCase().let { channel ->
                ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub(channel)
            }
        }

        override operator fun invoke(): ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub = service
    }
