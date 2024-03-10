package com.pawlowski.network.service

import ElectrocardiogramGrpcKt
import com.pawlowski.datastore.IServerAddressRepository
import com.pawlowski.network.channel.IGetGrpcChannelUseCase
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class EkgServiceProvider
    @Inject
    constructor(
        private val getGrpcChannelUseCase: IGetGrpcChannelUseCase,
        private val serverAddressRepository: IServerAddressRepository,
    ) : IEkgServiceProvider {
        private var service: ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub? = null

        private var lastServerUrl: String? = null

        private val mutex = Mutex()

        override suspend operator fun invoke(): ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub =
            mutex.withLock {
                val currentServerUrl = serverAddressRepository.getServerAddress()
                val lastService = service
                if (lastServerUrl != currentServerUrl || lastService == null) {
                    getGrpcChannelUseCase(url = "srv3.enteam.pl").let { channel ->
                        ElectrocardiogramGrpcKt.ElectrocardiogramCoroutineStub(channel)
                    }.also {
                        service = it
                        lastServerUrl = currentServerUrl
                    }
                } else {
                    lastService
                }
            }
    }
