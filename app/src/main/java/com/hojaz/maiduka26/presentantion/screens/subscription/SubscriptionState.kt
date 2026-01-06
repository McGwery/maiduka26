package com.hojaz.maiduka26.presentantion.screens.subscription

import com.hojaz.maiduka26.domain.model.Subscription
import com.hojaz.maiduka26.domain.model.SubscriptionPlan
import com.hojaz.maiduka26.domain.usecase.subscription.PaymentInitiation
import com.hojaz.maiduka26.presentantion.base.ViewState
import java.math.BigDecimal

/**
 * State for the Subscription screen.
 */
data class SubscriptionState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val currentSubscription: Subscription? = null,
    val subscriptionHistory: List<Subscription> = emptyList(),
    val daysRemaining: Int = 0,

    // Plan selection
    val selectedPlan: SubscriptionPlan = SubscriptionPlan.BASIC,
    val isAnnual: Boolean = false,
    val calculatedPrice: BigDecimal = BigDecimal("12000.00"),

    // Payment
    val selectedPaymentMethod: PaymentMethod = PaymentMethod.MPESA,
    val phoneNumber: String = "",
    val isProcessingPayment: Boolean = false,
    val paymentInitiation: PaymentInitiation? = null,

    // Dialogs
    val showPaymentDialog: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val showConfirmCancelDialog: Boolean = false,

    val shopId: String = ""
) : ViewState {

    val annualSavings: BigDecimal
        get() = when (selectedPlan) {
            SubscriptionPlan.FREE -> BigDecimal.ZERO
            SubscriptionPlan.BASIC -> BigDecimal("24000.00") // 2 months free
            SubscriptionPlan.PREMIUM -> BigDecimal("50000.00")
            SubscriptionPlan.ENTERPRISE -> BigDecimal("100000.00")
        }

    val monthlyPrice: BigDecimal
        get() = when (selectedPlan) {
            SubscriptionPlan.FREE -> BigDecimal.ZERO
            SubscriptionPlan.BASIC -> BigDecimal("12000.00")
            SubscriptionPlan.PREMIUM -> BigDecimal("25000.00")
            SubscriptionPlan.ENTERPRISE -> BigDecimal("50000.00")
        }

    val annualPrice: BigDecimal
        get() = when (selectedPlan) {
            SubscriptionPlan.FREE -> BigDecimal.ZERO
            SubscriptionPlan.BASIC -> BigDecimal("120000.00")
            SubscriptionPlan.PREMIUM -> BigDecimal("250000.00")
            SubscriptionPlan.ENTERPRISE -> BigDecimal("500000.00")
        }
}

enum class PaymentMethod(val displayName: String, val code: String) {
    MPESA("M-Pesa", "mpesa"),
    TIGOPESA("Tigo Pesa", "tigopesa"),
    AIRTEL("Airtel Money", "airtel"),
    HALOPESA("Halo Pesa", "halopesa"),
    CARD("Card Payment", "card")
}

/**
 * Events for the Subscription screen.
 */
sealed class SubscriptionEvent {
    data object LoadSubscription : SubscriptionEvent()
    data class SelectPlan(val plan: SubscriptionPlan) : SubscriptionEvent()
    data class ToggleBillingCycle(val isAnnual: Boolean) : SubscriptionEvent()
    data class SelectPaymentMethod(val method: PaymentMethod) : SubscriptionEvent()
    data class UpdatePhoneNumber(val phone: String) : SubscriptionEvent()
    data object ShowPaymentDialog : SubscriptionEvent()
    data object HidePaymentDialog : SubscriptionEvent()
    data object InitiatePayment : SubscriptionEvent()
    data object ConfirmPayment : SubscriptionEvent()
    data object CancelSubscription : SubscriptionEvent()
    data object ShowCancelDialog : SubscriptionEvent()
    data object HideCancelDialog : SubscriptionEvent()
    data object HideSuccessDialog : SubscriptionEvent()
    data object NavigateBack : SubscriptionEvent()
}

/**
 * Side effects for the Subscription screen.
 */
sealed class SubscriptionEffect {
    data object NavigateBack : SubscriptionEffect()
    data object NavigateToDashboard : SubscriptionEffect()
    data class ShowSnackbar(val message: String) : SubscriptionEffect()
    data object OpenUssdPrompt : SubscriptionEffect()
    data class OpenPaymentUrl(val url: String) : SubscriptionEffect()
}

