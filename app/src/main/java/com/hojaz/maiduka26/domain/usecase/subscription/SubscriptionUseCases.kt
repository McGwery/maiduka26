package com.hojaz.maiduka26.domain.usecase.subscription

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Subscription
import com.hojaz.maiduka26.domain.model.SubscriptionPlan
import com.hojaz.maiduka26.domain.model.SubscriptionStatus
import com.hojaz.maiduka26.domain.model.SubscriptionType
import com.hojaz.maiduka26.domain.repository.ShopSettingsRepository
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * Subscription pricing configuration.
 */
object SubscriptionPricing {
    // Monthly prices in TZS
    val BASIC_MONTHLY = BigDecimal("12000.00")
    val PREMIUM_MONTHLY = BigDecimal("25000.00")
    val ENTERPRISE_MONTHLY = BigDecimal("50000.00")

    // Annual prices (10 months price for 12 months)
    val BASIC_ANNUAL = BASIC_MONTHLY.multiply(BigDecimal("10"))
    val PREMIUM_ANNUAL = PREMIUM_MONTHLY.multiply(BigDecimal("10"))
    val ENTERPRISE_ANNUAL = ENTERPRISE_MONTHLY.multiply(BigDecimal("10"))

    // Features per plan
    val BASIC_FEATURES = listOf(
        "Up to 500 products",
        "Up to 3 users",
        "Basic reports",
        "Email support",
        "Offline mode"
    )

    val PREMIUM_FEATURES = listOf(
        "Unlimited products",
        "Up to 10 users",
        "Advanced reports",
        "Priority support",
        "Offline mode",
        "Multi-shop management",
        "Customer credit management"
    )

    val ENTERPRISE_FEATURES = listOf(
        "Unlimited products",
        "Unlimited users",
        "Advanced analytics",
        "24/7 phone support",
        "Offline mode",
        "Multi-shop management",
        "Customer credit management",
        "B2B marketplace access",
        "Custom integrations",
        "White-label option"
    )

    fun getPrice(plan: SubscriptionPlan, isAnnual: Boolean): BigDecimal {
        return when (plan) {
            SubscriptionPlan.FREE -> BigDecimal.ZERO
            SubscriptionPlan.BASIC -> if (isAnnual) BASIC_ANNUAL else BASIC_MONTHLY
            SubscriptionPlan.PREMIUM -> if (isAnnual) PREMIUM_ANNUAL else PREMIUM_MONTHLY
            SubscriptionPlan.ENTERPRISE -> if (isAnnual) ENTERPRISE_ANNUAL else ENTERPRISE_MONTHLY
        }
    }

    fun getFeatures(plan: SubscriptionPlan): List<String> {
        return when (plan) {
            SubscriptionPlan.FREE -> listOf("Up to 100 products", "1 user", "Basic features")
            SubscriptionPlan.BASIC -> BASIC_FEATURES
            SubscriptionPlan.PREMIUM -> PREMIUM_FEATURES
            SubscriptionPlan.ENTERPRISE -> ENTERPRISE_FEATURES
        }
    }

    fun getMaxUsers(plan: SubscriptionPlan): Int? {
        return when (plan) {
            SubscriptionPlan.FREE -> 1
            SubscriptionPlan.BASIC -> 3
            SubscriptionPlan.PREMIUM -> 10
            SubscriptionPlan.ENTERPRISE -> null // Unlimited
        }
    }

    fun getMaxProducts(plan: SubscriptionPlan): Int? {
        return when (plan) {
            SubscriptionPlan.FREE -> 100
            SubscriptionPlan.BASIC -> 500
            SubscriptionPlan.PREMIUM -> null // Unlimited
            SubscriptionPlan.ENTERPRISE -> null // Unlimited
        }
    }
}

// ==================== Subscription Use Cases ====================

/**
 * Use case for getting active subscription.
 */
class GetActiveSubscriptionUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    operator fun invoke(shopId: String): Flow<Subscription?> {
        return repository.getActiveSubscription(shopId)
    }
}

/**
 * Use case for getting subscription history.
 */
class GetSubscriptionHistoryUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    operator fun invoke(shopId: String): Flow<List<Subscription>> {
        return repository.getSubscriptionHistory(shopId)
    }
}

/**
 * Use case for getting subscription by ID.
 */
class GetSubscriptionByIdUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(subscriptionId: String): Either<Throwable, Subscription?> {
        return repository.getSubscriptionById(subscriptionId)
    }
}

/**
 * Use case for creating a new subscription.
 */
class CreateSubscriptionUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(
        shopId: String,
        plan: SubscriptionPlan,
        type: SubscriptionType = SubscriptionType.OFFLINE,
        isAnnual: Boolean = false,
        autoRenew: Boolean = true
    ): Either<Throwable, Subscription> {
        val price = SubscriptionPricing.getPrice(plan, isAnnual)
        val features = SubscriptionPricing.getFeatures(plan)
        val maxUsers = SubscriptionPricing.getMaxUsers(plan)
        val maxProducts = SubscriptionPricing.getMaxProducts(plan)

        val subscription = Subscription(
            id = UUID.randomUUID().toString(),
            shopId = shopId,
            plan = plan,
            type = type,
            status = SubscriptionStatus.PENDING,
            price = price,
            currency = "TZS",
            autoRenew = autoRenew,
            features = features,
            maxUsers = maxUsers,
            maxProducts = maxProducts,
            createdAt = LocalDateTime.now()
        )

        return repository.createSubscription(subscription)
    }
}

/**
 * Use case for initiating subscription payment.
 * Returns payment details for mobile money or card payment.
 */
class InitiateSubscriptionPaymentUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(
        subscriptionId: String,
        paymentMethod: String // "mpesa", "tigopesa", "airtel", "card"
    ): Either<Throwable, PaymentInitiation> {
        return repository.getSubscriptionById(subscriptionId).map { subscription ->
            subscription?.let {
                PaymentInitiation(
                    subscriptionId = it.id,
                    amount = it.price,
                    currency = it.currency,
                    paymentMethod = paymentMethod,
                    referenceNumber = "SUB-${System.currentTimeMillis()}",
                    description = "MaiDuka ${it.plan.name} Subscription",
                    expiresAt = LocalDateTime.now().plusMinutes(30)
                )
            } ?: throw Exception("Subscription not found")
        }
    }
}

data class PaymentInitiation(
    val subscriptionId: String,
    val amount: BigDecimal,
    val currency: String,
    val paymentMethod: String,
    val referenceNumber: String,
    val description: String,
    val expiresAt: LocalDateTime
)

/**
 * Use case for activating subscription after payment.
 */
class ActivateSubscriptionUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(
        subscriptionId: String,
        transactionReference: String
    ): Either<Throwable, Subscription> {
        return repository.activateSubscription(subscriptionId, transactionReference)
    }
}

/**
 * Use case for cancelling subscription.
 */
class CancelSubscriptionUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(subscriptionId: String, reason: String): Either<Throwable, Unit> {
        return repository.cancelSubscription(subscriptionId, reason)
    }
}

/**
 * Use case for renewing subscription.
 */
class RenewSubscriptionUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(subscriptionId: String): Either<Throwable, Subscription> {
        return repository.renewSubscription(subscriptionId)
    }
}

/**
 * Use case for checking if subscription is valid.
 */
class IsSubscriptionValidUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(shopId: String): Boolean {
        return repository.isSubscriptionValid(shopId)
    }
}

/**
 * Use case for getting days remaining in subscription.
 */
class GetSubscriptionDaysRemainingUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(shopId: String): Int {
        return repository.getDaysRemaining(shopId)
    }
}

/**
 * Use case for upgrading subscription plan.
 */
class UpgradeSubscriptionUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(
        shopId: String,
        currentSubscriptionId: String,
        newPlan: SubscriptionPlan
    ): Either<Throwable, Subscription> {
        // Cancel current subscription
        repository.cancelSubscription(currentSubscriptionId, "Upgraded to ${newPlan.name}")

        // Create new subscription with new plan
        val price = SubscriptionPricing.getPrice(newPlan, false)
        val features = SubscriptionPricing.getFeatures(newPlan)
        val maxUsers = SubscriptionPricing.getMaxUsers(newPlan)
        val maxProducts = SubscriptionPricing.getMaxProducts(newPlan)

        val subscription = Subscription(
            id = UUID.randomUUID().toString(),
            shopId = shopId,
            plan = newPlan,
            type = SubscriptionType.OFFLINE,
            status = SubscriptionStatus.PENDING,
            price = price,
            currency = "TZS",
            autoRenew = true,
            features = features,
            maxUsers = maxUsers,
            maxProducts = maxProducts,
            createdAt = LocalDateTime.now()
        )

        return repository.createSubscription(subscription)
    }
}

