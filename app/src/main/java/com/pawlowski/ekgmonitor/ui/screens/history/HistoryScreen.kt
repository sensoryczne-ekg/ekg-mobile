package com.pawlowski.ekgmonitor.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.ekgmonitor.ui.components.chartNew.ChartNew
import com.pawlowski.ekgmonitor.ui.components.errorItem.ErrorItem
import com.pawlowski.network.Classification
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
                    HistoryChart(
                        records = state.recordsResource.data,
                        classificationResource = state.classifyResource,
                        onNewEvent = onNewEvent,
                    )
                } else {
                    EmptyState()
                }
            }
        }
    }
}

@Composable
private fun HistoryChart(
    records: ImmutableList<EkgRecord>,
    classificationResource: Resource<Classification>,
    onNewEvent: (HistoryEvent) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(space = 24.dp)) {
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

        ClassificationTable(
            classificationResource = classificationResource,
            onNewEvent = onNewEvent,
        )
    }
}

@Composable
private fun ClassificationTable(
    classificationResource: Resource<Classification>,
    onNewEvent: (HistoryEvent) -> Unit,
) {
    when (classificationResource) {
        is Resource.Error ->
            ErrorItem(
                onRetryClick = { onNewEvent(HistoryEvent.RetryClick) },
            )
        Resource.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
        }
        is Resource.Success -> {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Text(text = "Klasyfikacja")
                ClassificationRow(
                    label = "n: ",
                    value = classificationResource.data.n.toString(),
                )
                ClassificationRow(
                    label = "s: ",
                    value = classificationResource.data.s.toString(),
                )
                ClassificationRow(
                    label = "v: ",
                    value = classificationResource.data.v.toString(),
                )
                ClassificationRow(
                    label = "f: ",
                    value = classificationResource.data.f.toString(),
                )
                ClassificationRow(
                    label = "q: ",
                    value = classificationResource.data.q.toString(),
                )
            }
        }
    }
}

@Composable
private fun ClassificationRow(
    label: String,
    value: String,
) {
    Row {
        Text(
            text = label,
            modifier = Modifier.weight(1f),
        )
        Text(text = value)
    }
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

@Preview(
    showBackground = true,
)
@Composable
private fun HistoryScreenPreview() {
    HistoryScreen(
        state =
            HistoryState(
                recordsResource =
                    Resource.Success(
                        persistentListOf(
                            EkgRecord(
                                id = 1,
                                timestamp = 1,
                                value = 1,
                            ),
                            EkgRecord(
                                id = 2,
                                timestamp = 2000,
                                value = 300,
                            ),
                        ),
                    ),
                classifyResource =
                    Resource.Success(
                        Classification(
                            n = 2f,
                            s = 3f,
                            v = 4f,
                            f = 5f,
                            q = 6f,
                        ),
                    ),
            ),
        onNewEvent = {},
    )
}
