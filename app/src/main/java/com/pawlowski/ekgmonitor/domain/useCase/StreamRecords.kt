package com.pawlowski.ekgmonitor.domain.useCase

import com.pawlowski.network.IEkgDataProvider
import com.pawlowski.network.Record
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class StreamRecords
    @Inject
    constructor(
        private val ekgDataProvider: IEkgDataProvider,
    ) {
        operator fun invoke(): Flow<Record> = ekgDataProvider.streamRecords()
    }
