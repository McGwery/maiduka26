package com.hojaz.maiduka26.presentantion.screens.subscription

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.model.SubscriptionPlan
import com.hojaz.maiduka26.domain.usecase.shop.GetActiveShopUseCase
import com.hojaz.maiduka26.domain.usecase.subscription.*
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/**
 * ViewModel for the Subscription screen.
 */
@HiltViewModel
class SubscriptionViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase,
    private val getSubscriptionHistoryUseCase: GetSubscriptionHistoryUseCase,
    private val getDaysRemainingUseCase: GetSubscriptionDaysRemainingUseCase,
    private val createSubscriptionUseCase: CreateSubscriptionUseCase,
    private val initiatePaymentUseCase: InitiateSubscriptionPaymentUseCase,
    private val activateSubscriptionUseCase: ActivateSubscriptionUseCase,
    private val cancelSubscriptionUseCase: CancelSubscriptionUseCase
) : BaseViewModel<SubscriptionState, SubscriptionEvent, SubscriptionEffect>() {

    override fun createInitialState(): SubscriptionState = SubscriptionState()

    init {
        loadSubscription()
    }

    override fun onEvent(event: SubscriptionEvent) {
        when (event) {
            is SubscriptionEvent.LoadSubscription -> loadSubscription()
            is SubscriptionEvent.SelectPlan -> selectPlan(event.plan)
            is SubscriptionEvent.ToggleBillingCycle -> toggleBillingCycle(event.isAnnual)
            is SubscriptionEvent.SelectPaymentMethod -> setState { copy(selectedPaymentMethod = event.method) }
            is SubscriptionEvent.UpdatePhoneNumber -> setState { copy(phoneNumber = event.phone) }
            is SubscriptionEvent.ShowPaymentDialog -> setState { copy(showPaymentDialog = true) }
            is SubscriptionEvent.HidePaymentDialog -> setState { copy(showPaymentDialog = false) }
            is SubscriptionEvent.InitiatePayment -> initiatePayment()
            is SubscriptionEvent.ConfirmPayment -> confirmPayment()
            is SubscriptionEvent.CancelSubscription -> cancelSubscription()
            is SubscriptionEvent.ShowCancelDialog -> setState { copy(showConfirmCancelDialog = true) }
            is SubscriptionEvent.HideCancelDialog -> setState { copy(showConfirmCancelDialog = false) }
            is SubscriptionEvent.HideSuccessDialog -> setState { copy(showSuccessDialog = false) }
            is SubscriptionEvent.NavigateBack -> setEffect(SubscriptionEffect.NavigateBack)
        }
    }

    private fun loadSubscription() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val shop = getActiveShopUseCase().first()
                if (shop == null) {
                    setState { copy(isLoading = false, error = "No shop selected") }
                    return@launch
                }

                setState { copy(shopId = shop.id) }

                // Load current subscription
                launch {
                    getActiveSubscriptionUseCase(shop.id).collectLatest { subscription ->
                        setState {
                            copy(
                                currentSubscription = subscription,
                                selectedPlan = subscription?.plan ?: SubscriptionPlan.BASIC
                            )
                        }
                    }
                }

                // Load subscription history
                launch {
                    getSubscriptionHistoryUseCase(shop.id).collectLatest { history ->
                        setState { copy(subscriptionHistory = history) }
                    }
                }

                // Load days remaining
                launch {
                    val days = getDaysRemainingUseCase(shop.id)
                    setState { copy(daysRemaining = days) }
                }

                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun selectPlan(plan: SubscriptionPlan) {
        val price = calculatePrice(plan, currentState.isAnnual)
        setState { copy(selectedPlan = plan, calculatedPrice = price) }
    }

    private fun toggleBillingCycle(isAnnual: Boolean) {
        val price = calculatePrice(currentState.selectedPlan, isAnnual)
        setState { copy(isAnnual = isAnnual, calculatedPrice = price) }
    }

    private fun calculatePrice(plan: SubscriptionPlan, isAnnual: Boolean): BigDecimal {
        return SubscriptionPricing.getPrice(plan, isAnnual)
    }

    private fun initiatePayment() {
        if (currentState.selectedPaymentMethod != PaymentMethod.CARD &&
            currentState.phoneNumber.isBlank()) {
            setEffect(SubscriptionEffect.ShowSnackbar("Please enter your phone number"))
            return
        }

        viewModelScope.launch {
            setState { copy(isProcessingPayment = true) }

            try {
                // First create the subscription
                createSubscriptionUseCase(
                    shopId = currentState.shopId,
                    plan = currentState.selectedPlan,
                    isAnnual = currentState.isAnnual,
                    autoRenew = true
                ).fold(
                    ifLeft = { error ->
                        setState { copy(isProcessingPayment = false) }
                        setEffect(SubscriptionEffect.ShowSnackbar(error.message ?: "Failed to create subscription"))
                    },
                    ifRight = { subscription ->
                        // Now initiate payment
                        initiatePaymentUseCase(
                            subscriptionId = subscription.id,
                            paymentMethod = currentState.selectedPaymentMethod.code
                        ).fold(
                            ifLeft = { error ->
                                setState { copy(isProcessingPayment = false) }
                                setEffect(SubscriptionEffect.ShowSnackbar(error.message ?: "Payment initiation failed"))
                            },
                            ifRight = { paymentInitiation ->
                                setState {
                                    copy(
                                        isProcessingPayment = false,
                                        paymentInitiation = paymentInitiation,
                                        showPaymentDialog = true
                                    )
                                }

                                // For mobile money, show USSD prompt simulation
                                if (currentState.selectedPaymentMethod != PaymentMethod.CARD) {
                                    setEffect(SubscriptionEffect.OpenUssdPrompt)
                                }
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                setState { copy(isProcessingPayment = false) }
                setEffect(SubscriptionEffect.ShowSnackbar(e.message ?: "Payment failed"))
            }
        }
    }

    private fun confirmPayment() {
        val paymentInitiation = currentState.paymentInitiation ?: return

        viewModelScope.launch {
            setState { copy(isProcessingPayment = true) }

            try {
                // Simulate payment confirmation (in production, this would come from payment gateway callback)
                val transactionRef = "TXN${System.currentTimeMillis()}"

                activateSubscriptionUseCase(
                    subscriptionId = paymentInitiation.subscriptionId,
                    transactionReference = transactionRef
                ).fold(
                    ifLeft = { error ->
                        setState { copy(isProcessingPayment = false) }
                        setEffect(SubscriptionEffect.ShowSnackbar(error.message ?: "Activation failed"))
                    },
                    ifRight = { subscription ->
                        setState {
                            copy(
                                isProcessingPayment = false,
                                showPaymentDialog = false,
                                showSuccessDialog = true,
                                currentSubscription = subscription
                            )
                        }
                        setEffect(SubscriptionEffect.ShowSnackbar("Subscription activated successfully!"))
                    }
                )
            } catch (e: Exception) {
                setState { copy(isProcessingPayment = false) }
                setEffect(SubscriptionEffect.ShowSnackbar(e.message ?: "Payment confirmation failed"))
            }
        }
    }

    private fun cancelSubscription() {
        val subscription = currentState.currentSubscription ?: return

        viewModelScope.launch {
            setState { copy(isLoading = true, showConfirmCancelDialog = false) }

            cancelSubscriptionUseCase(subscription.id, "User requested cancellation").fold(
                ifLeft = { error ->
                    setState { copy(isLoading = false) }
                    setEffect(SubscriptionEffect.ShowSnackbar(error.message ?: "Failed to cancel"))
                },
                ifRight = {
                    setState { copy(isLoading = false) }
                    setEffect(SubscriptionEffect.ShowSnackbar("Subscription cancelled"))
                    loadSubscription()
                }
            )
        }
    }
}

