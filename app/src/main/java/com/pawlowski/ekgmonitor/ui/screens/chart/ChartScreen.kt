package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
internal fun ChartScreen(
    state: ChartState,
    onNewEvent: (ChartEvent) -> Unit,
) {
    Text(text = "Chart")
}
