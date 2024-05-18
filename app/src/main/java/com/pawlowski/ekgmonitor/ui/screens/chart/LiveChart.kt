package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.pawlowski.ekgmonitor.ui.components.chartNew.ChartNew
import com.pawlowski.network.EkgRecord
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf
import kotlin.time.Duration.Companion.seconds

@Composable
fun LiveChart(
    records: ImmutableList<EkgRecord>,
    indexesToShowPeeks: ImmutableList<Int>,
) {
    Column {
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

        val ekgRecords =
            remember(records) {
                records.mapIndexed { index, ekgRecord ->
                    ChartNew.Record(
                        timestamp = ekgRecord.timestamp,
                        value = ekgRecord.value.toInt(),
                        showPeek = indexesToShowPeeks.contains(index),
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
                    autoScroll = isAutoScrolling.value,
                    timePerWidth = 5.seconds,
                ),
        )
    }
}
