package com.hojaz.maiduka26.domain.usecase.shop

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.domain.model.*
import com.hojaz.maiduka26.domain.repository.ShopRepository
import com.hojaz.maiduka26.domain.repository.ShopSettingsRepository
import com.hojaz.maiduka26.util.DateTimeUtil
import timber.log.Timber
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Data class representing the result of shop setup.
 */
data class ShopSetupResult(
    val shop: Shop,
    val shopMember: ShopMember,
    val subscription: Subscription,
    val shopSettings: ShopSettings
)

/**
 * Use case for setting up a new shop with all necessary defaults.
 * This creates:
 * 1. The shop
 * 2. Owner as shop member
 * 3. Default free subscription (or monthly basic - TZS 12,000)
 * 4. Default shop settings
 *
 * This is used during registration to create the first shop.
 */
class SetupNewShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository,
    private val shopSettingsRepository: ShopSettingsRepository
) {
    /**
     * Creates a new shop with all default configurations.
     *
     * @param shop The shop to create
     * @param ownerId The user ID of the owner
     * @param subscriptionPlan The plan for the shop (default is FREE, can be BASIC for TZS 12,000/month)
     * @param createTrialSubscription Whether to create a 14-day trial for premium features
     * @return Either an error or the shop setup result
     */
    suspend operator fun invoke(
        shop: Shop,
        ownerId: String,
        subscriptionPlan: SubscriptionPlan = SubscriptionPlan.FREE,
        createTrialSubscription: Boolean = false
    ): Either<Throwable, ShopSetupResult> {
        return try {
            // Step 1: Create the shop
            val createdShop = shopRepository.createShop(shop).fold(
                ifLeft = { return it.left() },
                ifRight = { it }
            )
            Timber.d("Shop created: ${createdShop.id}")

            val now = DateTimeUtil.now()

            // Step 2: Create the owner as a shop member with all permissions
            val ownerMember = ShopMember(
                id = UUID.randomUUID().toString(),
                shopId = createdShop.id,
                userId = ownerId,
                role = MemberRole.OWNER,
                permissions = Permission.entries.toList(), // Owners have all permissions
                isActive = true,
                createdAt = now,
                updatedAt = now
            )

            val createdMember = shopSettingsRepository.addMember(ownerMember).fold(
                ifLeft = { error ->
                    Timber.e(error, "Failed to create owner member, but shop was created")
                    // Continue with shop creation even if member creation fails
                    ownerMember
                },
                ifRight = { it }
            )
            Timber.d("Owner member created: ${createdMember.id}")

            // Step 3: Create subscription
            val subscriptionPrice = when (subscriptionPlan) {
                SubscriptionPlan.FREE -> BigDecimal.ZERO
                SubscriptionPlan.BASIC -> BigDecimal("12000.00") // TZS 12,000/month
                SubscriptionPlan.PREMIUM -> BigDecimal("25000.00")
                SubscriptionPlan.ENTERPRISE -> BigDecimal("50000.00")
            }

            val expiresAt: LocalDateTime? = when {
                subscriptionPlan == SubscriptionPlan.FREE -> null // Never expires
                createTrialSubscription -> DateTimeUtil.plusDays(now, 14) // 14-day trial
                else -> DateTimeUtil.plusDays(now, 30) // 30 days for paid plans
            }

            val subscriptionStatus = when {
                subscriptionPlan == SubscriptionPlan.FREE -> SubscriptionStatus.ACTIVE
                createTrialSubscription -> SubscriptionStatus.ACTIVE // Trial is active
                else -> SubscriptionStatus.PENDING // Needs payment
            }

            val subscription = Subscription(
                id = UUID.randomUUID().toString(),
                shopId = createdShop.id,
                plan = subscriptionPlan,
                type = SubscriptionType.OFFLINE,
                status = subscriptionStatus,
                price = subscriptionPrice,
                currency = shop.currency.ifBlank { "TZS" },
                startsAt = now,
                expiresAt = expiresAt,
                autoRenew = subscriptionPlan != SubscriptionPlan.FREE,
                features = getFeatures(subscriptionPlan),
                maxUsers = getMaxUsers(subscriptionPlan),
                maxProducts = getMaxProducts(subscriptionPlan),
                notes = if (createTrialSubscription) "14-day free trial" else null,
                createdAt = now,
                updatedAt = now
            )

            val createdSubscription = shopSettingsRepository.createSubscription(subscription).fold(
                ifLeft = { error ->
                    Timber.e(error, "Failed to create subscription, but shop was created")
                    subscription
                },
                ifRight = { it }
            )
            Timber.d("Subscription created: ${createdSubscription.id}")

            // Step 4: Create default shop settings
            val shopSettings = ShopSettings(
                id = UUID.randomUUID().toString(),
                shopId = createdShop.id,
                enableSmsNotifications = true,
                enableEmailNotifications = false,
                notifyLowStock = true,
                lowStockThreshold = 10,
                autoPrintReceipt = false,
                allowCreditSales = true,
                creditLimitDays = 30,
                requireCustomerForCredit = true,
                allowDiscounts = true,
                maxDiscountPercentage = BigDecimal("20.00"),
                trackStock = true,
                allowNegativeStock = false,
                autoDeductStockOnSale = true,
                stockValuationMethod = StockValuationMethod.FIFO,
                receiptHeader = shop.name,
                receiptFooter = "Thank you for your business!",
                showShopLogoOnReceipt = true,
                openingTime = "08:00",
                closingTime = "20:00",
                workingDays = listOf(1, 2, 3, 4, 5, 6), // Monday to Saturday
                language = "sw", // Swahili
                timezone = "Africa/Dar_es_Salaam",
                dateFormat = "d/m/Y",
                timeFormat = "H:i",
                createdAt = now,
                updatedAt = now
            )

            val createdSettings = shopSettingsRepository.saveShopSettings(shopSettings).fold(
                ifLeft = { error ->
                    Timber.e(error, "Failed to create shop settings, but shop was created")
                    shopSettings
                },
                ifRight = { it }
            )
            Timber.d("Shop settings created: ${createdSettings.id}")

            ShopSetupResult(
                shop = createdShop,
                shopMember = createdMember,
                subscription = createdSubscription,
                shopSettings = createdSettings
            ).right()

        } catch (e: Exception) {
            Timber.e(e, "Error setting up new shop")
            e.left()
        }
    }

    private fun getFeatures(plan: SubscriptionPlan): List<String> {
        return when (plan) {
            SubscriptionPlan.FREE -> listOf(
                "Up to 100 products",
                "1 user",
                "Basic features",
                "Offline mode"
            )
            SubscriptionPlan.BASIC -> listOf(
                "Up to 500 products",
                "Up to 3 users",
                "Basic reports",
                "Email support",
                "Offline mode"
            )
            SubscriptionPlan.PREMIUM -> listOf(
                "Unlimited products",
                "Up to 10 users",
                "Advanced reports",
                "Priority support",
                "Offline mode",
                "Multi-shop management",
                "Customer credit management"
            )
            SubscriptionPlan.ENTERPRISE -> listOf(
                "Unlimited products",
                "Unlimited users",
                "Advanced analytics",
                "24/7 phone support",
                "Offline mode",
                "Multi-shop management",
                "Customer credit management",
                "B2B marketplace access",
                "Custom integrations"
            )
        }
    }

    private fun getMaxUsers(plan: SubscriptionPlan): Int? {
        return when (plan) {
            SubscriptionPlan.FREE -> 1
            SubscriptionPlan.BASIC -> 3
            SubscriptionPlan.PREMIUM -> 10
            SubscriptionPlan.ENTERPRISE -> null // Unlimited
        }
    }

    private fun getMaxProducts(plan: SubscriptionPlan): Int? {
        return when (plan) {
            SubscriptionPlan.FREE -> 100
            SubscriptionPlan.BASIC -> 500
            SubscriptionPlan.PREMIUM -> null // Unlimited
            SubscriptionPlan.ENTERPRISE -> null // Unlimited
        }
    }
}

