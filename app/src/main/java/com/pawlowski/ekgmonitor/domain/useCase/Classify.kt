package com.pawlowski.ekgmonitor.domain.useCase

import com.pawlowski.network.IEkgDataProvider
import javax.inject.Inject

class Classify
    @Inject
    constructor(
        private val dataProvider: IEkgDataProvider,
    ) {
        suspend operator fun invoke(
            from: Long,
            to: Long,
        ) = dataProvider
            .classify(
                from = from,
                to = to,
            )
    }
