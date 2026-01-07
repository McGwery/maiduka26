package com.hojaz.maiduka26.presentantion.screens.subscription

import com.hojaz.maiduka26.presentantion.base.ViewState

/**
 * State for Subscription Payment screen.
 */
data class SubscriptionPaymentState(
    val selectedPaymentMethod: String = "",
    val phoneNumber: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val paymentSuccessful: Boolean = false
) : ViewState

/**
 * Events for Subscription Payment screen.
 */
sealed class SubscriptionPaymentEvent {
    data class SelectPaymentMethod(val method: String) : SubscriptionPaymentEvent()
    data class UpdatePhoneNumber(val phone: String) : SubscriptionPaymentEvent()
    data object ProcessPayment : SubscriptionPaymentEvent()
    data object ClearError : SubscriptionPaymentEvent()
}

/**
 * Effects for Subscription Payment screen.
 */
sealed class SubscriptionPaymentEffect {
    data object NavigateToDashboard : SubscriptionPaymentEffect()
    data class ShowSnackbar(val message: String) : SubscriptionPaymentEffect()
}

