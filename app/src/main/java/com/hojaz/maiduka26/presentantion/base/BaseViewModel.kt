package com.hojaz.maiduka26.presentantion.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * Base ViewModel providing common functionality for all ViewModels.
 * Implements MVI pattern with state, events, and effects.
 */
abstract class BaseViewModel<State : ViewState, Event, Effect> : ViewModel() {

    // Initial state must be provided by subclasses
    abstract fun createInitialState(): State

    // Current state
    private val _state: MutableStateFlow<State> by lazy { MutableStateFlow(createInitialState()) }
    val state: StateFlow<State> by lazy { _state.asStateFlow() }

    // One-time effects (navigation, snackbar, etc.)
    private val _effect: Channel<Effect> = Channel()
    val effect: Flow<Effect> = _effect.receiveAsFlow()

    // Loading state
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Error state
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    /**
     * Get current state value.
     */
    protected val currentState: State
        get() = _state.value

    /**
     * Handle events from the UI.
     */
    abstract fun onEvent(event: Event)

    /**
     * Update state with a reducer function.
     */
    protected fun setState(reduce: State.() -> State) {
        _state.update { it.reduce() }
    }

    /**
     * Send a one-time effect to the UI.
     */
    protected fun setEffect(effect: Effect) {
        viewModelScope.launch {
            _effect.send(effect)
        }
    }

    /**
     * Set loading state.
     */
    protected fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    /**
     * Set error message.
     */
    protected fun setError(message: String?) {
        _error.value = message
    }

    /**
     * Clear error message.
     */
    fun clearError() {
        _error.value = null
    }

    /**
     * Execute a suspend function with loading state management.
     */
    protected fun <T> execute(
        showLoading: Boolean = true,
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null,
        block: suspend () -> T
    ) {
        viewModelScope.launch {
            try {
                if (showLoading) setLoading(true)
                val result = block()
                onSuccess?.invoke(result)
            } catch (e: Exception) {
                setError(e.message ?: "An error occurred")
                onError?.invoke(e)
            } finally {
                if (showLoading) setLoading(false)
            }
        }
    }

    /**
     * Execute with Either result handling.
     */
    protected fun <T> executeEither(
        showLoading: Boolean = true,
        onError: ((Throwable) -> Unit)? = null,
        onSuccess: ((T) -> Unit)? = null,
        block: suspend () -> arrow.core.Either<Throwable, T>
    ) {
        viewModelScope.launch {
            try {
                if (showLoading) setLoading(true)
                block().fold(
                    ifLeft = { error ->
                        setError(error.message ?: "An error occurred")
                        onError?.invoke(error)
                    },
                    ifRight = { result ->
                        onSuccess?.invoke(result)
                    }
                )
            } finally {
                if (showLoading) setLoading(false)
            }
        }
    }
}

