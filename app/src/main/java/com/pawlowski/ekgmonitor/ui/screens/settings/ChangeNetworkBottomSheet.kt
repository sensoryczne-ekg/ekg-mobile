package com.pawlowski.ekgmonitor.ui.screens.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.pawlowski.datastore.ServerAddress
import com.pawlowski.ekgmonitor.ui.BaseBottomSheet

@Composable
internal fun ChangeNetworkBottomSheet(
    show: Boolean,
    onDismiss: () -> Unit,
    initialAddress: ServerAddress,
    onConfirm: (ServerAddress) -> Unit,
) {
    BaseBottomSheet(show = show, onDismiss = onDismiss) {
        Column(
            verticalArrangement = Arrangement.spacedBy(space = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier.fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(bottom = 15.dp)
                    .padding(horizontal = 16.dp),
        ) {
            Text(
                text = "Edytuj adres serwera",
                style = MaterialTheme.typography.titleMedium,
            )
            val showErrorIfAny =
                remember {
                    mutableStateOf(false)
                }
            val urlState =
                remember(initialAddress) {
                    mutableStateOf(initialAddress.url)
                }
            TextField(
                value = urlState.value,
                label = {
                    Text(text = "Url serwera")
                },
                onValueChange = {
                    urlState.value = urlState.value
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                    ),
                singleLine = true,
                isError = urlState.value.isBlank() && showErrorIfAny.value,
                modifier = Modifier.fillMaxWidth(),
            )
            val portState =
                remember(initialAddress) {
                    mutableStateOf(initialAddress.port.toString())
                }
            TextField(
                value = portState.value,
                label = {
                    Text(text = "Port serwera")
                },
                onValueChange = { newPort ->
                    portState.value = newPort
                },
                keyboardOptions =
                    KeyboardOptions(
                        keyboardType = KeyboardType.Number,
                    ),
                isError =
                    (portState.value.isBlank() || portState.value.toIntOrNull() == null) &&
                        showErrorIfAny.value,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                onClick = {
                    val portOrNull = portState.value.toIntOrNull()
                    if (urlState.value.isNotBlank() && portOrNull != null) {
                        hideBottomSheetWithAction {
                            onConfirm(
                                ServerAddress(
                                    url = urlState.value,
                                    port = portOrNull,
                                ),
                            )
                        }
                    } else {
                        showErrorIfAny.value = true
                    }
                },
                shape = RectangleShape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF355CA8),
                        contentColor = Color.White,
                    ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Edytuj")
            }

            Button(
                onClick = {
                    dismissBottomSheet()
                },
                shape = RectangleShape,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFF15C5C),
                        contentColor = Color.White,
                    ),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(text = "Anuluj")
            }
        }
    }
}
