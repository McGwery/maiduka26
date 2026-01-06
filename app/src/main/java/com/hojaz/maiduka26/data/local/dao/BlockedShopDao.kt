package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.BlockedShopEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for BlockedShop entity operations.
 */
@Dao
interface BlockedShopDao : BaseDao<BlockedShopEntity> {

    /**
     * Gets all blocked shops for a shop.
     */
    @Query("SELECT * FROM blocked_shops WHERE shop_id = :shopId")
    fun getBlockedShops(shopId: String): Flow<List<BlockedShopEntity>>

    /**
     * Checks if a shop is blocked.
     */
    @Query("SELECT * FROM blocked_shops WHERE shop_id = :shopId AND blocked_shop_id = :blockedShopId")
    suspend fun isShopBlocked(shopId: String, blockedShopId: String): BlockedShopEntity?

    /**
     * Unblocks a shop.
     */
    @Query("DELETE FROM blocked_shops WHERE shop_id = :shopId AND blocked_shop_id = :blockedShopId")
    suspend fun unblockShop(shopId: String, blockedShopId: String)

    /**
     * Gets blocked shops pending sync.
     */
    @Query("SELECT * FROM blocked_shops WHERE sync_status = 'pending'")
    suspend fun getBlockedShopsPendingSync(): List<BlockedShopEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE blocked_shops SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all blocked shops.
     */
    @Query("DELETE FROM blocked_shops")
    suspend fun deleteAllBlockedShops()
}

