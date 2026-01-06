package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ShopSettingsEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ShopSettings entity operations.
 */
@Dao
interface ShopSettingsDao : BaseDao<ShopSettingsEntity> {

    /**
     * Gets settings for a shop.
     */
    @Query("SELECT * FROM shop_settings WHERE shop_id = :shopId AND deleted_at IS NULL")
    suspend fun getSettingsByShop(shopId: String): ShopSettingsEntity?

    /**
     * Gets settings by ID.
     */
    @Query("SELECT * FROM shop_settings WHERE id = :settingsId AND deleted_at IS NULL")
    suspend fun getSettingsById(settingsId: String): ShopSettingsEntity?

    /**
     * Gets settings for a shop as Flow.
     */
    @Query("SELECT * FROM shop_settings WHERE shop_id = :shopId AND deleted_at IS NULL")
    fun getSettingsByShopFlow(shopId: String): Flow<ShopSettingsEntity?>

    /**
     * Updates notification settings.
     */
    @Query("""
        UPDATE shop_settings SET 
        enable_sms_notifications = :enableSms, 
        enable_email_notifications = :enableEmail, 
        notify_low_stock = :notifyLowStock,
        updated_at = :updatedAt 
        WHERE shop_id = :shopId
    """)
    suspend fun updateNotificationSettings(
        shopId: String,
        enableSms: Boolean,
        enableEmail: Boolean,
        notifyLowStock: Boolean,
        updatedAt: Long
    )

    /**
     * Updates sales settings.
     */
    @Query("""
        UPDATE shop_settings SET 
        allow_credit_sales = :allowCreditSales, 
        allow_discounts = :allowDiscounts, 
        max_discount_percentage = :maxDiscountPercentage,
        updated_at = :updatedAt 
        WHERE shop_id = :shopId
    """)
    suspend fun updateSalesSettings(
        shopId: String,
        allowCreditSales: Boolean,
        allowDiscounts: Boolean,
        maxDiscountPercentage: String,
        updatedAt: Long
    )

    /**
     * Updates tax percentage.
     */
    @Query("UPDATE shop_settings SET tax_percentage = :taxPercentage, updated_at = :updatedAt WHERE shop_id = :shopId")
    suspend fun updateTaxPercentage(shopId: String, taxPercentage: String, updatedAt: Long)

    /**
     * Updates low stock threshold.
     */
    @Query("UPDATE shop_settings SET low_stock_threshold = :threshold, updated_at = :updatedAt WHERE shop_id = :shopId")
    suspend fun updateLowStockThreshold(shopId: String, threshold: Int, updatedAt: Long)

    /**
     * Updates receipt settings.
     */
    @Query("""
        UPDATE shop_settings SET 
        receipt_header = :header, 
        receipt_footer = :footer, 
        show_shop_logo_on_receipt = :showLogo,
        updated_at = :updatedAt 
        WHERE shop_id = :shopId
    """)
    suspend fun updateReceiptSettings(
        shopId: String,
        header: String?,
        footer: String?,
        showLogo: Boolean,
        updatedAt: Long
    )

    /**
     * Gets settings pending sync.
     */
    @Query("SELECT * FROM shop_settings WHERE sync_status = 'pending'")
    suspend fun getSettingsPendingSync(): List<ShopSettingsEntity>

    /**
     * Updates sync status for settings.
     */
    @Query("UPDATE shop_settings SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :settingsId")
    suspend fun updateSyncStatus(settingsId: String, status: String, syncedAt: Long)

    /**
     * Deletes all settings.
     */
    @Query("DELETE FROM shop_settings")
    suspend fun deleteAllSettings()
}

