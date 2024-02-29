package com.pawlowski.ekgmonitor

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.util.concurrent.atomic.AtomicBoolean

abstract class BaseMviViewModel<STATE, EVENT>(
    val initialState: STATE,
) : ViewModel() {
    private val wasInitialisedCalled = AtomicBoolean(false)

    protected open fun initialised() {}

    private val mutableStateFlow = MutableStateFlow(initialState)
    val stateFlow =
        mutableStateFlow.onSubscribe {
            if (wasInitialisedCalled.compareAndSet(false, true)) {
                initialised()
            }
        }

    protected val actualState: STATE
        get() = stateFlow.value

    protected fun updateState(update: STATE.() -> STATE) {
        mutableStateFlow.update(update)
    }

    abstract fun onNewEvent(event: EVENT)
}

private fun <T> StateFlow<T>.onSubscribe(block: () -> Unit): StateFlow<T> =
    object : StateFlow<T> {
        override val replayCache: List<T>
            get() = this@onSubscribe.replayCache

        override val value: T
            get() = this@onSubscribe.value

        override suspend fun collect(collector: FlowCollector<T>): Nothing {
            block()
            this@onSubscribe.collect(collector)
        }
    }
