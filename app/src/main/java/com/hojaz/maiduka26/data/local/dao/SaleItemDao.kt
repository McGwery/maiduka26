package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.SaleItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SaleItem entity operations.
 */
@Dao
interface SaleItemDao : BaseDao<SaleItemEntity> {

    /**
     * Gets all items for a sale.
     */
    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId AND deleted_at IS NULL")
    fun getSaleItemsBySale(saleId: String): Flow<List<SaleItemEntity>>

    /**
     * Gets all items for a sale (suspend version).
     */
    @Query("SELECT * FROM sale_items WHERE sale_id = :saleId AND deleted_at IS NULL")
    suspend fun getSaleItemsBySaleSync(saleId: String): List<SaleItemEntity>

    /**
     * Gets a sale item by ID.
     */
    @Query("SELECT * FROM sale_items WHERE id = :itemId")
    suspend fun getSaleItemById(itemId: String): SaleItemEntity?

    /**
     * Gets sale items by product.
     */
    @Query("SELECT * FROM sale_items WHERE product_id = :productId AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getSaleItemsByProduct(productId: String): Flow<List<SaleItemEntity>>

    /**
     * Gets total quantity sold for a product.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(quantity AS REAL)), 0) 
        FROM sale_items 
        WHERE product_id = :productId 
        AND deleted_at IS NULL
    """)
    suspend fun getTotalQuantitySold(productId: String): Double

    /**
     * Gets sale items pending sync.
     */
    @Query("SELECT * FROM sale_items WHERE sync_status = 'pending'")
    suspend fun getSaleItemsPendingSync(): List<SaleItemEntity>

    /**
     * Updates sync status for a sale item.
     */
    @Query("UPDATE sale_items SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :itemId")
    suspend fun updateSyncStatus(itemId: String, status: String, syncedAt: Long)

    /**
     * Deletes all items for a sale.
     */
    @Query("DELETE FROM sale_items WHERE sale_id = :saleId")
    suspend fun deleteSaleItems(saleId: String)

    /**
     * Deletes all sale items.
     */
    @Query("DELETE FROM sale_items")
    suspend fun deleteAllSaleItems()
}

