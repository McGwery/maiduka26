package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Shop
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for shop operations.
 */
interface ShopRepository {

    /**
     * Get all shops for the current user.
     */
    fun getShops(): Flow<List<Shop>>

    /**
     * Get a shop by ID.
     */
    suspend fun getShopById(shopId: String): Either<Throwable, Shop?>

    /**
     * Get a shop by ID as Flow.
     */
    fun getShopByIdFlow(shopId: String): Flow<Shop?>

    /**
     * Create a new shop.
     */
    suspend fun createShop(shop: Shop): Either<Throwable, Shop>

    /**
     * Update an existing shop.
     */
    suspend fun updateShop(shop: Shop): Either<Throwable, Shop>

    /**
     * Delete a shop.
     */
    suspend fun deleteShop(shopId: String): Either<Throwable, Unit>

    /**
     * Search shops by name.
     */
    fun searchShops(query: String): Flow<List<Shop>>

    /**
     * Get the currently active shop.
     */
    fun getActiveShop(): Flow<Shop?>

    /**
     * Set the active shop.
     */
    suspend fun setActiveShop(shopId: String): Either<Throwable, Unit>

    /**
     * Get shop count for the current user.
     */
    suspend fun getShopCount(): Int

    /**
     * Sync shops with remote server.
     */
    suspend fun syncShops(): Either<Throwable, Unit>
}

