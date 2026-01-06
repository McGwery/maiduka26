package com.hojaz.maiduka26.presentantion.screens.settings

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.sync.SyncManager
import com.hojaz.maiduka26.domain.usecase.auth.GetCurrentUserUseCase
import com.hojaz.maiduka26.domain.usecase.auth.LogoutUseCase
import com.hojaz.maiduka26.domain.usecase.shop.GetActiveShopUseCase
import com.hojaz.maiduka26.domain.usecase.subscription.GetActiveSubscriptionUseCase
import com.hojaz.maiduka26.domain.usecase.subscription.GetSubscriptionDaysRemainingUseCase
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import com.hojaz.maiduka26.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Settings screen.
 */
@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase,
    private val getDaysRemainingUseCase: GetSubscriptionDaysRemainingUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val preferencesManager: PreferencesManager,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor
) : BaseViewModel<SettingsState, SettingsEvent, SettingsEffect>() {

    override fun createInitialState(): SettingsState = SettingsState()

    init {
        loadSettings()
        observeNetworkStatus()
    }

    override fun onEvent(event: SettingsEvent) {
        when (event) {
            is SettingsEvent.LoadSettings -> loadSettings()
            is SettingsEvent.NavigateToProfile -> setEffect(SettingsEffect.NavigateToProfile)
            is SettingsEvent.NavigateToShopSettings -> setEffect(SettingsEffect.NavigateToShopSettings)
            is SettingsEvent.NavigateToSubscription -> setEffect(SettingsEffect.NavigateToSubscription)
            is SettingsEvent.NavigateToShopMembers -> setEffect(SettingsEffect.NavigateToShopMembers)
            is SettingsEvent.NavigateToAbout -> { /* Navigate to About */ }
            is SettingsEvent.NavigateToHelp -> { /* Navigate to Help */ }
            is SettingsEvent.NavigateToPrivacyPolicy -> { /* Open Privacy Policy */ }
            is SettingsEvent.NavigateToTerms -> { /* Open Terms */ }
            is SettingsEvent.SyncNow -> syncNow()
            is SettingsEvent.ToggleDarkMode -> toggleDarkMode(event.enabled)
            is SettingsEvent.ToggleNotifications -> toggleNotifications(event.enabled)
            is SettingsEvent.ChangeLanguage -> changeLanguage(event.language)
            is SettingsEvent.ShowLogoutDialog -> setState { copy(showLogoutDialog = true) }
            is SettingsEvent.HideLogoutDialog -> setState { copy(showLogoutDialog = false) }
            is SettingsEvent.Logout -> logout()
            is SettingsEvent.ShowDeleteAccountDialog -> setState { copy(showDeleteAccountDialog = true) }
            is SettingsEvent.HideDeleteAccountDialog -> setState { copy(showDeleteAccountDialog = false) }
            is SettingsEvent.DeleteAccount -> deleteAccount()
            is SettingsEvent.NavigateBack -> setEffect(SettingsEffect.NavigateBack)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            try {
                // Load user
                launch {
                    getCurrentUserUseCase().collectLatest { user ->
                        setState { copy(user = user) }
                    }
                }

                // Load active shop
                launch {
                    getActiveShopUseCase().collectLatest { shop ->
                        setState { copy(activeShop = shop) }

                        shop?.let { loadSubscription(it.id) }
                    }
                }

                // Load preferences
                launch {
                    preferencesManager.userPreferencesFlow.collectLatest { prefs ->
                        setState {
                            copy(
                                isDarkMode = prefs.themeMode == "dark",
                                language = prefs.language,
                                notifications = prefs.enableNotifications
                            )
                        }
                    }
                }

                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun loadSubscription(shopId: String) {
        viewModelScope.launch {
            getActiveSubscriptionUseCase(shopId).collectLatest { subscription ->
                setState { copy(subscription = subscription) }
            }
        }

        viewModelScope.launch {
            val days = getDaysRemainingUseCase(shopId)
            setState { copy(daysRemaining = days) }
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.isOnlineFlow.collectLatest { isOnline ->
                setState { copy(isOnline = isOnline) }
            }
        }
    }

    private fun syncNow() {
        if (!currentState.isOnline) {
            setEffect(SettingsEffect.ShowSnackbar("You are offline"))
            return
        }

        syncManager.triggerImmediateSync()
        setEffect(SettingsEffect.ShowSnackbar("Syncing..."))
    }

    private fun toggleDarkMode(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(if (enabled) "dark" else "light")
            setState { copy(isDarkMode = enabled) }
        }
    }

    private fun toggleNotifications(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
            setState { copy(notifications = enabled) }
        }
    }

    private fun changeLanguage(language: String) {
        viewModelScope.launch {
            preferencesManager.setLanguage(language)
            setState { copy(language = language) }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            setState { copy(showLogoutDialog = false, isLoading = true) }

            logoutUseCase().fold(
                ifLeft = { error ->
                    setState { copy(isLoading = false) }
                    setEffect(SettingsEffect.ShowSnackbar(error.message ?: "Logout failed"))
                },
                ifRight = {
                    setState { copy(isLoading = false) }
                    setEffect(SettingsEffect.NavigateToLogin)
                }
            )
        }
    }

    private fun deleteAccount() {
        viewModelScope.launch {
            setState { copy(showDeleteAccountDialog = false, isLoading = true) }
            // TODO: Implement delete account
            setEffect(SettingsEffect.ShowSnackbar("Account deletion requested"))
            setState { copy(isLoading = false) }
        }
    }
}

