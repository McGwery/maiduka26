package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.StockTransferEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for StockTransfer entity operations.
 */
@Dao
interface StockTransferDao : BaseDao<StockTransferEntity> {

    /**
     * Gets transfers for a purchase order.
     */
    @Query("SELECT * FROM stock_transfers WHERE purchase_order_id = :orderId AND deleted_at IS NULL ORDER BY transferred_at DESC")
    fun getTransfersForOrder(orderId: String): Flow<List<StockTransferEntity>>

    /**
     * Gets transfers for a product.
     */
    @Query("SELECT * FROM stock_transfers WHERE product_id = :productId AND deleted_at IS NULL ORDER BY transferred_at DESC")
    fun getTransfersForProduct(productId: String): Flow<List<StockTransferEntity>>

    /**
     * Gets a transfer by ID.
     */
    @Query("SELECT * FROM stock_transfers WHERE id = :transferId")
    suspend fun getTransferById(transferId: String): StockTransferEntity?

    /**
     * Gets total quantity transferred for an order.
     */
    @Query("SELECT COALESCE(SUM(quantity), 0) FROM stock_transfers WHERE purchase_order_id = :orderId AND deleted_at IS NULL")
    suspend fun getTotalTransferredForOrder(orderId: String): Int

    /**
     * Gets transfers pending sync.
     */
    @Query("SELECT * FROM stock_transfers WHERE sync_status = 'pending'")
    suspend fun getTransfersPendingSync(): List<StockTransferEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE stock_transfers SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all transfers.
     */
    @Query("DELETE FROM stock_transfers")
    suspend fun deleteAllTransfers()
}

