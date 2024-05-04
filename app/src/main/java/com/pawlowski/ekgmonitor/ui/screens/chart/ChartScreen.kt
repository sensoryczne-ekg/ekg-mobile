package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.ekgmonitor.ui.screens.settings.ChangeNetworkBottomSheet
import com.pawlowski.network.EkgRecord
import kotlinx.collections.immutable.toPersistentList

@Composable
internal fun ChartScreen(
    state: ChartState,
    onNewEvent: (ChartEvent) -> Unit,
) {
    Column {
        var showChangeNetworkBottomSheet by remember {
            mutableStateOf(false)
        }
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            IconButton(
                onClick = { showChangeNetworkBottomSheet = true },
            ) {
                Icon(Icons.Rounded.Settings, contentDescription = "")
            }
            IconButton(
                onClick = { onNewEvent(ChartEvent.HistoryClick) },
            ) {
                Icon(
                    imageVector = Icons.Filled.History,
                    contentDescription = null,
                )
            }
        }

        state.currentServerAddress?.let {
            ChangeNetworkBottomSheet(
                show = showChangeNetworkBottomSheet,
                onDismiss = { showChangeNetworkBottomSheet = false },
                initialAddress = state.currentServerAddress,
                onConfirm = {
                    onNewEvent(ChartEvent.ChangeServerAddress(it))
                    showChangeNetworkBottomSheet = false
                },
            )
        }

        when (state.recordsResource) {
            is Resource.Success -> {
                LiveChart(records = state.recordsResource.data.toPersistentList())
            }
            is Resource.Loading -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    CircularProgressIndicator()
                }
            }
            is Resource.Error -> {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize(),
                ) {
                    Text(text = "Error")
                }
            }
        }
    }
}

@Preview(
    showBackground = true,
)
@Composable
private fun ChartScreenPreview() {
    ChartScreen(
        state =
            ChartState(
                recordsResource =
                    Resource.Success(
                        (1..5000L)
                            .filter {
                                (it % 8) == 0L
                            }
                            .map {
                                EkgRecord(
                                    id = 1,
                                    value = it / 20,
                                    timestamp = it,
                                )
                            },
                    ),
                currentServerAddress = null,
            ),
    ) {}
}
