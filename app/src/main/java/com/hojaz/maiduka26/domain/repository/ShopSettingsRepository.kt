package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.ShopMember
import com.hojaz.maiduka26.domain.model.ShopSettings
import com.hojaz.maiduka26.domain.model.Subscription
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for shop settings and configuration operations.
 */
interface ShopSettingsRepository {

    // ==================== Shop Settings ====================

    /**
     * Get settings for a shop.
     */
    fun getShopSettings(shopId: String): Flow<ShopSettings?>

    /**
     * Get settings by ID.
     */
    suspend fun getShopSettingsById(settingsId: String): Either<Throwable, ShopSettings?>

    /**
     * Create or update shop settings.
     */
    suspend fun saveShopSettings(settings: ShopSettings): Either<Throwable, ShopSettings>

    /**
     * Update specific settings field.
     */
    suspend fun updateNotificationSettings(
        shopId: String,
        enableSms: Boolean,
        enableEmail: Boolean,
        notifyLowStock: Boolean
    ): Either<Throwable, Unit>

    /**
     * Update sales settings.
     */
    suspend fun updateSalesSettings(
        shopId: String,
        allowCreditSales: Boolean,
        allowDiscounts: Boolean,
        maxDiscountPercentage: Double
    ): Either<Throwable, Unit>

    // ==================== Shop Members ====================

    /**
     * Get all members of a shop.
     */
    fun getShopMembers(shopId: String): Flow<List<ShopMember>>

    /**
     * Get active members of a shop.
     */
    fun getActiveShopMembers(shopId: String): Flow<List<ShopMember>>

    /**
     * Get a member by ID.
     */
    suspend fun getMemberById(memberId: String): Either<Throwable, ShopMember?>

    /**
     * Get member by user ID and shop ID.
     */
    suspend fun getMemberByUserId(shopId: String, userId: String): Either<Throwable, ShopMember?>

    /**
     * Add a new member to the shop.
     */
    suspend fun addMember(member: ShopMember): Either<Throwable, ShopMember>

    /**
     * Update member role.
     */
    suspend fun updateMemberRole(memberId: String, role: String): Either<Throwable, Unit>

    /**
     * Update member permissions.
     */
    suspend fun updateMemberPermissions(memberId: String, permissions: List<String>): Either<Throwable, Unit>

    /**
     * Deactivate a member.
     */
    suspend fun deactivateMember(memberId: String): Either<Throwable, Unit>

    /**
     * Reactivate a member.
     */
    suspend fun reactivateMember(memberId: String): Either<Throwable, Unit>

    /**
     * Remove a member from shop.
     */
    suspend fun removeMember(memberId: String): Either<Throwable, Unit>

    /**
     * Get member count for a shop.
     */
    suspend fun getMemberCount(shopId: String): Int

    // ==================== Subscriptions ====================

    /**
     * Get active subscription for a shop.
     */
    fun getActiveSubscription(shopId: String): Flow<Subscription?>

    /**
     * Get subscription history for a shop.
     */
    fun getSubscriptionHistory(shopId: String): Flow<List<Subscription>>

    /**
     * Get subscription by ID.
     */
    suspend fun getSubscriptionById(subscriptionId: String): Either<Throwable, Subscription?>

    /**
     * Create a new subscription.
     */
    suspend fun createSubscription(subscription: Subscription): Either<Throwable, Subscription>

    /**
     * Activate subscription after payment.
     */
    suspend fun activateSubscription(subscriptionId: String, transactionReference: String): Either<Throwable, Subscription>

    /**
     * Cancel subscription.
     */
    suspend fun cancelSubscription(subscriptionId: String, reason: String): Either<Throwable, Unit>

    /**
     * Renew subscription.
     */
    suspend fun renewSubscription(subscriptionId: String): Either<Throwable, Subscription>

    /**
     * Check if subscription is valid.
     */
    suspend fun isSubscriptionValid(shopId: String): Boolean

    /**
     * Get days remaining in subscription.
     */
    suspend fun getDaysRemaining(shopId: String): Int

    /**
     * Sync shop configuration with remote server.
     */
    suspend fun syncShopConfiguration(shopId: String): Either<Throwable, Unit>
}

