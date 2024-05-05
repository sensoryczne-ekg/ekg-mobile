package com.pawlowski.ekgmonitor.ui.screens.history

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
internal fun HistoryDestination() {
    val viewModel = hiltViewModel<HistoryViewModel>()
    HistoryScreen(
        state = viewModel.stateFlow.collectAsStateWithLifecycle().value,
        onNewEvent = viewModel::onNewEvent,
    )
}
