package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ActiveShopEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ActiveShop entity operations.
 */
@Dao
interface ActiveShopDao : BaseDao<ActiveShopEntity> {

    /**
     * Gets the active shop for a user.
     */
    @Query("SELECT * FROM active_shops WHERE user_id = :userId")
    suspend fun getActiveShopForUser(userId: String): ActiveShopEntity?

    /**
     * Gets the active shop for a user as Flow.
     */
    @Query("SELECT * FROM active_shops WHERE user_id = :userId")
    fun getActiveShopForUserFlow(userId: String): Flow<ActiveShopEntity?>

    /**
     * Sets the active shop for a user (replaces existing).
     */
    @Query("DELETE FROM active_shops WHERE user_id = :userId")
    suspend fun clearActiveShopForUser(userId: String)

    /**
     * Gets all active shop entries.
     */
    @Query("SELECT * FROM active_shops")
    fun getAllActiveShops(): Flow<List<ActiveShopEntity>>

    /**
     * Gets active shops pending sync.
     */
    @Query("SELECT * FROM active_shops WHERE sync_status = 'pending'")
    suspend fun getActiveShopsPendingSync(): List<ActiveShopEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE active_shops SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all active shops.
     */
    @Query("DELETE FROM active_shops")
    suspend fun deleteAllActiveShops()
}

