package com.pawlowski.ekgmonitor.ui.screens.chart

import com.pawlowski.ekgmonitor.BaseMviViewModel
import com.pawlowski.ekgmonitor.domain.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
internal class ChartViewModel
    @Inject
    constructor() : BaseMviViewModel<ChartState, ChartEvent>(
            initialState = ChartState(recordsResource = Resource.Loading),
        ) {
        override fun onNewEvent(event: ChartEvent) {
        }
    }
