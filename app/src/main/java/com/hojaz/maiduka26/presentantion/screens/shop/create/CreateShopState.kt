package com.hojaz.maiduka26.presentantion.screens.shop.create

import com.hojaz.maiduka26.presentantion.base.ViewState

/**
 * State for the Create Shop screen.
 */
data class CreateShopState(
    val shopName: String = "",
    val businessType: String = "",
    val address: String = "",
    val phoneNumber: String = "",
    val currency: String = "TZS",

    // Validation errors
    val shopNameError: String? = null,
    val businessTypeError: String? = null,

    // Loading state
    val isLoading: Boolean = false,
    val error: String? = null,

    // Success flag
    val isShopCreated: Boolean = false
) : ViewState {

    val isValid: Boolean
        get() = shopName.isNotBlank() && businessType.isNotBlank()
}

/**
 * Available business types for shop creation.
 */
val BUSINESS_TYPES = listOf(
    "retail" to "Retail Store",
    "wholesale" to "Wholesale",
    "restaurant" to "Restaurant/Cafe",
    "pharmacy" to "Pharmacy",
    "electronics" to "Electronics",
    "clothing" to "Clothing & Fashion",
    "grocery" to "Grocery",
    "hardware" to "Hardware Store",
    "beauty" to "Beauty & Cosmetics",
    "mobile_money" to "Mobile Money Agent",
    "supermarket" to "Supermarket",
    "other" to "Other"
)

/**
 * Available currencies.
 */
val CURRENCIES = listOf(
    "TZS" to "Tanzanian Shilling (TZS)",
    "KES" to "Kenyan Shilling (KES)",
    "UGX" to "Ugandan Shilling (UGX)",
    "USD" to "US Dollar (USD)"
)

/**
 * Events for the Create Shop screen.
 */
sealed class CreateShopEvent {
    data class UpdateShopName(val name: String) : CreateShopEvent()
    data class UpdateBusinessType(val type: String) : CreateShopEvent()
    data class UpdateAddress(val address: String) : CreateShopEvent()
    data class UpdatePhoneNumber(val phone: String) : CreateShopEvent()
    data class UpdateCurrency(val currency: String) : CreateShopEvent()
    data object CreateShop : CreateShopEvent()
    data object ClearError : CreateShopEvent()
    data object NavigateBack : CreateShopEvent()
}

/**
 * Side effects for the Create Shop screen.
 */
sealed class CreateShopEffect {
    data object NavigateToDashboard : CreateShopEffect()
    data object NavigateBack : CreateShopEffect()
    data class ShowSnackbar(val message: String) : CreateShopEffect()
}

