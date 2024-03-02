package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.network.Record

@Composable
internal fun ChartScreen(
    state: ChartState,
    onNewEvent: (ChartEvent) -> Unit,
) {
    when (state.recordsResource) {
        is Resource.Success -> {
            Chart(
                records = state.recordsResource.data,
                colors = ChartColors(),
            )
        }
        is Resource.Loading -> {
        }
        is Resource.Error -> {
        }
    }
}

data class ChartColors(
    val lineColor: Color = Color(0xFFF94144),
)

const val WIDTH_TIMESTAMP = 5000

@Composable
private fun Chart(
    records: List<Record>,
    colors: ChartColors,
) {
    val minTimestamp = records.minOf { it.timestamp }
    val maxTimestamp = records.maxOf { it.timestamp }
    val maxValue = records.maxOf { it.value }.coerceAtLeast(minimumValue = 10) + 50
    Canvas(
        modifier =
            Modifier.fillMaxSize(),
    ) {
        val scaleX = size.width / WIDTH_TIMESTAMP
        val scaleY = size.height / maxValue

        val scaledRecords =
            records.map {
                Offset(
                    x = (it.timestamp - minTimestamp) * scaleX,
                    y = (size.height - (it.value * scaleY)),
                )
            }

        val path =
            Path().apply {
                val firstPoint = scaledRecords.first()
                moveTo(
                    x = firstPoint.x,
                    y = firstPoint.y,
                )
                scaledRecords.drop(1).forEach { record ->
                    lineTo(
                        x = record.x,
                        y = record.y,
                    )
                }
            }
        drawPath(
            path = path,
            color = colors.lineColor,
            style =
                Stroke(
                    width = 3.dp.toPx(),
                ),
        )
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
                                Record(
                                    id = 1,
                                    value = it / 20,
                                    timestamp = it,
                                )
                            },
                    ),
            ),
    ) {}
}
