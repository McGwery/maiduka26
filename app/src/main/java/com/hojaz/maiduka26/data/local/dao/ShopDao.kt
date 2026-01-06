package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ShopEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Shop entity operations.
 */
@Dao
interface ShopDao : BaseDao<ShopEntity> {

    /**
     * Gets all active shops.
     */
    @Query("SELECT * FROM shops WHERE is_active = 1 AND deleted_at IS NULL ORDER BY name ASC")
    fun getAllActiveShops(): Flow<List<ShopEntity>>

    /**
     * Gets all shops for a specific owner.
     */
    @Query("SELECT * FROM shops WHERE owner_id = :ownerId AND deleted_at IS NULL ORDER BY name ASC")
    fun getShopsByOwner(ownerId: String): Flow<List<ShopEntity>>

    /**
     * Gets a shop by ID.
     */
    @Query("SELECT * FROM shops WHERE id = :shopId")
    suspend fun getShopById(shopId: String): ShopEntity?

    /**
     * Gets a shop by ID as Flow.
     */
    @Query("SELECT * FROM shops WHERE id = :shopId")
    fun getShopByIdFlow(shopId: String): Flow<ShopEntity?>

    /**
     * Gets a shop by agent code.
     */
    @Query("SELECT * FROM shops WHERE agent_code = :agentCode")
    suspend fun getShopByAgentCode(agentCode: String): ShopEntity?

    /**
     * Searches shops by name.
     */
    @Query("SELECT * FROM shops WHERE name LIKE '%' || :query || '%' AND deleted_at IS NULL ORDER BY name ASC")
    fun searchShops(query: String): Flow<List<ShopEntity>>

    /**
     * Gets shops pending sync.
     */
    @Query("SELECT * FROM shops WHERE sync_status = 'pending'")
    suspend fun getShopsPendingSync(): List<ShopEntity>

    /**
     * Updates sync status for a shop.
     */
    @Query("UPDATE shops SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :shopId")
    suspend fun updateSyncStatus(shopId: String, status: String, syncedAt: Long)

    /**
     * Soft deletes a shop.
     */
    @Query("UPDATE shops SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :shopId")
    suspend fun softDelete(shopId: String, deletedAt: Long)

    /**
     * Gets shop count for an owner.
     */
    @Query("SELECT COUNT(*) FROM shops WHERE owner_id = :ownerId AND deleted_at IS NULL")
    suspend fun getShopCount(ownerId: String): Int

    /**
     * Deletes all shops.
     */
    @Query("DELETE FROM shops")
    suspend fun deleteAllShops()
}

