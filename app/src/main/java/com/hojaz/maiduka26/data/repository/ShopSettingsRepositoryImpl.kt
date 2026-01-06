package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.ShopMemberDao
import com.hojaz.maiduka26.data.local.dao.ShopSettingsDao
import com.hojaz.maiduka26.data.local.dao.SubscriptionDao
import com.hojaz.maiduka26.data.local.entity.SubscriptionEntity
import com.hojaz.maiduka26.data.mapper.ShopConfigurationMapper.toDomain
import com.hojaz.maiduka26.data.mapper.ShopConfigurationMapper.toEntity
import com.hojaz.maiduka26.data.mapper.ShopConfigurationMapper.toMemberDomainList
import com.hojaz.maiduka26.data.mapper.ShopConfigurationMapper.toSubscriptionDomainList
import com.hojaz.maiduka26.domain.model.ShopMember
import com.hojaz.maiduka26.domain.model.ShopSettings
import com.hojaz.maiduka26.domain.model.Subscription
import com.hojaz.maiduka26.domain.repository.ShopSettingsRepository
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ShopSettingsRepository.
 */
@Singleton
class ShopSettingsRepositoryImpl @Inject constructor(
    private val shopSettingsDao: ShopSettingsDao,
    private val shopMemberDao: ShopMemberDao,
    private val subscriptionDao: SubscriptionDao,
    private val networkMonitor: NetworkMonitor
) : ShopSettingsRepository {

    // ==================== Shop Settings ====================

    override fun getShopSettings(shopId: String): Flow<ShopSettings?> {
        return shopSettingsDao.getSettingsByShopFlow(shopId).map { it?.toDomain() }
    }

    override suspend fun getShopSettingsById(settingsId: String): Either<Throwable, ShopSettings?> {
        return try {
            val entity = shopSettingsDao.getSettingsById(settingsId)
            val settings = entity?.toDomain()
            settings.right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting shop settings: $settingsId")
            e.left()
        }
    }

    override suspend fun saveShopSettings(settings: ShopSettings): Either<Throwable, ShopSettings> {
        return try {
            val entity = settings.toEntity(syncStatus = "pending")
            // Check if settings exist, then update, otherwise insert
            val existing = shopSettingsDao.getSettingsByShop(settings.shopId)
            if (existing != null) {
                shopSettingsDao.update(entity)
            } else {
                shopSettingsDao.insert(entity)
            }
            Timber.d("Shop settings saved: ${settings.shopId}")
            settings.right()
        } catch (e: Exception) {
            Timber.e(e, "Error saving shop settings")
            e.left()
        }
    }

    override suspend fun updateNotificationSettings(
        shopId: String,
        enableSms: Boolean,
        enableEmail: Boolean,
        notifyLowStock: Boolean
    ): Either<Throwable, Unit> {
        return try {
            shopSettingsDao.updateNotificationSettings(
                shopId, enableSms, enableEmail, notifyLowStock, System.currentTimeMillis()
            )
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating notification settings")
            e.left()
        }
    }

    override suspend fun updateSalesSettings(
        shopId: String,
        allowCreditSales: Boolean,
        allowDiscounts: Boolean,
        maxDiscountPercentage: Double
    ): Either<Throwable, Unit> {
        return try {
            shopSettingsDao.updateSalesSettings(
                shopId,
                allowCreditSales,
                allowDiscounts,
                BigDecimal(maxDiscountPercentage).toPlainString(),
                System.currentTimeMillis()
            )
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating sales settings")
            e.left()
        }
    }

    // ==================== Shop Members ====================

    override fun getShopMembers(shopId: String): Flow<List<ShopMember>> {
        return shopMemberDao.getMembersByShop(shopId).map { it.toMemberDomainList() }
    }

    override fun getActiveShopMembers(shopId: String): Flow<List<ShopMember>> {
        return shopMemberDao.getActiveMembersByShop(shopId).map { it.toMemberDomainList() }
    }

    override suspend fun getMemberById(memberId: String): Either<Throwable, ShopMember?> {
        return try {
            shopMemberDao.getMemberById(memberId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting member: $memberId")
            e.left()
        }
    }

    override suspend fun getMemberByUserId(shopId: String, userId: String): Either<Throwable, ShopMember?> {
        return try {
            shopMemberDao.getMemberByUserId(shopId, userId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting member by user ID")
            e.left()
        }
    }

    override suspend fun addMember(member: ShopMember): Either<Throwable, ShopMember> {
        return try {
            val entity = member.toEntity(syncStatus = "pending")
            shopMemberDao.insert(entity)
            Timber.d("Member added: ${member.id}")
            member.right()
        } catch (e: Exception) {
            Timber.e(e, "Error adding member")
            e.left()
        }
    }

    override suspend fun updateMemberRole(memberId: String, role: String): Either<Throwable, Unit> {
        return try {
            shopMemberDao.updateRole(memberId, role, System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating member role")
            e.left()
        }
    }

    override suspend fun updateMemberPermissions(memberId: String, permissions: List<String>): Either<Throwable, Unit> {
        return try {
            val permissionsJson = com.google.gson.Gson().toJson(permissions)
            shopMemberDao.updatePermissions(memberId, permissionsJson, System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating member permissions")
            e.left()
        }
    }

    override suspend fun deactivateMember(memberId: String): Either<Throwable, Unit> {
        return try {
            shopMemberDao.updateActiveStatus(memberId, false, System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deactivating member")
            e.left()
        }
    }

    override suspend fun reactivateMember(memberId: String): Either<Throwable, Unit> {
        return try {
            shopMemberDao.updateActiveStatus(memberId, true, System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error reactivating member")
            e.left()
        }
    }

    override suspend fun removeMember(memberId: String): Either<Throwable, Unit> {
        return try {
            val member = shopMemberDao.getMemberById(memberId)
            member?.let { shopMemberDao.delete(it) }
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error removing member")
            e.left()
        }
    }

    override suspend fun getMemberCount(shopId: String): Int {
        return shopMemberDao.getMemberCount(shopId)
    }

    // ==================== Subscriptions ====================

    override fun getActiveSubscription(shopId: String): Flow<Subscription?> {
        return subscriptionDao.getActiveSubscriptionFlow(shopId).map { it?.toDomain() }
    }

    override fun getSubscriptionHistory(shopId: String): Flow<List<Subscription>> {
        return subscriptionDao.getSubscriptionsByShop(shopId).map { it.toSubscriptionDomainList() }
    }

    override suspend fun getSubscriptionById(subscriptionId: String): Either<Throwable, Subscription?> {
        return try {
            subscriptionDao.getSubscriptionById(subscriptionId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting subscription: $subscriptionId")
            e.left()
        }
    }

    override suspend fun createSubscription(subscription: Subscription): Either<Throwable, Subscription> {
        return try {
            val entity = subscription.toEntity(syncStatus = "pending")
            subscriptionDao.insert(entity)
            Timber.d("Subscription created: ${subscription.id}")
            subscription.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating subscription")
            e.left()
        }
    }

    override suspend fun activateSubscription(
        subscriptionId: String,
        transactionReference: String
    ): Either<Throwable, Subscription> {
        return try {
            val now = System.currentTimeMillis()
            val expiresAt = now + (30L * 24 * 60 * 60 * 1000) // 30 days

            subscriptionDao.activateSubscription(
                subscriptionId,
                "active",
                now,
                expiresAt,
                transactionReference,
                now
            )

            val subscription = subscriptionDao.getSubscriptionById(subscriptionId)
                ?: return Exception("Subscription not found").left()

            Timber.d("Subscription activated: $subscriptionId")
            subscription.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error activating subscription")
            e.left()
        }
    }

    override suspend fun cancelSubscription(subscriptionId: String, reason: String): Either<Throwable, Unit> {
        return try {
            subscriptionDao.cancelSubscription(
                subscriptionId,
                reason,
                System.currentTimeMillis()
            )
            Timber.d("Subscription cancelled: $subscriptionId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error cancelling subscription")
            e.left()
        }
    }

    override suspend fun renewSubscription(subscriptionId: String): Either<Throwable, Subscription> {
        return try {
            val existing = subscriptionDao.getSubscriptionById(subscriptionId)
                ?: return Exception("Subscription not found").left()

            // Create new subscription based on existing
            val now = System.currentTimeMillis()
            val newSubscription = SubscriptionEntity(
                id = UUID.randomUUID().toString(),
                shopId = existing.shopId,
                plan = existing.plan,
                type = existing.type,
                status = "pending",
                price = existing.price,
                currency = existing.currency,
                autoRenew = existing.autoRenew,
                createdAt = now,
                updatedAt = now,
                syncStatus = "pending"
            )

            subscriptionDao.insert(newSubscription)
            Timber.d("Subscription renewal created: ${newSubscription.id}")
            newSubscription.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error renewing subscription")
            e.left()
        }
    }

    override suspend fun isSubscriptionValid(shopId: String): Boolean {
        val subscription = subscriptionDao.getActiveSubscriptionSync(shopId) ?: return false
        return subscription.status == "active" &&
               (subscription.expiresAt == null || subscription.expiresAt > System.currentTimeMillis())
    }

    override suspend fun getDaysRemaining(shopId: String): Int {
        val subscription = subscriptionDao.getActiveSubscriptionSync(shopId) ?: return 0
        val expiresAt = subscription.expiresAt ?: return 0
        val now = System.currentTimeMillis()

        if (expiresAt <= now) return 0

        return ((expiresAt - now) / (24 * 60 * 60 * 1000)).toInt()
    }

    override suspend fun syncShopConfiguration(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }
            // TODO: Implement sync logic
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing shop configuration")
            e.left()
        }
    }
}

