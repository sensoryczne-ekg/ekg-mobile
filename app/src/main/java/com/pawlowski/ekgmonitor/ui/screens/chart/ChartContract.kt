package com.pawlowski.ekgmonitor.ui.screens.chart

import com.pawlowski.datastore.ServerAddress
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.network.EkgRecord
import kotlinx.collections.immutable.ImmutableList

internal data class ChartState(
    val recordsResource: Resource<List<EkgRecord>>,
    val indexesToShowPeeks: ImmutableList<Int>,
    val currentServerAddress: ServerAddress?,
)

internal sealed interface ChartEvent {
    data class ChangeServerAddress(val newAddress: ServerAddress) : ChartEvent

    data object HistoryClick : ChartEvent

    data object RetryClick : ChartEvent
}
