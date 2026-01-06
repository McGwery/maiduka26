package com.hojaz.maiduka26.util

/**
 * A generic class that holds a value with its loading status.
 * @param T
 */
sealed class Resource<T>(
    val data: T? = null,
    val message: String? = null
) {
    class Success<T>(data: T) : Resource<T>(data)
    class Loading<T>(data: T? = null) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)

    val isLoading: Boolean get() = this is Loading
    val isSuccess: Boolean get() = this is Success
    val isError: Boolean get() = this is Error

    fun getOrNull(): T? = data
    fun getOrThrow(): T = data ?: throw IllegalStateException(message ?: "Data is null")

    inline fun onSuccess(action: (T) -> Unit): Resource<T> {
        if (this is Success && data != null) {
            action(data)
        }
        return this
    }

    inline fun onError(action: (String) -> Unit): Resource<T> {
        if (this is Error && message != null) {
            action(message)
        }
        return this
    }

    inline fun onLoading(action: () -> Unit): Resource<T> {
        if (this is Loading) {
            action()
        }
        return this
    }

    inline fun <R> map(transform: (T) -> R): Resource<R> {
        return when (this) {
            is Success -> Success(transform(data!!))
            is Error -> Error(message!!, data?.let { transform(it) })
            is Loading -> Loading(data?.let { transform(it) })
        }
    }

    companion object {
        fun <T> success(data: T): Resource<T> = Success(data)
        fun <T> error(message: String, data: T? = null): Resource<T> = Error(message, data)
        fun <T> loading(data: T? = null): Resource<T> = Loading(data)
    }
}

