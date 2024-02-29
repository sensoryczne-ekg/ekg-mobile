package com.pawlowski.network.di

import com.pawlowski.network.IEkgDataProvider
import com.pawlowski.network.channel.GetGrpcChannelUseCase
import com.pawlowski.network.channel.IGetGrpcChannelUseCase
import com.pawlowski.network.dataProvider.EkgDataProvider
import com.pawlowski.network.service.EkgServiceProvider
import com.pawlowski.network.service.IEkgServiceProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
internal abstract class NetworkModuleBinds {
    @Binds
    abstract fun getGrpcChannelUseCase(getGrpcChannelUseCase: GetGrpcChannelUseCase): IGetGrpcChannelUseCase

    @Binds
    abstract fun ekgServiceProvider(ekgServiceProvider: EkgServiceProvider): IEkgServiceProvider

    @Binds
    abstract fun ekgDataProvider(ekgDataProvider: EkgDataProvider): IEkgDataProvider
}
