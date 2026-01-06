package com.hojaz.maiduka26.presentantion.screens.shop.settings

import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.domain.model.ShopMember
import com.hojaz.maiduka26.domain.model.ShopSettings
import com.hojaz.maiduka26.domain.model.Subscription
import com.hojaz.maiduka26.presentantion.base.ViewState
import java.math.BigDecimal

/**
 * State for the Shop Settings screen.
 */
data class ShopSettingsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val shop: Shop? = null,
    val settings: ShopSettings? = null,
    val subscription: Subscription? = null,
    val members: List<ShopMember> = emptyList(),
    val memberCount: Int = 0,
    val daysRemaining: Int = 0,

    // Settings tab
    val selectedTab: SettingsTab = SettingsTab.GENERAL,

    // Form fields - General
    val shopName: String = "",
    val businessType: String = "",
    val phoneNumber: String = "",
    val address: String = "",
    val currency: String = "TZS",

    // Form fields - Notifications
    val enableSmsNotifications: Boolean = true,
    val enableEmailNotifications: Boolean = false,
    val notifyLowStock: Boolean = true,
    val lowStockThreshold: Int = 10,
    val notifyDailySalesSummary: Boolean = false,

    // Form fields - Sales
    val allowCreditSales: Boolean = true,
    val allowDiscounts: Boolean = true,
    val maxDiscountPercentage: BigDecimal = BigDecimal("20.00"),
    val requireCustomerForCredit: Boolean = true,

    // Form fields - Inventory
    val trackStock: Boolean = true,
    val allowNegativeStock: Boolean = false,
    val autoDeductStockOnSale: Boolean = true,

    // Form fields - Receipt
    val receiptHeader: String = "",
    val receiptFooter: String = "",
    val showShopLogoOnReceipt: Boolean = true,
    val showTaxOnReceipt: Boolean = false,
    val taxPercentage: BigDecimal = BigDecimal.ZERO,

    // Dialogs
    val showAddMemberDialog: Boolean = false,
    val showEditMemberDialog: Boolean = false,
    val showSubscriptionDialog: Boolean = false,
    val showDeleteConfirmDialog: Boolean = false,
    val selectedMember: ShopMember? = null,

    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false
) : ViewState

enum class SettingsTab {
    GENERAL, NOTIFICATIONS, SALES, INVENTORY, RECEIPT, MEMBERS, SUBSCRIPTION
}

/**
 * Events for the Shop Settings screen.
 */
sealed class ShopSettingsEvent {
    data object LoadSettings : ShopSettingsEvent()
    data class SelectTab(val tab: SettingsTab) : ShopSettingsEvent()

    // General settings
    data class UpdateShopName(val name: String) : ShopSettingsEvent()
    data class UpdateBusinessType(val type: String) : ShopSettingsEvent()
    data class UpdatePhoneNumber(val phone: String) : ShopSettingsEvent()
    data class UpdateAddress(val address: String) : ShopSettingsEvent()
    data class UpdateCurrency(val currency: String) : ShopSettingsEvent()

    // Notification settings
    data class UpdateSmsNotifications(val enabled: Boolean) : ShopSettingsEvent()
    data class UpdateEmailNotifications(val enabled: Boolean) : ShopSettingsEvent()
    data class UpdateNotifyLowStock(val enabled: Boolean) : ShopSettingsEvent()
    data class UpdateLowStockThreshold(val threshold: Int) : ShopSettingsEvent()
    data class UpdateDailySummary(val enabled: Boolean) : ShopSettingsEvent()

    // Sales settings
    data class UpdateAllowCreditSales(val enabled: Boolean) : ShopSettingsEvent()
    data class UpdateAllowDiscounts(val enabled: Boolean) : ShopSettingsEvent()
    data class UpdateMaxDiscount(val percentage: BigDecimal) : ShopSettingsEvent()
    data class UpdateRequireCustomerForCredit(val required: Boolean) : ShopSettingsEvent()

    // Inventory settings
    data class UpdateTrackStock(val enabled: Boolean) : ShopSettingsEvent()
    data class UpdateAllowNegativeStock(val enabled: Boolean) : ShopSettingsEvent()
    data class UpdateAutoDeductStock(val enabled: Boolean) : ShopSettingsEvent()

    // Receipt settings
    data class UpdateReceiptHeader(val header: String) : ShopSettingsEvent()
    data class UpdateReceiptFooter(val footer: String) : ShopSettingsEvent()
    data class UpdateShowLogo(val show: Boolean) : ShopSettingsEvent()
    data class UpdateShowTax(val show: Boolean) : ShopSettingsEvent()
    data class UpdateTaxPercentage(val percentage: BigDecimal) : ShopSettingsEvent()

    // Members
    data object ShowAddMemberDialog : ShopSettingsEvent()
    data object HideAddMemberDialog : ShopSettingsEvent()
    data class ShowEditMemberDialog(val member: ShopMember) : ShopSettingsEvent()
    data object HideEditMemberDialog : ShopSettingsEvent()
    data class AddMember(val userId: String, val role: String) : ShopSettingsEvent()
    data class UpdateMemberRole(val memberId: String, val role: String) : ShopSettingsEvent()
    data class RemoveMember(val memberId: String) : ShopSettingsEvent()
    data class ToggleMemberActive(val memberId: String, val active: Boolean) : ShopSettingsEvent()

    // Subscription
    data object ShowSubscriptionDialog : ShopSettingsEvent()
    data object HideSubscriptionDialog : ShopSettingsEvent()
    data object NavigateToSubscription : ShopSettingsEvent()

    // Actions
    data object SaveSettings : ShopSettingsEvent()
    data object NavigateBack : ShopSettingsEvent()
}

/**
 * Side effects for the Shop Settings screen.
 */
sealed class ShopSettingsEffect {
    data object NavigateBack : ShopSettingsEffect()
    data object NavigateToSubscription : ShopSettingsEffect()
    data class ShowSnackbar(val message: String) : ShopSettingsEffect()
    data object SettingsSaved : ShopSettingsEffect()
}

