package com.pawlowski.ekgmonitor.ui.screens.chart

import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.network.Record

internal data class ChartState(
    val recordsResource: Resource<List<Record>>,
)

internal sealed interface ChartEvent
