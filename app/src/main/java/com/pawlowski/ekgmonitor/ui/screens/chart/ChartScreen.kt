package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.ekgmonitor.ui.screens.settings.ChangeNetworkBottomSheet
import com.pawlowski.network.Record
import kotlinx.coroutines.delay
import kotlin.math.abs

@Composable
internal fun ChartScreen(
    state: ChartState,
    onNewEvent: (ChartEvent) -> Unit,
) {
    Column {
        var showChangeNetworkBottomSheet by remember {
            mutableStateOf(false)
        }
        IconButton(
            onClick = { showChangeNetworkBottomSheet = true },
            modifier = Modifier.align(Alignment.CenterHorizontally),
        ) {
            Icon(Icons.Rounded.Settings, contentDescription = "")
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
                Column(
                    verticalArrangement = Arrangement.spacedBy(space = 16.dp),
                ) {
                    val isAutoScrolling =
                        remember {
                            mutableStateOf(true)
                        }
                    FilterChip(
                        selected = isAutoScrolling.value,
                        onClick = { isAutoScrolling.value = !isAutoScrolling.value },
                        label = { Text(text = "Śledzenie") },
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                    )

                    val scrollOffset =
                        remember {
                            mutableFloatStateOf(0f)
                        }

                    val maxScrollAvailable = state.recordsResource.data.maxScrollAvailable()

                    val scrollState =
                        rememberScrollableState { delta ->
                            scrollOffset.floatValue =
                                (scrollOffset.floatValue + delta)
                                    .coerceAtLeast(minimumValue = -maxScrollAvailable)
                                    .coerceAtMost(maximumValue = 0f)
                            delta
                        }

                    LaunchedEffect(key1 = Unit) {
                        while (true) {
                            if (!scrollState.isScrollInProgress && isAutoScrolling.value) {
                                runCatching {
                                    scrollState.scrollBy(-(abs(scrollOffset.floatValue) - maxScrollAvailable))
                                }
                            }
                            delay(10)
                        }
                    }

                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .scrollable(
                                    state = scrollState,
                                    orientation = Orientation.Horizontal,
                                ),
                    ) {
                        Chart(
                            records = state.recordsResource.data,
                            colors = ChartColors(),
                            translateOffset = scrollOffset::value,
                        )
                    }
                }
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

@Composable
private fun List<Record>.maxScrollAvailable(): Float {
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    val minTimestamp = minOf { it.timestamp }
    val maxTimestamp = maxOf { it.timestamp }
    val diff = maxTimestamp - minTimestamp
    val scaleX = with(density) { screenWidth.toPx() } / WIDTH_TIMESTAMP

    return (diff * scaleX) - with(density) { screenWidth.toPx() }
}

data class ChartColors(
    val lineColor: Color = Color(0xFFF94144),
)

const val WIDTH_TIMESTAMP = 5000

@Composable
private fun Chart(
    records: List<Record>,
    translateOffset: () -> Float,
    colors: ChartColors,
) {
    val minTimestamp = records.minOf { it.timestamp }
    val maxValue = records.maxOf { it.value }.coerceAtLeast(minimumValue = 1000) + 50
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val scaleX = with(density) { screenWidth.toPx() } / WIDTH_TIMESTAMP
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(300.dp),
    ) {
        val scaleY = size.height / maxValue

        val scaledRecords =
            records.map {
                Offset(
                    x = (it.timestamp - minTimestamp) * scaleX,
                    y = (size.height - (it.value * scaleY)),
                )
            }

        drawHorizontalHelperLines(
            maxValue = maxValue * scaleY,
            textMeasurer = textMeasurer,
        )

        translate(left = translateOffset()) {
            drawRecordsPath(
                recordsPoints = scaledRecords,
                colors = colors,
            )
        }
    }
}

private fun DrawScope.drawRecordsPath(
    recordsPoints: List<Offset>,
    colors: ChartColors,
) {
    val path =
        Path().apply {
            val firstPoint = recordsPoints.first()
            moveTo(
                x = firstPoint.x,
                y = firstPoint.y,
            )
            recordsPoints.drop(1).forEach { record ->
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

private fun DrawScope.drawHorizontalHelperLines(
    maxValue: Float,
    textMeasurer: TextMeasurer,
) {
    val step = 100
    val linesCount = (maxValue / step).toInt()
    repeat(linesCount) {
        val lineY = it * step
        val lineYSwapped = size.height - lineY

        val textLayoutResult =
            textMeasurer.measure(
                text =
                    buildAnnotatedString {
                        append(lineY.toString())
                    },
            )
        drawText(
            textLayoutResult = textLayoutResult,
            topLeft =
                Offset(
                    x = 0f,
                    y = lineYSwapped - textLayoutResult.size.height,
                ),
        )

        drawLine(
            start =
                Offset(
                    x = 0f,
                    y = lineYSwapped,
                ),
            end =
                Offset(
                    x = size.width,
                    y = lineYSwapped,
                ),
            color = Color.Gray,
            strokeWidth = 1f,
            pathEffect = PathEffect.dashPathEffect(intervals = floatArrayOf(10f, 5f)),
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
                currentServerAddress = null,
            ),
    ) {}
}
