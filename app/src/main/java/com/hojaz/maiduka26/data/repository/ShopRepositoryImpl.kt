package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.ActiveShopDao
import com.hojaz.maiduka26.data.local.dao.ShopDao
import com.hojaz.maiduka26.data.local.entity.ActiveShopEntity
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.mapper.ShopMapper.toDomain
import com.hojaz.maiduka26.data.mapper.ShopMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.ShopMapper.toEntity
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.domain.repository.ShopRepository
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ShopRepository.
 * Follows offline-first approach: local database is the source of truth.
 */
@Singleton
class ShopRepositoryImpl @Inject constructor(
    private val shopDao: ShopDao,
    private val activeShopDao: ActiveShopDao,
    private val networkMonitor: NetworkMonitor,
    private val preferencesManager: PreferencesManager
) : ShopRepository {

    override fun getShops(): Flow<List<Shop>> {
        return shopDao.getAllActiveShops().map { it.toDomainList() }
    }

    override suspend fun getShopById(shopId: String): Either<Throwable, Shop?> {
        return try {
            shopDao.getShopById(shopId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting shop by ID: $shopId")
            e.left()
        }
    }

    override fun getShopByIdFlow(shopId: String): Flow<Shop?> {
        return shopDao.getShopByIdFlow(shopId).map { it?.toDomain() }
    }

    override suspend fun createShop(shop: Shop): Either<Throwable, Shop> {
        return try {
            val entity = shop.toEntity(syncStatus = "pending")
            shopDao.insert(entity)
            Timber.d("Shop created locally: ${shop.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            shop.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating shop")
            e.left()
        }
    }

    override suspend fun updateShop(shop: Shop): Either<Throwable, Shop> {
        return try {
            val entity = shop.toEntity(syncStatus = "pending")
            shopDao.update(entity)
            Timber.d("Shop updated locally: ${shop.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            shop.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating shop")
            e.left()
        }
    }

    override suspend fun deleteShop(shopId: String): Either<Throwable, Unit> {
        return try {
            shopDao.softDelete(shopId, System.currentTimeMillis())
            Timber.d("Shop soft deleted: $shopId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting shop")
            e.left()
        }
    }

    override fun searchShops(query: String): Flow<List<Shop>> {
        return shopDao.searchShops(query).map { it.toDomainList() }
    }

    override fun getActiveShop(): Flow<Shop?> {
        return preferencesManager.userPreferencesFlow.flatMapLatest { prefs ->
            val userId = prefs.userId
            if (userId != null) {
                activeShopDao.getActiveShopForUserFlow(userId).flatMapLatest { activeShop ->
                    if (activeShop != null) {
                        shopDao.getShopByIdFlow(activeShop.shopId).map { it?.toDomain() }
                    } else {
                        flowOf(null)
                    }
                }
            } else {
                flowOf(null)
            }
        }
    }

    override suspend fun setActiveShop(shopId: String): Either<Throwable, Unit> {
        return try {
            val prefs = preferencesManager.userPreferencesFlow.first()
            val userId = prefs.userId ?: return Exception("User not logged in").left()

            // Get shop details
            val shop = shopDao.getShopById(shopId) ?: return Exception("Shop not found").left()

            // Clear existing active shop for user
            activeShopDao.clearActiveShopForUser(userId)

            // Set new active shop
            val activeShop = ActiveShopEntity(
                id = UUID.randomUUID().toString(),
                userId = userId,
                shopId = shopId,
                selectedAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )
            activeShopDao.insert(activeShop)

            // Update preferences
            preferencesManager.setActiveShop(shopId, shop.name)

            Timber.d("Active shop set: $shopId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error setting active shop")
            e.left()
        }
    }

    override suspend fun getShopCount(): Int {
        val prefs = preferencesManager.userPreferencesFlow.first()
        val userId = prefs.userId ?: return 0
        return shopDao.getShopCount(userId)
    }

    override suspend fun syncShops(): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pendingShops = shopDao.getShopsPendingSync()

            // TODO: Upload pending shops to server
            // TODO: Download updated shops from server

            pendingShops.forEach { shop ->
                shopDao.updateSyncStatus(shop.id, "synced", System.currentTimeMillis())
            }

            Timber.d("Shops synced: ${pendingShops.size} uploaded")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing shops")
            e.left()
        }
    }
}

