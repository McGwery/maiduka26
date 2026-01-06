package com.hojaz.maiduka26.presentantion.screens.shop.settings

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.model.MemberRole
import com.hojaz.maiduka26.domain.model.ShopSettings
import com.hojaz.maiduka26.domain.usecase.settings.*
import com.hojaz.maiduka26.domain.usecase.shop.GetShopByIdUseCase
import com.hojaz.maiduka26.domain.usecase.subscription.GetActiveSubscriptionUseCase
import com.hojaz.maiduka26.domain.usecase.subscription.GetSubscriptionDaysRemainingUseCase
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Shop Settings screen.
 */
@HiltViewModel
class ShopSettingsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getShopByIdUseCase: GetShopByIdUseCase,
    private val getShopSettingsUseCase: GetShopSettingsUseCase,
    private val saveShopSettingsUseCase: SaveShopSettingsUseCase,
    private val getShopMembersUseCase: GetShopMembersUseCase,
    private val addShopMemberUseCase: AddShopMemberUseCase,
    private val updateMemberRoleUseCase: UpdateMemberRoleUseCase,
    private val removeMemberUseCase: RemoveMemberUseCase,
    private val deactivateMemberUseCase: DeactivateMemberUseCase,
    private val reactivateMemberUseCase: ReactivateMemberUseCase,
    private val getMemberCountUseCase: GetMemberCountUseCase,
    private val getActiveSubscriptionUseCase: GetActiveSubscriptionUseCase,
    private val getDaysRemainingUseCase: GetSubscriptionDaysRemainingUseCase
) : BaseViewModel<ShopSettingsState, ShopSettingsEvent, ShopSettingsEffect>() {

    private val shopId: String = savedStateHandle.get<String>("shopId") ?: ""

    override fun createInitialState(): ShopSettingsState = ShopSettingsState()

    init {
        if (shopId.isNotEmpty()) {
            loadSettings()
        }
    }

    override fun onEvent(event: ShopSettingsEvent) {
        when (event) {
            is ShopSettingsEvent.LoadSettings -> loadSettings()
            is ShopSettingsEvent.SelectTab -> setState { copy(selectedTab = event.tab) }

            // General settings
            is ShopSettingsEvent.UpdateShopName -> setState { copy(shopName = event.name) }
            is ShopSettingsEvent.UpdateBusinessType -> setState { copy(businessType = event.type) }
            is ShopSettingsEvent.UpdatePhoneNumber -> setState { copy(phoneNumber = event.phone) }
            is ShopSettingsEvent.UpdateAddress -> setState { copy(address = event.address) }
            is ShopSettingsEvent.UpdateCurrency -> setState { copy(currency = event.currency) }

            // Notification settings
            is ShopSettingsEvent.UpdateSmsNotifications -> setState { copy(enableSmsNotifications = event.enabled) }
            is ShopSettingsEvent.UpdateEmailNotifications -> setState { copy(enableEmailNotifications = event.enabled) }
            is ShopSettingsEvent.UpdateNotifyLowStock -> setState { copy(notifyLowStock = event.enabled) }
            is ShopSettingsEvent.UpdateLowStockThreshold -> setState { copy(lowStockThreshold = event.threshold) }
            is ShopSettingsEvent.UpdateDailySummary -> setState { copy(notifyDailySalesSummary = event.enabled) }

            // Sales settings
            is ShopSettingsEvent.UpdateAllowCreditSales -> setState { copy(allowCreditSales = event.enabled) }
            is ShopSettingsEvent.UpdateAllowDiscounts -> setState { copy(allowDiscounts = event.enabled) }
            is ShopSettingsEvent.UpdateMaxDiscount -> setState { copy(maxDiscountPercentage = event.percentage) }
            is ShopSettingsEvent.UpdateRequireCustomerForCredit -> setState { copy(requireCustomerForCredit = event.required) }

            // Inventory settings
            is ShopSettingsEvent.UpdateTrackStock -> setState { copy(trackStock = event.enabled) }
            is ShopSettingsEvent.UpdateAllowNegativeStock -> setState { copy(allowNegativeStock = event.enabled) }
            is ShopSettingsEvent.UpdateAutoDeductStock -> setState { copy(autoDeductStockOnSale = event.enabled) }

            // Receipt settings
            is ShopSettingsEvent.UpdateReceiptHeader -> setState { copy(receiptHeader = event.header) }
            is ShopSettingsEvent.UpdateReceiptFooter -> setState { copy(receiptFooter = event.footer) }
            is ShopSettingsEvent.UpdateShowLogo -> setState { copy(showShopLogoOnReceipt = event.show) }
            is ShopSettingsEvent.UpdateShowTax -> setState { copy(showTaxOnReceipt = event.show) }
            is ShopSettingsEvent.UpdateTaxPercentage -> setState { copy(taxPercentage = event.percentage) }

            // Members
            is ShopSettingsEvent.ShowAddMemberDialog -> setState { copy(showAddMemberDialog = true) }
            is ShopSettingsEvent.HideAddMemberDialog -> setState { copy(showAddMemberDialog = false) }
            is ShopSettingsEvent.ShowEditMemberDialog -> setState { copy(showEditMemberDialog = true, selectedMember = event.member) }
            is ShopSettingsEvent.HideEditMemberDialog -> setState { copy(showEditMemberDialog = false, selectedMember = null) }
            is ShopSettingsEvent.AddMember -> addMember(event.userId, event.role)
            is ShopSettingsEvent.UpdateMemberRole -> updateMemberRole(event.memberId, event.role)
            is ShopSettingsEvent.RemoveMember -> removeMember(event.memberId)
            is ShopSettingsEvent.ToggleMemberActive -> toggleMemberActive(event.memberId, event.active)

            // Subscription
            is ShopSettingsEvent.ShowSubscriptionDialog -> setState { copy(showSubscriptionDialog = true) }
            is ShopSettingsEvent.HideSubscriptionDialog -> setState { copy(showSubscriptionDialog = false) }
            is ShopSettingsEvent.NavigateToSubscription -> setEffect(ShopSettingsEffect.NavigateToSubscription)

            // Actions
            is ShopSettingsEvent.SaveSettings -> saveSettings()
            is ShopSettingsEvent.NavigateBack -> setEffect(ShopSettingsEffect.NavigateBack)
        }
    }

    private fun loadSettings() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                // Load shop
                getShopByIdUseCase(shopId).fold(
                    ifLeft = { error ->
                        setState { copy(isLoading = false, error = error.message) }
                    },
                    ifRight = { shop ->
                        shop?.let {
                            setState {
                                copy(
                                    shop = it,
                                    shopName = it.name,
                                    businessType = it.businessType,
                                    phoneNumber = it.phoneNumber ?: "",
                                    address = it.address,
                                    currency = it.currency
                                )
                            }
                        }
                    }
                )

                // Load settings
                launch {
                    getShopSettingsUseCase(shopId).collectLatest { settings ->
                        settings?.let {
                            setState {
                                copy(
                                    settings = it,
                                    enableSmsNotifications = it.enableSmsNotifications,
                                    enableEmailNotifications = it.enableEmailNotifications,
                                    notifyLowStock = it.notifyLowStock,
                                    lowStockThreshold = it.lowStockThreshold,
                                    notifyDailySalesSummary = it.notifyDailySalesSummary,
                                    allowCreditSales = it.allowCreditSales,
                                    allowDiscounts = it.allowDiscounts,
                                    maxDiscountPercentage = it.maxDiscountPercentage,
                                    requireCustomerForCredit = it.requireCustomerForCredit,
                                    trackStock = it.trackStock,
                                    allowNegativeStock = it.allowNegativeStock,
                                    autoDeductStockOnSale = it.autoDeductStockOnSale,
                                    receiptHeader = it.receiptHeader ?: "",
                                    receiptFooter = it.receiptFooter ?: "",
                                    showShopLogoOnReceipt = it.showShopLogoOnReceipt,
                                    showTaxOnReceipt = it.showTaxOnReceipt,
                                    taxPercentage = it.taxPercentage
                                )
                            }
                        }
                    }
                }

                // Load members
                launch {
                    getShopMembersUseCase(shopId).collectLatest { members ->
                        setState { copy(members = members, memberCount = members.size) }
                    }
                }

                // Load subscription
                launch {
                    getActiveSubscriptionUseCase(shopId).collectLatest { subscription ->
                        setState { copy(subscription = subscription) }
                    }
                }

                // Load days remaining
                launch {
                    val days = getDaysRemainingUseCase(shopId)
                    setState { copy(daysRemaining = days) }
                }

                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun saveSettings() {
        viewModelScope.launch {
            setState { copy(isSaving = true) }

            try {
                val existingSettings = currentState.settings
                val settings = ShopSettings(
                    id = existingSettings?.id ?: UUID.randomUUID().toString(),
                    shopId = shopId,
                    enableSmsNotifications = currentState.enableSmsNotifications,
                    enableEmailNotifications = currentState.enableEmailNotifications,
                    notifyLowStock = currentState.notifyLowStock,
                    lowStockThreshold = currentState.lowStockThreshold,
                    notifyDailySalesSummary = currentState.notifyDailySalesSummary,
                    allowCreditSales = currentState.allowCreditSales,
                    allowDiscounts = currentState.allowDiscounts,
                    maxDiscountPercentage = currentState.maxDiscountPercentage,
                    requireCustomerForCredit = currentState.requireCustomerForCredit,
                    trackStock = currentState.trackStock,
                    allowNegativeStock = currentState.allowNegativeStock,
                    autoDeductStockOnSale = currentState.autoDeductStockOnSale,
                    receiptHeader = currentState.receiptHeader.takeIf { it.isNotBlank() },
                    receiptFooter = currentState.receiptFooter.takeIf { it.isNotBlank() },
                    showShopLogoOnReceipt = currentState.showShopLogoOnReceipt,
                    showTaxOnReceipt = currentState.showTaxOnReceipt,
                    taxPercentage = currentState.taxPercentage
                )

                saveShopSettingsUseCase(settings).fold(
                    ifLeft = { error ->
                        setState { copy(isSaving = false) }
                        setEffect(ShopSettingsEffect.ShowSnackbar(error.message ?: "Failed to save settings"))
                    },
                    ifRight = {
                        setState { copy(isSaving = false, saveSuccess = true) }
                        setEffect(ShopSettingsEffect.ShowSnackbar("Settings saved successfully"))
                        setEffect(ShopSettingsEffect.SettingsSaved)
                    }
                )
            } catch (e: Exception) {
                setState { copy(isSaving = false) }
                setEffect(ShopSettingsEffect.ShowSnackbar(e.message ?: "Failed to save settings"))
            }
        }
    }

    private fun addMember(userId: String, role: String) {
        viewModelScope.launch {
            try {
                val memberRole = MemberRole.valueOf(role.uppercase())
                addShopMemberUseCase(shopId, userId, memberRole).fold(
                    ifLeft = { error ->
                        setEffect(ShopSettingsEffect.ShowSnackbar(error.message ?: "Failed to add member"))
                    },
                    ifRight = {
                        setState { copy(showAddMemberDialog = false) }
                        setEffect(ShopSettingsEffect.ShowSnackbar("Member added successfully"))
                    }
                )
            } catch (e: Exception) {
                setEffect(ShopSettingsEffect.ShowSnackbar(e.message ?: "Invalid role"))
            }
        }
    }

    private fun updateMemberRole(memberId: String, role: String) {
        viewModelScope.launch {
            try {
                val memberRole = MemberRole.valueOf(role.uppercase())
                updateMemberRoleUseCase(memberId, memberRole).fold(
                    ifLeft = { error ->
                        setEffect(ShopSettingsEffect.ShowSnackbar(error.message ?: "Failed to update role"))
                    },
                    ifRight = {
                        setState { copy(showEditMemberDialog = false, selectedMember = null) }
                        setEffect(ShopSettingsEffect.ShowSnackbar("Role updated successfully"))
                    }
                )
            } catch (e: Exception) {
                setEffect(ShopSettingsEffect.ShowSnackbar(e.message ?: "Invalid role"))
            }
        }
    }

    private fun removeMember(memberId: String) {
        viewModelScope.launch {
            removeMemberUseCase(memberId).fold(
                ifLeft = { error ->
                    setEffect(ShopSettingsEffect.ShowSnackbar(error.message ?: "Failed to remove member"))
                },
                ifRight = {
                    setEffect(ShopSettingsEffect.ShowSnackbar("Member removed successfully"))
                }
            )
        }
    }

    private fun toggleMemberActive(memberId: String, active: Boolean) {
        viewModelScope.launch {
            val result = if (active) {
                reactivateMemberUseCase(memberId)
            } else {
                deactivateMemberUseCase(memberId)
            }

            result.fold(
                ifLeft = { error ->
                    setEffect(ShopSettingsEffect.ShowSnackbar(error.message ?: "Failed to update member"))
                },
                ifRight = {
                    val message = if (active) "Member activated" else "Member deactivated"
                    setEffect(ShopSettingsEffect.ShowSnackbar(message))
                }
            )
        }
    }
}

