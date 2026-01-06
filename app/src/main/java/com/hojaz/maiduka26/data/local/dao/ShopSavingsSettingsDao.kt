package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ShopSavingsSettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ShopSavingsSettings entity operations.
 */
@Dao
interface ShopSavingsSettingsDao : BaseDao<ShopSavingsSettingsEntity> {

    /**
     * Gets savings settings for a shop.
     */
    @Query("SELECT * FROM shop_savings_settings WHERE shop_id = :shopId AND deleted_at IS NULL")
    suspend fun getSettingsByShop(shopId: String): ShopSavingsSettingsEntity?

    /**
     * Gets savings settings for a shop as Flow.
     */
    @Query("SELECT * FROM shop_savings_settings WHERE shop_id = :shopId AND deleted_at IS NULL")
    fun getSettingsByShopFlow(shopId: String): Flow<ShopSavingsSettingsEntity?>

    /**
     * Updates savings enabled status.
     */
    @Query("UPDATE shop_savings_settings SET is_enabled = :isEnabled, updated_at = :updatedAt WHERE shop_id = :shopId")
    suspend fun updateEnabledStatus(shopId: String, isEnabled: Boolean, updatedAt: Long)

    /**
     * Updates current balance.
     */
    @Query("""
        UPDATE shop_savings_settings SET 
        current_balance = :balance,
        total_saved = :totalSaved,
        last_savings_date = :lastSavingsDate,
        updated_at = :updatedAt
        WHERE shop_id = :shopId
    """)
    suspend fun updateBalance(shopId: String, balance: String, totalSaved: String, lastSavingsDate: Long, updatedAt: Long)

    /**
     * Gets settings pending sync.
     */
    @Query("SELECT * FROM shop_savings_settings WHERE sync_status = 'pending'")
    suspend fun getSettingsPendingSync(): List<ShopSavingsSettingsEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE shop_savings_settings SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all settings.
     */
    @Query("DELETE FROM shop_savings_settings")
    suspend fun deleteAllSettings()
}

