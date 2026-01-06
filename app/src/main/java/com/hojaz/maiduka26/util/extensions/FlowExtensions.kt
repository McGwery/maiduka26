package com.hojaz.maiduka26.util.extensions

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import com.hojaz.maiduka26.util.Resource

/**
 * Extension functions for Flow.
 */

/**
 * Wraps flow emissions in Resource wrapper.
 */
fun <T> Flow<T>.asResource(): Flow<Resource<T>> {
    return this
        .map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading()) }
        .catch { emit(Resource.Error(it.message ?: "An error occurred")) }
}

/**
 * Maps flow with error handling.
 */
fun <T, R> Flow<T>.mapCatching(transform: suspend (T) -> R): Flow<Result<R>> {
    return map { value ->
        try {
            Result.success(transform(value))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

/**
 * Collects flow with error handling.
 */
suspend fun <T> Flow<T>.collectSafely(
    onError: (Throwable) -> Unit = {},
    onEach: suspend (T) -> Unit
) {
    catch { onError(it) }
        .collect { onEach(it) }
}

