package com.pawlowski.ekgmonitor.domain.useCase

import com.pawlowski.network.EkgRecord
import com.pawlowski.network.IEkgDataProvider
import javax.inject.Inject

internal class GetRecords
    @Inject
    constructor(
        private val ekgDataProvider: IEkgDataProvider,
    ) {
        suspend operator fun invoke(
            from: Long,
            to: Long,
        ): List<EkgRecord> =
            ekgDataProvider.getRecords(
                from = from,
                to = to,
            )
    }
