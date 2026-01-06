package com.hojaz.maiduka26.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor for handling and transforming API errors.
 */
@Singleton
class ErrorInterceptor @Inject constructor() : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        return try {
            val response = chain.proceed(request)

            // Log non-successful responses
            if (!response.isSuccessful) {
                Timber.e("API Error: ${response.code} - ${response.message}")
            }

            // Handle specific error codes
            when (response.code) {
                401 -> {
                    // Unauthorized - token expired or invalid
                    Timber.w("Unauthorized request - token may be expired")
                    // Token refresh should be handled by AuthAuthenticator
                }
                403 -> {
                    Timber.w("Forbidden - user doesn't have permission")
                }
                404 -> {
                    Timber.w("Resource not found: ${request.url}")
                }
                422 -> {
                    Timber.w("Validation error")
                }
                500, 502, 503 -> {
                    Timber.e("Server error: ${response.code}")
                }
            }

            response
        } catch (e: SocketTimeoutException) {
            Timber.e(e, "Request timeout")
            throw IOException("Request timed out. Please check your internet connection.", e)
        } catch (e: UnknownHostException) {
            Timber.e(e, "No internet connection")
            throw IOException("No internet connection. Please check your network settings.", e)
        } catch (e: IOException) {
            Timber.e(e, "Network error")
            throw e
        }
    }
}

