package com.pawlowski.ekgmonitor.ui.screens.history

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.ekgmonitor.ui.components.chartNew.ChartNew
import com.pawlowski.ekgmonitor.ui.components.errorItem.ErrorItem
import com.pawlowski.network.EkgRecord
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HistoryScreen(
    state: HistoryState,
    onNewEvent: (HistoryEvent) -> Unit,
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TopAppBar(
            title = { Text(text = "Historyczne pomiary") },
            navigationIcon = {
                IconButton(onClick = { onNewEvent(HistoryEvent.BackClick) }) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
        )

        when (state.recordsResource) {
            is Resource.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }
            }

            is Resource.Error -> {
                ErrorItem(
                    onRetryClick = { onNewEvent(HistoryEvent.RetryClick) },
                )
            }
            is Resource.Success -> {
                if (state.recordsResource.data.isNotEmpty()) {
                    HistoryChart(records = state.recordsResource.data)
                } else {
                    EmptyState()
                }
            }
        }
    }
}

@Composable
private fun HistoryChart(records: ImmutableList<EkgRecord>) {
    val ekgRecords =
        remember(records) {
            records.map {
                ChartNew.Record(
                    timestamp = it.timestamp,
                    value = it.value.toInt(),
                )
            }
        }
    ChartNew(
        axisses =
            persistentListOf(
                ChartNew.Axis(
                    ekgRecords,
                    color = Color.Red,
                ),
            ),
        widthConfig =
            ChartNew.WidthConfig.Scrollable(
                autoScroll = false,
                timePerWidth = 5.seconds,
            ),
    )
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = "Nie ma pomiarów dla podanych kryteriów")
    }
}
