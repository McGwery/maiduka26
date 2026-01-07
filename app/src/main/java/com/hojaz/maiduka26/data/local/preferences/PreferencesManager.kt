package com.hojaz.maiduka26.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "maiduka_preferences")

/**
 * Manages user preferences using DataStore.
 * Provides type-safe access to app settings and user session data.
 */
@Singleton
class PreferencesManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // Keys
    private object PreferenceKeys {
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_PHONE = stringPreferencesKey("user_phone")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val TOKEN_EXPIRY = longPreferencesKey("token_expiry")
        val ACTIVE_SHOP_ID = stringPreferencesKey("active_shop_id")
        val ACTIVE_SHOP_NAME = stringPreferencesKey("active_shop_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val IS_FIRST_LAUNCH = booleanPreferencesKey("is_first_launch")
        val LAST_SYNC_TIME = longPreferencesKey("last_sync_time")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val LANGUAGE = stringPreferencesKey("language")
        val CURRENCY = stringPreferencesKey("currency")
        val ENABLE_NOTIFICATIONS = booleanPreferencesKey("enable_notifications")
        val ENABLE_BIOMETRIC = booleanPreferencesKey("enable_biometric")
        val AUTO_SYNC_ENABLED = booleanPreferencesKey("auto_sync_enabled")
        val SYNC_ON_WIFI_ONLY = booleanPreferencesKey("sync_on_wifi_only")
    }

    // User Preferences Data Class
    data class UserPreferences(
        val userId: String? = null,
        val userName: String? = null,
        val userEmail: String? = null,
        val userPhone: String? = null,
        val accessToken: String? = null,
        val refreshToken: String? = null,
        val tokenExpiry: Long = 0L,
        val activeShopId: String? = null,
        val activeShopName: String? = null,
        val isLoggedIn: Boolean = false,
        val isFirstLaunch: Boolean = true,
        val lastSyncTime: Long = 0L,
        val themeMode: String = "system",
        val language: String = "sw",
        val currency: String = "TZS",
        val enableNotifications: Boolean = true,
        val enableBiometric: Boolean = false,
        val autoSyncEnabled: Boolean = true,
        val syncOnWifiOnly: Boolean = false
    )

    // Flow of user preferences
    val userPreferencesFlow: Flow<UserPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            UserPreferences(
                userId = preferences[PreferenceKeys.USER_ID],
                userName = preferences[PreferenceKeys.USER_NAME],
                userEmail = preferences[PreferenceKeys.USER_EMAIL],
                userPhone = preferences[PreferenceKeys.USER_PHONE],
                accessToken = preferences[PreferenceKeys.ACCESS_TOKEN],
                refreshToken = preferences[PreferenceKeys.REFRESH_TOKEN],
                tokenExpiry = preferences[PreferenceKeys.TOKEN_EXPIRY] ?: 0L,
                activeShopId = preferences[PreferenceKeys.ACTIVE_SHOP_ID],
                activeShopName = preferences[PreferenceKeys.ACTIVE_SHOP_NAME],
                isLoggedIn = preferences[PreferenceKeys.IS_LOGGED_IN] ?: false,
                isFirstLaunch = preferences[PreferenceKeys.IS_FIRST_LAUNCH] ?: true,
                lastSyncTime = preferences[PreferenceKeys.LAST_SYNC_TIME] ?: 0L,
                themeMode = preferences[PreferenceKeys.THEME_MODE] ?: "system",
                language = preferences[PreferenceKeys.LANGUAGE] ?: "sw",
                currency = preferences[PreferenceKeys.CURRENCY] ?: "TZS",
                enableNotifications = preferences[PreferenceKeys.ENABLE_NOTIFICATIONS] ?: true,
                enableBiometric = preferences[PreferenceKeys.ENABLE_BIOMETRIC] ?: false,
                autoSyncEnabled = preferences[PreferenceKeys.AUTO_SYNC_ENABLED] ?: true,
                syncOnWifiOnly = preferences[PreferenceKeys.SYNC_ON_WIFI_ONLY] ?: false
            )
        }

    // Individual preference flows
    val isLoggedIn: Flow<Boolean> = dataStore.data.map { it[PreferenceKeys.IS_LOGGED_IN] ?: false }
    val activeShopId: Flow<String?> = dataStore.data.map { it[PreferenceKeys.ACTIVE_SHOP_ID] }
    val accessToken: Flow<String?> = dataStore.data.map { it[PreferenceKeys.ACCESS_TOKEN] }

    // Save user session after login
    suspend fun saveUserSession(
        userId: String,
        userName: String,
        email: String?,
        phone: String?,
        accessToken: String,
        refreshToken: String,
        tokenExpiry: Long
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ID] = userId
            preferences[PreferenceKeys.USER_NAME] = userName
            email?.let { preferences[PreferenceKeys.USER_EMAIL] = it }
            phone?.let { preferences[PreferenceKeys.USER_PHONE] = it }
            preferences[PreferenceKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferenceKeys.REFRESH_TOKEN] = refreshToken
            preferences[PreferenceKeys.TOKEN_EXPIRY] = tokenExpiry
            preferences[PreferenceKeys.IS_LOGGED_IN] = true
            preferences[PreferenceKeys.IS_FIRST_LAUNCH] = false
        }
    }

    /**
     * Save partial session during registration flow.
     * Does NOT set isLoggedIn to true - used when user still needs to complete OTP/shop setup.
     */
    suspend fun savePartialSession(
        userId: String,
        userName: String,
        email: String?,
        phone: String?,
        accessToken: String
    ) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.USER_ID] = userId
            preferences[PreferenceKeys.USER_NAME] = userName
            email?.let { preferences[PreferenceKeys.USER_EMAIL] = it }
            phone?.let { preferences[PreferenceKeys.USER_PHONE] = it }
            preferences[PreferenceKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferenceKeys.TOKEN_EXPIRY] = System.currentTimeMillis() + (7 * 24 * 60 * 60 * 1000)
            // Do NOT set IS_LOGGED_IN = true here
        }
    }

    /**
     * Complete the registration by setting isLoggedIn to true.
     * Called after OTP verification and shop setup are complete.
     */
    suspend fun completeRegistration() {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.IS_LOGGED_IN] = true
            preferences[PreferenceKeys.IS_FIRST_LAUNCH] = false
        }
    }

    // Update tokens
    suspend fun updateTokens(accessToken: String, refreshToken: String, tokenExpiry: Long) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ACCESS_TOKEN] = accessToken
            preferences[PreferenceKeys.REFRESH_TOKEN] = refreshToken
            preferences[PreferenceKeys.TOKEN_EXPIRY] = tokenExpiry
        }
    }

    // Set active shop
    suspend fun setActiveShop(shopId: String, shopName: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ACTIVE_SHOP_ID] = shopId
            preferences[PreferenceKeys.ACTIVE_SHOP_NAME] = shopName
        }
    }

    // Update last sync time
    suspend fun updateLastSyncTime(time: Long = System.currentTimeMillis()) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LAST_SYNC_TIME] = time
        }
    }

    // Update theme
    suspend fun setThemeMode(mode: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.THEME_MODE] = mode
        }
    }

    // Update language
    suspend fun setLanguage(language: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.LANGUAGE] = language
        }
    }

    // Update currency
    suspend fun setCurrency(currency: String) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.CURRENCY] = currency
        }
    }

    // Enable/disable notifications
    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ENABLE_NOTIFICATIONS] = enabled
        }
    }

    // Enable/disable biometric
    suspend fun setBiometricEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.ENABLE_BIOMETRIC] = enabled
        }
    }

    // Set auto sync
    suspend fun setAutoSyncEnabled(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.AUTO_SYNC_ENABLED] = enabled
        }
    }

    // Set sync on WiFi only
    suspend fun setSyncOnWifiOnly(enabled: Boolean) {
        dataStore.edit { preferences ->
            preferences[PreferenceKeys.SYNC_ON_WIFI_ONLY] = enabled
        }
    }

    // Clear session on logout
    suspend fun clearSession() {
        dataStore.edit { preferences ->
            preferences.remove(PreferenceKeys.USER_ID)
            preferences.remove(PreferenceKeys.USER_NAME)
            preferences.remove(PreferenceKeys.USER_EMAIL)
            preferences.remove(PreferenceKeys.USER_PHONE)
            preferences.remove(PreferenceKeys.ACCESS_TOKEN)
            preferences.remove(PreferenceKeys.REFRESH_TOKEN)
            preferences.remove(PreferenceKeys.TOKEN_EXPIRY)
            preferences.remove(PreferenceKeys.ACTIVE_SHOP_ID)
            preferences.remove(PreferenceKeys.ACTIVE_SHOP_NAME)
            preferences[PreferenceKeys.IS_LOGGED_IN] = false
        }
    }

    // Clear all preferences
    suspend fun clearAll() {
        dataStore.edit { it.clear() }
    }
}

