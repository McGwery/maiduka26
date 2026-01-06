package com.hojaz.maiduka26.presentantion.screens.settings

import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.domain.model.Subscription
import com.hojaz.maiduka26.domain.model.User
import com.hojaz.maiduka26.presentantion.base.ViewState

/**
 * State for the Settings screen.
 */
data class SettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val user: User? = null,
    val activeShop: Shop? = null,
    val subscription: Subscription? = null,
    val daysRemaining: Int = 0,
    val isOnline: Boolean = true,
    val lastSyncTime: String? = null,
    val pendingSyncCount: Int = 0,

    // App settings
    val isDarkMode: Boolean = false,
    val language: String = "English",
    val notifications: Boolean = true,

    val showLogoutDialog: Boolean = false,
    val showDeleteAccountDialog: Boolean = false
) : ViewState

/**
 * Events for the Settings screen.
 */
sealed class SettingsEvent {
    data object LoadSettings : SettingsEvent()
    data object NavigateToProfile : SettingsEvent()
    data object NavigateToShopSettings : SettingsEvent()
    data object NavigateToSubscription : SettingsEvent()
    data object NavigateToShopMembers : SettingsEvent()
    data object NavigateToAbout : SettingsEvent()
    data object NavigateToHelp : SettingsEvent()
    data object NavigateToPrivacyPolicy : SettingsEvent()
    data object NavigateToTerms : SettingsEvent()
    data object SyncNow : SettingsEvent()
    data class ToggleDarkMode(val enabled: Boolean) : SettingsEvent()
    data class ToggleNotifications(val enabled: Boolean) : SettingsEvent()
    data class ChangeLanguage(val language: String) : SettingsEvent()
    data object ShowLogoutDialog : SettingsEvent()
    data object HideLogoutDialog : SettingsEvent()
    data object Logout : SettingsEvent()
    data object ShowDeleteAccountDialog : SettingsEvent()
    data object HideDeleteAccountDialog : SettingsEvent()
    data object DeleteAccount : SettingsEvent()
    data object NavigateBack : SettingsEvent()
}

/**
 * Side effects for the Settings screen.
 */
sealed class SettingsEffect {
    data object NavigateBack : SettingsEffect()
    data object NavigateToProfile : SettingsEffect()
    data object NavigateToShopSettings : SettingsEffect()
    data object NavigateToSubscription : SettingsEffect()
    data object NavigateToShopMembers : SettingsEffect()
    data object NavigateToLogin : SettingsEffect()
    data class ShowSnackbar(val message: String) : SettingsEffect()
}

