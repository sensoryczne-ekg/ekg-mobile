package com.pawlowski.ekgmonitor.ui.screens.history

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.pawlowski.ekgmonitor.BaseMviViewModel
import com.pawlowski.ekgmonitor.domain.Resource
import com.pawlowski.ekgmonitor.domain.RetrySharedFlow
import com.pawlowski.ekgmonitor.domain.resourceFlowWithRetrying
import com.pawlowski.ekgmonitor.domain.useCase.GetRecords
import com.pawlowski.ekgmonitor.ui.navigation.Back
import com.pawlowski.ekgmonitor.ui.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class HistoryViewModel
    @Inject
    constructor(
        private val getRecords: GetRecords,
        savedStateHandle: SavedStateHandle,
    ) : BaseMviViewModel<HistoryState, HistoryEvent, Screen.History.HistoryDirection>(
            initialState =
                HistoryState(
                    recordsResource = Resource.Loading,
                ),
        ) {
        private val from = savedStateHandle.get<Long>("from")!!
        private val to = savedStateHandle.get<Long>("to")!!

        private val retrySharedFlow = RetrySharedFlow()

        override fun initialised() {
            viewModelScope.launch {
                resourceFlowWithRetrying(retrySharedFlow = retrySharedFlow) {
                    getRecords(
                        from = from,
                        to = to,
                    ).toImmutableList()
                }.collect {
                    updateState {
                        copy(recordsResource = it)
                    }
                }
            }
        }

        override fun onNewEvent(event: HistoryEvent) {
            when (event) {
                HistoryEvent.BackClick -> pushNavigationEvent(Back)
                HistoryEvent.RetryClick -> retrySharedFlow.sendRetryEvent()
            }
        }
    }
