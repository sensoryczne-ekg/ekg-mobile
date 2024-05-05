package com.pawlowski.ekgmonitor.ui.screens.history

import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.network.EkgRecord
import kotlinx.collections.immutable.ImmutableList

data class HistoryState(
    val recordsResource: Resource<ImmutableList<EkgRecord>>,
)

sealed interface HistoryEvent {
    data object BackClick : HistoryEvent

    data object RetryClick : HistoryEvent
}
