package com.pawlowski.ekgmonitor.ui.screens.choosePeriod

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.pawlowski.ekgmonitor.ui.components.timePicker.TimePickerDialog
import com.pawlowski.ekgmonitor.ui.utils.formatDate
import com.pawlowski.ekgmonitor.ui.utils.formatTime
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePeriodScreen(
    onBackClick: () -> Unit,
    onConfirmClick: (LocalDateTime, LocalDateTime) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(space = 16.dp),
    ) {
        TopAppBar(
            title = {
                Text(text = "Wybierz okres")
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
        )
        val fromChosenDate =
            remember {
                mutableStateOf<LocalDate?>(null)
            }
        val fromChosenTime =
            remember {
                mutableStateOf<LocalTime?>(null)
            }
        val toChosenDate =
            remember {
                mutableStateOf<LocalDate?>(null)
            }
        val toChosenTime =
            remember {
                mutableStateOf<LocalTime?>(null)
            }
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 32.dp),
            modifier = Modifier.padding(all = 16.dp),
        ) {
            ChooseTimeRow(
                dateLabel = "Od (data)",
                timeLabel = "Od (godzina)",
                chosenDate = fromChosenDate,
                chosenTime = fromChosenTime,
            )

            ChooseTimeRow(
                dateLabel = "Do (data)",
                timeLabel = "Do (godzina)",
                chosenDate = toChosenDate,
                chosenTime = toChosenTime,
            )
        }

        Button(
            onClick = {
                val fromChosenDateValue = fromChosenDate.value
                val fromChosenTimeValue = fromChosenTime.value
                val toChosenDateValue = toChosenDate.value
                val toChosenTimeValue = toChosenTime.value
                if (fromChosenDateValue != null && toChosenDateValue != null) {
                    val fromDateTime = LocalDateTime(fromChosenDateValue, fromChosenTimeValue ?: LocalTime.fromMillisecondOfDay(0))
                    val toDateTime = LocalDateTime(toChosenDateValue, toChosenTimeValue ?: LocalTime.fromMillisecondOfDay(0))
                    onConfirmClick(fromDateTime, toDateTime)
                }
            },
            modifier = Modifier.padding(all = 16.dp),
        ) {
            Text(text = "Pokaż wykres")
        }
    }
}

@Composable
private fun ChooseTimeRow(
    chosenDate: MutableState<LocalDate?>,
    chosenTime: MutableState<LocalTime?>,
    dateLabel: String,
    timeLabel: String,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(space = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val showChosenFromDialog = remember { mutableStateOf(false) }
        ChooseTimeCard(
            text = chosenDate.value?.formatDate() ?: "Kliknij aby wybrać",
            label = dateLabel,
            onClick = { showChosenFromDialog.value = true },
            modifier = Modifier.weight(weight = 1f),
        )
        EkgDatePicker(
            showDialog = showChosenFromDialog.value,
            onDismiss = {
                showChosenFromDialog.value = false
            },
            onConfirm = {
                showChosenFromDialog.value = false
                chosenDate.value = it
            },
        )

        val showChosenToDialog = remember { mutableStateOf(false) }
        ChooseTimeCard(
            text = chosenTime.value?.formatTime() ?: "Kliknij aby wybrać",
            label = timeLabel,
            onClick = { showChosenToDialog.value = true },
            modifier = Modifier.weight(weight = 1f),
        )
        if (showChosenToDialog.value) {
            TimePickerDialog(
                onCancel = { showChosenToDialog.value = false },
                onConfirm = {
                    showChosenToDialog.value = false
                    chosenTime.value = it
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EkgDatePicker(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LocalDate?) -> Unit,
) {
    if (showDialog) {
        val datePickerState = rememberDatePickerState()

        DatePickerDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(
                    onClick = {
                        val result =
                            datePickerState.selectedDateMillis?.let {
                                Instant.fromEpochMilliseconds(it).toLocalDateTime(timeZone = TimeZone.UTC).date
                            }
                        onConfirm(result)
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        onDismiss()
                    },
                ) {
                    Text("Cancel")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun ChooseTimeCard(
    text: String,
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.DateRange,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space = 2.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        OutlinedCard(
            shape = RoundedCornerShape(size = 4.dp),
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(space = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier =
                    Modifier.padding(
                        horizontal = 6.dp,
                        vertical = 12.dp,
                    ),
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(size = 24.dp),
                )
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}
