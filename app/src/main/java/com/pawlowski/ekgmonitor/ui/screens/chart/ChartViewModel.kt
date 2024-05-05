package com.pawlowski.ekgmonitor.ui.screens.chart

import androidx.lifecycle.viewModelScope
import com.pawlowski.datastore.IServerAddressRepository
import com.pawlowski.datastore.ServerAddress
import com.pawlowski.ekgmonitor.BaseMviViewModel
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.ekgmonitor.domain.RetrySharedFlow
import com.pawlowski.ekgmonitor.domain.getDataOrNull
import com.pawlowski.ekgmonitor.domain.useCase.StreamRecords
import com.pawlowski.ekgmonitor.ui.navigation.Screen.Chart.ChartDirection
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class ChartViewModel
    @Inject
    constructor(
        private val streamRecords: StreamRecords,
        private val serverAddressRepository: IServerAddressRepository,
    ) : BaseMviViewModel<ChartState, ChartEvent, ChartDirection>(
            initialState =
                ChartState(
                    recordsResource = Resource.Loading,
                    currentServerAddress = null,
                ),
        ) {
        private val retrySharedFlow = RetrySharedFlow()
        val observeRecordsFlow =
            flow<Unit> {
                observeRecords()
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000L),
                initialValue = Unit,
            )

        override fun initialised() {
            refreshServerAddress()
        }

        override fun onNewEvent(event: ChartEvent) {
            when (event) {
                is ChartEvent.ChangeServerAddress -> {
                    viewModelScope.launch {
                        runCatching {
                            serverAddressRepository.changeServerAddress(newAddress = event.newAddress)
                            pushNavigationEvent(ChartDirection.CHART_WITH_REFRESH)
                        }.onFailure {
                            ensureActive()
                            it.printStackTrace()
                        }
                    }
                }

                ChartEvent.HistoryClick -> pushNavigationEvent(ChartDirection.CHOOSE_PERIOD)
                ChartEvent.RetryClick -> retrySharedFlow.sendRetryEvent()
            }
        }

        private suspend fun observeRecords() {
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
                }.retryWhen { cause, _ ->
                    updateState {
                        copy(recordsResource = Resource.Error(throwable = cause))
                    }
                    retrySharedFlow.waitForRetry()
                    true
                }.collect {}
        }

        private fun refreshServerAddress() {
            viewModelScope.launch {
                val address =
                    runCatching {
                        serverAddressRepository.getServerAddress()
                    }.getOrNull() ?: ServerAddress(url = "", port = 0)

                updateState {
                    copy(currentServerAddress = address)
                }
            }
        }
    }
