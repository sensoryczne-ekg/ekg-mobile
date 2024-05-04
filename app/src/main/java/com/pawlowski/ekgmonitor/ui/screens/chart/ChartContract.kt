package com.pawlowski.ekgmonitor.ui.screens.chart

import com.pawlowski.datastore.ServerAddress
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.network.EkgRecord

internal data class ChartState(
    val recordsResource: Resource<List<EkgRecord>>,
    val currentServerAddress: ServerAddress?,
)

internal sealed interface ChartEvent {
    data class ChangeServerAddress(val newAddress: ServerAddress) : ChartEvent
}
