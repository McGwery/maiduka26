package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.SubscriptionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Subscription entity operations.
 */
@Dao
interface SubscriptionDao : BaseDao<SubscriptionEntity> {

    /**
     * Gets subscriptions for a shop.
     */
    @Query("SELECT * FROM subscriptions WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getSubscriptionsForShop(shopId: String): Flow<List<SubscriptionEntity>>

    /**
     * Gets subscriptions for a shop (alias for getSubscriptionsForShop).
     */
    @Query("SELECT * FROM subscriptions WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getSubscriptionsByShop(shopId: String): Flow<List<SubscriptionEntity>>

    /**
     * Gets active subscription for a shop.
     */
    @Query("SELECT * FROM subscriptions WHERE shop_id = :shopId AND status = 'active' AND deleted_at IS NULL ORDER BY created_at DESC LIMIT 1")
    suspend fun getActiveSubscription(shopId: String): SubscriptionEntity?

    /**
     * Gets active subscription for a shop as Flow.
     */
    @Query("SELECT * FROM subscriptions WHERE shop_id = :shopId AND status = 'active' AND deleted_at IS NULL ORDER BY created_at DESC LIMIT 1")
    fun getActiveSubscriptionFlow(shopId: String): Flow<SubscriptionEntity?>

    /**
     * Gets active subscription for a shop synchronously.
     */
    @Query("SELECT * FROM subscriptions WHERE shop_id = :shopId AND status = 'active' AND deleted_at IS NULL ORDER BY created_at DESC LIMIT 1")
    suspend fun getActiveSubscriptionSync(shopId: String): SubscriptionEntity?

    /**
     * Gets a subscription by ID.
     */
    @Query("SELECT * FROM subscriptions WHERE id = :subscriptionId")
    suspend fun getSubscriptionById(subscriptionId: String): SubscriptionEntity?

    /**
     * Gets expiring subscriptions.
     */
    @Query("SELECT * FROM subscriptions WHERE status = 'active' AND expires_at <= :expiresBy AND deleted_at IS NULL")
    suspend fun getExpiringSubscriptions(expiresBy: Long): List<SubscriptionEntity>

    /**
     * Updates subscription status.
     */
    @Query("UPDATE subscriptions SET status = :status, updated_at = :updatedAt WHERE id = :subscriptionId")
    suspend fun updateStatus(subscriptionId: String, status: String, updatedAt: Long)

    /**
     * Cancels a subscription.
     */
    @Query("UPDATE subscriptions SET status = 'cancelled', cancelled_at = :cancelledAt, cancelled_reason = :reason, updated_at = :cancelledAt WHERE id = :subscriptionId")
    suspend fun cancelSubscription(subscriptionId: String, reason: String?, cancelledAt: Long)

    /**
     * Activates a subscription.
     */
    @Query("UPDATE subscriptions SET status = :status, starts_at = :startsAt, expires_at = :expiresAt, transaction_reference = :transactionReference, updated_at = :updatedAt WHERE id = :subscriptionId")
    suspend fun activateSubscription(
        subscriptionId: String,
        status: String,
        startsAt: Long,
        expiresAt: Long,
        transactionReference: String,
        updatedAt: Long
    )

    /**
     * Gets subscriptions pending sync.
     */
    @Query("SELECT * FROM subscriptions WHERE sync_status = 'pending'")
    suspend fun getSubscriptionsPendingSync(): List<SubscriptionEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE subscriptions SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all subscriptions.
     */
    @Query("DELETE FROM subscriptions")
    suspend fun deleteAllSubscriptions()
}

