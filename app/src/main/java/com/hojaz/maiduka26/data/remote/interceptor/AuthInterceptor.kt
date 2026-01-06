package com.hojaz.maiduka26.data.remote.interceptor

import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

/**
 * OkHttp interceptor for adding authentication headers to requests.
 */
@Singleton
class AuthInterceptor @Inject constructor(
    private val preferencesManager: PreferencesManager
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        // Skip auth for login/register endpoints
        val path = originalRequest.url.encodedPath
        if (path.contains("auth/login") ||
            path.contains("auth/register") ||
            path.contains("auth/forgot-password")) {
            return chain.proceed(originalRequest)
        }

        // Get access token
        val accessToken = runBlocking {
            preferencesManager.accessToken.first()
        }

        // Add authorization header if token exists
        val newRequest = if (!accessToken.isNullOrEmpty()) {
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
        } else {
            originalRequest.newBuilder()
                .header("Accept", "application/json")
                .header("Content-Type", "application/json")
                .build()
        }

        return chain.proceed(newRequest)
    }
}

