package com.hojaz.maiduka26.presentantion.screens.auth.register

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.domain.model.SubscriptionPlan
import com.hojaz.maiduka26.domain.usecase.auth.RegisterUseCase
import com.hojaz.maiduka26.domain.usecase.auth.SendOtpUseCase
import com.hojaz.maiduka26.domain.usecase.auth.VerifyOtpUseCase
import com.hojaz.maiduka26.domain.usecase.shop.SetActiveShopUseCase
import com.hojaz.maiduka26.domain.usecase.shop.SetupNewShopUseCase
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import com.hojaz.maiduka26.util.DateTimeUtil
import com.hojaz.maiduka26.util.extensions.isValidEmail
import com.hojaz.maiduka26.util.extensions.isValidPhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Register screen.
 *
 * Handles the 3-step registration flow:
 * 1. Account Info - User registration
 * 2. OTP Verification - Phone/Email verification
 * 3. Shop Setup - Create first shop with default subscription
 */
@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
    private val sendOtpUseCase: SendOtpUseCase,
    private val verifyOtpUseCase: VerifyOtpUseCase,
    private val setupNewShopUseCase: SetupNewShopUseCase,
    private val setActiveShopUseCase: SetActiveShopUseCase,
    private val preferencesManager: PreferencesManager
) : BaseViewModel<RegisterState, RegisterEvent, RegisterEffect>() {

    override fun createInitialState(): RegisterState = RegisterState()

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            // Step 1: Account Info
            is RegisterEvent.UpdateFullName -> {
                setState { copy(fullName = event.name, fullNameError = null, error = null) }
            }
            is RegisterEvent.UpdateEmail -> {
                setState { copy(email = event.email, emailError = null, error = null) }
            }
            is RegisterEvent.UpdatePhoneNumber -> {
                setState { copy(phoneNumber = event.phone, phoneError = null, error = null) }
            }
            is RegisterEvent.UpdatePassword -> {
                setState { copy(password = event.password, passwordError = null, error = null) }
            }
            is RegisterEvent.UpdateConfirmPassword -> {
                setState { copy(confirmPassword = event.password, confirmPasswordError = null, error = null) }
            }
            is RegisterEvent.SubmitAccountInfo -> submitAccountInfo()

            // Step 2: OTP Verification
            is RegisterEvent.UpdateOtpCode -> {
                setState { copy(otpCode = event.code, otpError = null, error = null) }
                if (event.code.length == 6) {
                    verifyOtp()
                }
            }
            is RegisterEvent.VerifyOtp -> verifyOtp()
            is RegisterEvent.ResendOtp -> resendOtp()

            // Step 3: Shop Setup
            is RegisterEvent.UpdateShopName -> {
                setState { copy(shopName = event.name, shopNameError = null, error = null) }
            }
            is RegisterEvent.UpdateBusinessType -> {
                setState { copy(businessType = event.type, businessTypeError = null, error = null) }
            }
            is RegisterEvent.UpdateShopAddress -> {
                setState { copy(shopAddress = event.address, error = null) }
            }
            is RegisterEvent.UpdateShopPhoneNumber -> {
                setState { copy(shopPhoneNumber = event.phone, error = null) }
            }
            is RegisterEvent.UpdateShopCurrency -> {
                setState { copy(shopCurrency = event.currency, error = null) }
            }
            is RegisterEvent.CreateShop -> createShopAndFinish()

            // Navigation
            is RegisterEvent.GoBack -> setEffect(RegisterEffect.NavigateToLogin)
            is RegisterEvent.GoToPreviousStep -> goToPreviousStep()
            is RegisterEvent.NavigateToLogin -> setEffect(RegisterEffect.NavigateToLogin)
            is RegisterEvent.ClearError -> setState { copy(error = null) }
        }
    }

    private fun submitAccountInfo() {
        if (!validateStep1()) return

        viewModelScope.launch {
            setState { copy(isLoading = true, isSendingOtp = true, error = null) }

            // Register user
            registerUseCase(
                name = currentState.fullName.trim(),
                email = currentState.email.trim().takeIf { it.isNotBlank() },
                phone = currentState.phoneNumber.trim().takeIf { it.isNotBlank() },
                password = currentState.password
            ).fold(
                ifLeft = { error ->
                    setState { copy(isLoading = false, isSendingOtp = false, error = error.message) }
                    setEffect(RegisterEffect.ShowSnackbar(error.message ?: "Registration failed"))
                },
                ifRight = { _ ->
                    // Send OTP
                    val target = currentState.phoneNumber.trim().takeIf { it.isNotBlank() }
                        ?: currentState.email.trim()

                    sendOtpUseCase(target).fold(
                        ifLeft = { error ->
                            setState { copy(isLoading = false, isSendingOtp = false, error = error.message) }
                        },
                        ifRight = {
                            setState {
                                copy(
                                    isLoading = false,
                                    isSendingOtp = false,
                                    currentStep = RegisterStep.OTP_VERIFICATION,
                                    otpResendCountdown = 60,
                                    canResendOtp = false
                                )
                            }
                            startOtpCountdown()
                            setEffect(RegisterEffect.ShowSnackbar("OTP sent to $target"))
                        }
                    )
                }
            )
        }
    }

    private fun validateStep1(): Boolean {
        var isValid = true

        if (currentState.fullName.isBlank()) {
            setState { copy(fullNameError = "Full name is required") }
            isValid = false
        }

        val hasEmail = currentState.email.isNotBlank()
        val hasPhone = currentState.phoneNumber.isNotBlank()

        if (!hasEmail && !hasPhone) {
            setState { copy(emailError = "Email or phone number is required") }
            isValid = false
        }

        if (hasEmail && !currentState.email.isValidEmail()) {
            setState { copy(emailError = "Invalid email format") }
            isValid = false
        }

        if (hasPhone && !currentState.phoneNumber.isValidPhone()) {
            setState { copy(phoneError = "Invalid phone number") }
            isValid = false
        }

        if (currentState.password.length < 6) {
            setState { copy(passwordError = "Password must be at least 6 characters") }
            isValid = false
        }

        if (currentState.password != currentState.confirmPassword) {
            setState { copy(confirmPasswordError = "Passwords do not match") }
            isValid = false
        }

        return isValid
    }

    private fun verifyOtp() {
        if (currentState.otpCode.length != 6) {
            setState { copy(otpError = "Please enter a valid 6-digit code") }
            return
        }

        viewModelScope.launch {
            setState { copy(isVerifyingOtp = true, error = null) }

            val target = currentState.phoneNumber.trim().takeIf { it.isNotBlank() }
                ?: currentState.email.trim()

            verifyOtpUseCase(target, currentState.otpCode).fold(
                ifLeft = { error ->
                    setState { copy(isVerifyingOtp = false, otpError = error.message) }
                    setEffect(RegisterEffect.ShowSnackbar(error.message ?: "Invalid OTP"))
                },
                ifRight = {
                    setState {
                        copy(
                            isVerifyingOtp = false,
                            currentStep = RegisterStep.SHOP_SETUP
                        )
                    }
                    setEffect(RegisterEffect.ShowSnackbar("Phone verified successfully!"))
                }
            )
        }
    }

    private fun resendOtp() {
        if (!currentState.canResendOtp) return

        viewModelScope.launch {
            setState { copy(isSendingOtp = true, error = null) }

            val target = currentState.phoneNumber.trim().takeIf { it.isNotBlank() }
                ?: currentState.email.trim()

            sendOtpUseCase(target).fold(
                ifLeft = { error ->
                    setState { copy(isSendingOtp = false, error = error.message) }
                },
                ifRight = {
                    setState {
                        copy(
                            isSendingOtp = false,
                            otpResendCountdown = 60,
                            canResendOtp = false,
                            otpCode = ""
                        )
                    }
                    startOtpCountdown()
                    setEffect(RegisterEffect.ShowSnackbar("OTP resent"))
                }
            )
        }
    }

    private fun startOtpCountdown() {
        viewModelScope.launch {
            while (currentState.otpResendCountdown > 0) {
                delay(1000)
                setState { copy(otpResendCountdown = otpResendCountdown - 1) }
            }
            setState { copy(canResendOtp = true) }
        }
    }

    private fun createShopAndFinish() {
        if (!validateStep3()) return

        viewModelScope.launch {
            setState { copy(isCreatingShop = true, error = null) }

            try {
                // Get current user ID from preferences
                val userPrefs = preferencesManager.userPreferencesFlow.first()
                val userId = userPrefs.userId ?: ""

                val now = DateTimeUtil.now()
                val shop = Shop(
                    id = UUID.randomUUID().toString(),
                    ownerId = userId,
                    name = currentState.shopName.trim(),
                    businessType = currentState.businessType,
                    address = currentState.shopAddress.trim().takeIf { it.isNotBlank() } ?: "",
                    phoneNumber = currentState.shopPhoneNumber.trim().takeIf { it.isNotBlank() },
                    currency = currentState.shopCurrency,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )

                // Use SetupNewShopUseCase to create shop with all defaults:
                // - Shop
                // - Owner as ShopMember
                // - Free subscription (or BASIC for TZS 12,000/month)
                // - Default ShopSettings
                setupNewShopUseCase(
                    shop = shop,
                    ownerId = userId,
                    subscriptionPlan = SubscriptionPlan.FREE, // Start with free plan
                    createTrialSubscription = true // Give 14-day trial of premium features
                ).fold(
                    ifLeft = { error ->
                        setState { copy(isCreatingShop = false, error = error.message) }
                        setEffect(RegisterEffect.ShowSnackbar(error.message ?: "Failed to create shop"))
                    },
                    ifRight = { result ->
                        // Set as active shop
                        setActiveShopUseCase(result.shop.id).fold(
                            ifLeft = { _ ->
                                // Shop created but failed to set as active - still proceed
                                setState { copy(isCreatingShop = false, registrationComplete = true) }
                                setEffect(RegisterEffect.NavigateToDashboard)
                            },
                            ifRight = {
                                setState { copy(isCreatingShop = false, registrationComplete = true) }
                                setEffect(RegisterEffect.ShowSnackbar("Welcome to MaiDuka! Your shop \"${result.shop.name}\" is ready."))
                                setEffect(RegisterEffect.NavigateToDashboard)
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                setState { copy(isCreatingShop = false, error = e.message) }
                setEffect(RegisterEffect.ShowSnackbar(e.message ?: "An error occurred"))
            }
        }
    }

    private fun validateStep3(): Boolean {
        var isValid = true

        if (currentState.shopName.isBlank()) {
            setState { copy(shopNameError = "Shop name is required") }
            isValid = false
        }

        if (currentState.businessType.isBlank()) {
            setState { copy(businessTypeError = "Please select a business type") }
            isValid = false
        }

        return isValid
    }

    private fun goToPreviousStep() {
        when (currentState.currentStep) {
            RegisterStep.ACCOUNT_INFO -> setEffect(RegisterEffect.NavigateToLogin)
            RegisterStep.OTP_VERIFICATION -> setState { copy(currentStep = RegisterStep.ACCOUNT_INFO) }
            RegisterStep.SHOP_SETUP -> setState { copy(currentStep = RegisterStep.OTP_VERIFICATION) }
        }
    }
}

