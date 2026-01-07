package com.hojaz.maiduka26.presentantion.screens.auth.login

import com.hojaz.maiduka26.presentantion.base.ViewState

/**
 * State for the Login screen.
 */
data class LoginState(
    val email: String = "",
    val phone: String = "",
    val password: String = "",
    val isEmailLogin: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val isLoginSuccessful: Boolean = false
) : ViewState

/**
 * Events for the Login screen.
 */
sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PhoneChanged(val phone: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object ToggleLoginMethod : LoginEvent()
    data object Login : LoginEvent()
    data object ClearError : LoginEvent()
    data object NavigateToRegister : LoginEvent()
    data object NavigateToForgotPassword : LoginEvent()
}

/**
 * Side effects for the Login screen.
 */
sealed class LoginEffect {
    data object NavigateToHome : LoginEffect()
    data object NavigateToRegister : LoginEffect()
    data object NavigateToForgotPassword : LoginEffect()
    data object NavigateToCreateShop : LoginEffect()
    data object NavigateToSubscriptionPayment : LoginEffect()
    data class NavigateToSubscriptionWarning(val daysRemaining: Int) : LoginEffect()
    data class ShowSnackbar(val message: String) : LoginEffect()
}

