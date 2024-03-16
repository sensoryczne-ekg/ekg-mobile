package com.pawlowski.ekgmonitor.ui.screens.chart

import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.network.Record

internal data class ChartState(
    val recordsResource: Resource<List<Record>>,
    val currentServerAddress: String?,
)

internal sealed interface ChartEvent {
    data class ChangeNetwork(val newNetwork: String) : ChartEvent
}
