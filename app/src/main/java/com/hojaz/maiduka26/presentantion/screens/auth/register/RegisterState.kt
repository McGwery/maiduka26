package com.hojaz.maiduka26.presentantion.screens.auth.register

import com.hojaz.maiduka26.presentantion.base.ViewState

/**
 * State for the Register screen.
 */
data class RegisterState(
    val currentStep: RegisterStep = RegisterStep.ACCOUNT_INFO,

    // Step 1: Account Info
    val fullName: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val password: String = "",
    val confirmPassword: String = "",

    // Step 2: OTP Verification
    val otpCode: String = "",
    val otpResendCountdown: Int = 0,
    val canResendOtp: Boolean = false,

    // Step 3: Shop Setup
    val shopName: String = "",
    val businessType: String = "",
    val shopAddress: String = "",
    val shopPhoneNumber: String = "",
    val shopCurrency: String = "TZS",

    // Validation errors
    val fullNameError: String? = null,
    val emailError: String? = null,
    val phoneError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val otpError: String? = null,
    val shopNameError: String? = null,
    val businessTypeError: String? = null,

    // Loading states
    val isLoading: Boolean = false,
    val isSendingOtp: Boolean = false,
    val isVerifyingOtp: Boolean = false,
    val isCreatingShop: Boolean = false,

    val error: String? = null,
    val registrationComplete: Boolean = false
) : ViewState {

    val isStep1Valid: Boolean
        get() = fullName.isNotBlank() &&
                (email.isNotBlank() || phoneNumber.isNotBlank()) &&
                password.length >= 6 &&
                password == confirmPassword

    val isStep2Valid: Boolean
        get() = otpCode.length == 6

    val isStep3Valid: Boolean
        get() = shopName.isNotBlank() && businessType.isNotBlank()

    val progressPercentage: Float
        get() = when (currentStep) {
            RegisterStep.ACCOUNT_INFO -> 0.33f
            RegisterStep.OTP_VERIFICATION -> 0.66f
            RegisterStep.SHOP_SETUP -> 1.0f
        }
}

enum class RegisterStep {
    ACCOUNT_INFO,
    OTP_VERIFICATION,
    SHOP_SETUP
}

/**
 * Available business types for shop registration.
 */
val BUSINESS_TYPES = listOf(
    "Retail Store",
    "Wholesale",
    "Restaurant/Cafe",
    "Pharmacy",
    "Electronics",
    "Clothing & Fashion",
    "Grocery",
    "Hardware Store",
    "Beauty & Cosmetics",
    "Mobile Money Agent",
    "Supermarket",
    "Other"
)

/**
 * Available currencies.
 */
val CURRENCIES = listOf(
    "TZS" to "Tanzanian Shilling",
    "KES" to "Kenyan Shilling",
    "UGX" to "Ugandan Shilling",
    "USD" to "US Dollar"
)

/**
 * Events for the Register screen.
 */
sealed class RegisterEvent {
    // Step 1: Account Info
    data class UpdateFullName(val name: String) : RegisterEvent()
    data class UpdateEmail(val email: String) : RegisterEvent()
    data class UpdatePhoneNumber(val phone: String) : RegisterEvent()
    data class UpdatePassword(val password: String) : RegisterEvent()
    data class UpdateConfirmPassword(val password: String) : RegisterEvent()
    data object SubmitAccountInfo : RegisterEvent()

    // Step 2: OTP Verification
    data class UpdateOtpCode(val code: String) : RegisterEvent()
    data object VerifyOtp : RegisterEvent()
    data object ResendOtp : RegisterEvent()

    // Step 3: Shop Setup
    data class UpdateShopName(val name: String) : RegisterEvent()
    data class UpdateBusinessType(val type: String) : RegisterEvent()
    data class UpdateShopAddress(val address: String) : RegisterEvent()
    data class UpdateShopPhoneNumber(val phone: String) : RegisterEvent()
    data class UpdateShopCurrency(val currency: String) : RegisterEvent()
    data object CreateShop : RegisterEvent()

    // Navigation
    data object GoBack : RegisterEvent()
    data object GoToPreviousStep : RegisterEvent()
    data object NavigateToLogin : RegisterEvent()
    data object ClearError : RegisterEvent()
}

/**
 * Side effects for the Register screen.
 */
sealed class RegisterEffect {
    data object NavigateToLogin : RegisterEffect()
    data object NavigateToDashboard : RegisterEffect()
    data class ShowSnackbar(val message: String) : RegisterEffect()
    data object StartOtpCountdown : RegisterEffect()
}

