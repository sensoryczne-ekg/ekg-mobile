package com.pawlowski.ekgmonitor.ui.components.chartNew

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
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
import androidx.compose.ui.unit.dp
import com.pawlowski.ekgmonitor.ui.components.chartNew.ChartNew.Axis
import com.pawlowski.ekgmonitor.ui.components.chartNew.ChartNew.WidthConfig
import com.pawlowski.ekgmonitor.ui.utils.formatDateTime
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.delay
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.time.Duration

interface ChartNew {
    data class Record(
        val timestamp: Long,
        val value: Int,
        val showPeek: Boolean = false,
    )

    data class Axis(
        val records: List<Record>,
        val color: Color,
    )

    sealed interface WidthConfig {
        data class Scrollable(
            val autoScroll: Boolean,
            val timePerWidth: Duration,
        ) : WidthConfig

        data object Fit : WidthConfig
    }
}

private fun WidthConfig.isAutoScroll(): Boolean = this is WidthConfig.Scrollable && autoScroll

private fun ImmutableList<Axis>.timestampRange(): Pair<Long, Long> {
    fun Pair<Long, Long>.getNewMinMax(newValue: Long): Pair<Long, Long> = min(first, newValue) to max(second, newValue)

    fun Pair<Long, Long>.getNewMinMax(newRange: Pair<Long, Long>): Pair<Long, Long> =
        min(first, newRange.first) to max(second, newRange.second)

    val axissesRanges =
        map {
            it.records.fold(initial = Long.MAX_VALUE to 0L) { currentRange, newRecord ->
                currentRange.getNewMinMax(newRecord.timestamp)
            }
        }

    return axissesRanges.fold(initial = Long.MAX_VALUE to 0L) { currentRange, newRange ->
        currentRange.getNewMinMax(newRange)
    }
}

private fun ImmutableList<Axis>.maxValue() =
    maxOf {
        if (it.records.isNotEmpty()) {
            it.records.maxOf { it.value }.coerceAtLeast(minimumValue = 0) + 5
        } else {
            5
        }
    }

@Composable
fun ChartNew(
    axisses: ImmutableList<Axis>,
    widthConfig: WidthConfig,
) {
    val axisTimestampRange =
        remember(axisses) {
            axisses.timestampRange()
        }

    val scrollOffset =
        remember {
            mutableFloatStateOf(0f)
        }

    Box(
        modifier =
            Modifier
                .fillMaxWidth()
                .scrollableModifierOrNot(
                    axisTimestampRange = axisTimestampRange,
                    widthConfig = widthConfig,
                    scrollOffset = scrollOffset,
                ),
    ) {
        ChartInternal(
            axisses = axisses,
            widthConfig = widthConfig,
            axisTimestampRange = axisTimestampRange,
            translateOffset = scrollOffset::value,
        )
    }
}

private fun Modifier.scrollableModifierOrNot(
    scrollOffset: MutableFloatState,
    axisTimestampRange: Pair<Long, Long>,
    widthConfig: WidthConfig,
): Modifier =
    composed {
        if (widthConfig is WidthConfig.Scrollable) {
            val maxScrollAvailable =
                rememberUpdatedState(
                    newValue =
                        getMaxScrollAvailable(
                            minTimestamp = axisTimestampRange.first,
                            maxTimestamp = axisTimestampRange.second,
                            millisecondsPerWidth = widthConfig.timePerWidth.inWholeMilliseconds,
                        ),
                )

            val scrollState =
                rememberScrollableState { delta ->
                    scrollOffset.floatValue =
                        (scrollOffset.floatValue + delta)
                            .coerceAtLeast(minimumValue = -maxScrollAvailable.value)
                            .coerceAtMost(maximumValue = 0f)
                    delta
                }

            LaunchedEffect(key1 = widthConfig, scrollState, scrollOffset) {
                if (widthConfig.isAutoScroll()) {
                    while (true) {
                        if (!scrollState.isScrollInProgress && widthConfig.isAutoScroll()) {
                            runCatching {
                                val scrollByValue =
                                    -abs(scrollOffset.floatValue - maxScrollAvailable.value)
                                scrollState.scrollBy(scrollByValue)
                            }
                        }
                        delay(10)
                    }
                }
            }

            remember(scrollState) {
                Modifier.scrollable(
                    state = scrollState,
                    orientation = Orientation.Horizontal,
                )
            }
        } else {
            this
        }
    }

@Composable
private fun getMaxScrollAvailable(
    minTimestamp: Long,
    maxTimestamp: Long,
    millisecondsPerWidth: Long,
): Float {
    // TODO: Move to layout phase?
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current

    val diff = maxTimestamp - minTimestamp
    val scaleX = with(density) { screenWidth.toPx() } / millisecondsPerWidth

    return (diff * scaleX) - with(density) { screenWidth.toPx() }
}

@Composable
private fun ChartInternal(
    axisses: ImmutableList<Axis>,
    axisTimestampRange: Pair<Long, Long>,
    widthConfig: WidthConfig,
    translateOffset: () -> Float,
) {
    val (minTimestamp, maxTimestamp) = axisTimestampRange
    val maxValue =
        remember(axisses) {
            axisses.maxValue()
        }

    val screenWidth = LocalConfiguration.current.screenWidthDp.dp
    val density = LocalDensity.current
    val textMeasurer = rememberTextMeasurer()

    val scaleX =
        when (widthConfig) {
            is WidthConfig.Scrollable -> with(density) { screenWidth.toPx() } / widthConfig.timePerWidth.inWholeMilliseconds
            WidthConfig.Fit -> with(density) { screenWidth.toPx() } / (maxTimestamp - minTimestamp)
        }
    Canvas(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(300.dp),
    ) {
        val scaleY = size.height / maxValue

        val scaledRecords =
            axisses.toScaledOffsets(
                minTimestamp = minTimestamp,
                scaleX = scaleX,
                scaleY = scaleY,
                canvasHeight = size.height,
            )

        drawHorizontalHelperLines(
            maxHelperValue = maxValue.toLong(),
            scaleY = scaleY,
            textMeasurer = textMeasurer,
        )

        translate(left = translateOffset()) {
            scaledRecords.forEach { (points, color, peeks) ->
                drawRecordsPath(
                    recordsPoints = points,
                    color = color,
                )
                drawPeeks(
                    peekPoints = peeks,
                    color = Color.Green,
                )
            }
            (widthConfig as? WidthConfig.Scrollable)?.let {
                drawTimestampsLabels(
                    maxTimestamp = maxTimestamp,
                    scaleX = scaleX,
                    textMeasurer = textMeasurer,
                    canvasHeight = size.height,
                    minTimestamp = minTimestamp,
                    timeDistanceBetweenLabels = widthConfig.timePerWidth,
                )
            }
        }
    }
}

private data class AxisOffsetsAndColor(
    val offsets: ImmutableList<Offset>,
    val color: Color,
    val peeks: ImmutableList<Offset>,
)

private fun ImmutableList<Axis>.toScaledOffsets(
    minTimestamp: Long,
    scaleX: Float,
    scaleY: Float,
    canvasHeight: Float,
): ImmutableList<AxisOffsetsAndColor> {
    fun ChartNew.Record.toOffset() =
        Offset(
            x = (timestamp - minTimestamp) * scaleX,
            y = (canvasHeight - (value * scaleY)),
        )

    return map { axis ->
        AxisOffsetsAndColor(
            offsets =
                axis.records.map { record ->
                    record.toOffset()
                }.toPersistentList(),
            color = axis.color,
            peeks =
                axis.records.filter { it.showPeek }.map { record ->
                    record.toOffset()
                }.toPersistentList(),
        )
    }.toPersistentList()
}

private fun DrawScope.drawTimestampsLabels(
    minTimestamp: Long,
    maxTimestamp: Long,
    scaleX: Float,
    textMeasurer: TextMeasurer,
    canvasHeight: Float,
    timeDistanceBetweenLabels: Duration,
) {
    var count = 1
    while (true) {
        val step = timeDistanceBetweenLabels.inWholeMilliseconds * count++
        val timestampOfStep = (step + minTimestamp)
        if (timestampOfStep <= maxTimestamp) {
            val textLayoutResult =
                textMeasurer.measure(
                    text =
                        buildAnnotatedString {
                            append(
                                Instant.fromEpochMilliseconds(
                                    timestampOfStep,
                                ).toLocalDateTime(timeZone = TimeZone.currentSystemDefault()).formatDateTime(),
                            )
                        },
                )

            drawText(
                textLayoutResult = textLayoutResult,
                topLeft =
                    Offset(
                        x = step * scaleX - textLayoutResult.size.width / 2.0f,
                        y = canvasHeight - textLayoutResult.size.height,
                    ),
            )
        } else {
            break
        }
    }
}

private fun DrawScope.drawRecordsPath(
    recordsPoints: List<Offset>,
    color: Color,
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
        color = color,
        style =
            Stroke(
                width = 3.dp.toPx(),
            ),
    )
}

private fun DrawScope.drawPeeks(
    peekPoints: List<Offset>,
    color: Color,
) {
    peekPoints.forEach {
        drawLine(
            start =
                Offset(
                    x = it.x,
                    y = 0f,
                ),
            end =
                Offset(
                    x = it.x,
                    y = size.height,
                ),
            color = color,
            strokeWidth = 1.dp.toPx(),
        )
    }
}

private fun DrawScope.drawHorizontalHelperLines(
    maxHelperValue: Long,
    scaleY: Float,
    textMeasurer: TextMeasurer,
) {
    val step = 100
    val linesCount = (maxHelperValue / step).toInt()
    repeat(linesCount) {
        val lineY = it * step
        val lineYScaled = lineY * scaleY
        val lineYSwapped = size.height - lineYScaled

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
