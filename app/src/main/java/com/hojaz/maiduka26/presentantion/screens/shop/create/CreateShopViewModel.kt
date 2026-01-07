package com.hojaz.maiduka26.presentantion.screens.shop.create

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.domain.model.SubscriptionPlan
import com.hojaz.maiduka26.domain.usecase.shop.SetActiveShopUseCase
import com.hojaz.maiduka26.domain.usecase.shop.SetupNewShopUseCase
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import com.hojaz.maiduka26.util.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Create Shop screen.
 * Used for standalone shop creation (after login when no shop exists).
 */
@HiltViewModel
class CreateShopViewModel @Inject constructor(
    private val setupNewShopUseCase: SetupNewShopUseCase,
    private val setActiveShopUseCase: SetActiveShopUseCase,
    private val preferencesManager: PreferencesManager
) : BaseViewModel<CreateShopState, CreateShopEvent, CreateShopEffect>() {

    override fun createInitialState(): CreateShopState = CreateShopState()

    override fun onEvent(event: CreateShopEvent) {
        when (event) {
            is CreateShopEvent.UpdateShopName -> {
                setState { copy(shopName = event.name, shopNameError = null, error = null) }
            }
            is CreateShopEvent.UpdateBusinessType -> {
                setState { copy(businessType = event.type, businessTypeError = null, error = null) }
            }
            is CreateShopEvent.UpdateAddress -> {
                setState { copy(address = event.address, error = null) }
            }
            is CreateShopEvent.UpdatePhoneNumber -> {
                setState { copy(phoneNumber = event.phone, error = null) }
            }
            is CreateShopEvent.UpdateCurrency -> {
                setState { copy(currency = event.currency, error = null) }
            }
            is CreateShopEvent.CreateShop -> createShop()
            is CreateShopEvent.ClearError -> setState { copy(error = null) }
            is CreateShopEvent.NavigateBack -> setEffect(CreateShopEffect.NavigateBack)
        }
    }

    private fun createShop() {
        if (!validateInput()) return

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val userPrefs = preferencesManager.userPreferencesFlow.first()
                val userId = userPrefs.userId

                if (userId.isNullOrBlank()) {
                    setState { copy(isLoading = false, error = "Session expired. Please login again.") }
                    return@launch
                }

                val now = DateTimeUtil.now()
                val shop = Shop(
                    id = UUID.randomUUID().toString(),
                    ownerId = userId,
                    name = currentState.shopName.trim(),
                    businessType = currentState.businessType,
                    address = currentState.address.trim().takeIf { it.isNotBlank() } ?: "",
                    phoneNumber = currentState.phoneNumber.trim().takeIf { it.isNotBlank() },
                    currency = currentState.currency,
                    isActive = true,
                    createdAt = now,
                    updatedAt = now
                )

                // Create shop with default subscription (14-day trial)
                setupNewShopUseCase(
                    shop = shop,
                    ownerId = userId,
                    subscriptionPlan = SubscriptionPlan.FREE,
                    createTrialSubscription = true
                ).fold(
                    ifLeft = { error ->
                        setState { copy(isLoading = false, error = error.message) }
                        setEffect(CreateShopEffect.ShowSnackbar(error.message ?: "Failed to create shop"))
                    },
                    ifRight = { result ->
                        // Set as active shop
                        setActiveShopUseCase(result.shop.id).fold(
                            ifLeft = { _ ->
                                // Shop created but failed to set active - still complete registration
                                completeAndNavigate(result.shop.name)
                            },
                            ifRight = {
                                completeAndNavigate(result.shop.name)
                            }
                        )
                    }
                )
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
                setEffect(CreateShopEffect.ShowSnackbar(e.message ?: "An error occurred"))
            }
        }
    }

    private suspend fun completeAndNavigate(shopName: String) {
        // Complete registration - sets isLoggedIn = true
        preferencesManager.completeRegistration()

        setState { copy(isLoading = false, isShopCreated = true) }
        setEffect(CreateShopEffect.ShowSnackbar("Welcome! Your shop \"$shopName\" is ready."))
        setEffect(CreateShopEffect.NavigateToDashboard)
    }

    private fun validateInput(): Boolean {
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
}

