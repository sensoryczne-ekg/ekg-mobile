package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
internal fun ChartDestination() {
    val viewModel = hiltViewModel<ChartViewModel>()
    ChartScreen(
        state = viewModel.stateFlow.collectAsState().value,
        onNewEvent = viewModel::onNewEvent,
    )
}
