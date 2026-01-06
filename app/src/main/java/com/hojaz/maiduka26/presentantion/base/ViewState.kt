package com.hojaz.maiduka26.presentantion.base

/**
 * Base interface for UI state in MVI pattern.
 */
interface ViewState

/**
 * Common UI state wrapper for screens.
 */
sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()
    data class Success<T>(val data: T) : UiState<T>()
    data class Error(val message: String) : UiState<Nothing>()
    data object Empty : UiState<Nothing>()

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error
    val isEmpty: Boolean get() = this is Empty

    fun getOrNull(): T? = (this as? Success)?.data
}

/**
 * Extension to convert to UiState.
 */
fun <T> T.toSuccess(): UiState<T> = UiState.Success(this)
fun String.toError(): UiState<Nothing> = UiState.Error(this)

