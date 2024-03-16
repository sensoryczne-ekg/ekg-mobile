package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.lifecycle.viewModelScope
import com.pawlowski.ekgmonitor.BaseMviViewModel
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.ekgmonitor.domain.getDataOrNull
import com.pawlowski.ekgmonitor.domain.useCase.StreamRecords
import com.pawlowski.ekgmonitor.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class ChartViewModel
    @Inject
    constructor(
        private val streamRecords: StreamRecords,
    ) : BaseMviViewModel<ChartState, ChartEvent, Screen.Chart.ChartDirection>(
            initialState = ChartState(recordsResource = Resource.Loading),
        ) {
        override fun initialised() {
            streamRecords()
                .onEach { newRecord ->
                    updateState {
                        copy(
                            recordsResource =
                                Resource.Success(
                                    data = (recordsResource.getDataOrNull() ?: listOf()) + newRecord,
                                ),
                        )
                    }
                }.catch {
                    it.printStackTrace()
                    updateState {
                        copy(recordsResource = Resource.Error(it))
                    }
                }.launchIn(viewModelScope)
        }

        override fun onNewEvent(event: ChartEvent) {
        }
    }
