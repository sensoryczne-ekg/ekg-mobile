package com.pawlowski.ekgmonitor.domain.useCase

import com.pawlowski.network.EkgRecord
import com.pawlowski.network.IEkgDataProvider
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class StreamRecords
    @Inject
    constructor(
        private val ekgDataProvider: IEkgDataProvider,
    ) {
        operator fun invoke(): Flow<EkgRecord> = ekgDataProvider.streamRecords()
    }
