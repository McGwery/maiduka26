package com.hojaz.maiduka26.presentantion.screens.subscription

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Subscription Payment screen.
 */
@HiltViewModel
class SubscriptionPaymentViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
    // TODO: Inject SubscriptionRepository for actual payment processing
) : BaseViewModel<SubscriptionPaymentState, SubscriptionPaymentEvent, SubscriptionPaymentEffect>() {

    override fun createInitialState(): SubscriptionPaymentState = SubscriptionPaymentState()

    override fun onEvent(event: SubscriptionPaymentEvent) {
        when (event) {
            is SubscriptionPaymentEvent.SelectPaymentMethod -> {
                setState { copy(selectedPaymentMethod = event.method, error = null) }
            }
            is SubscriptionPaymentEvent.UpdatePhoneNumber -> {
                setState { copy(phoneNumber = event.phone, error = null) }
            }
            is SubscriptionPaymentEvent.ProcessPayment -> processPayment()
            is SubscriptionPaymentEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun processPayment() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                // TODO: Implement actual payment API call
                // For now, simulate payment processing
                delay(2000)

                // Simulate successful payment
                // In real implementation, this would:
                // 1. Call payment API
                // 2. Wait for confirmation
                // 3. Update subscription in local database
                // 4. Complete registration if needed

                // Complete registration (sets isLoggedIn = true if not already)
                preferencesManager.completeRegistration()

                setState { copy(isLoading = false, paymentSuccessful = true) }
                setEffect(SubscriptionPaymentEffect.ShowSnackbar("Payment successful! Your subscription is now active."))
                setEffect(SubscriptionPaymentEffect.NavigateToDashboard)

            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message ?: "Payment failed") }
                setEffect(SubscriptionPaymentEffect.ShowSnackbar("Payment failed. Please try again."))
            }
        }
    }
}

