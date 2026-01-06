package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.PurchaseOrderItemEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for PurchaseOrderItem entity operations.
 */
@Dao
interface PurchaseOrderItemDao : BaseDao<PurchaseOrderItemEntity> {

    /**
     * Gets items for a purchase order.
     */
    @Query("SELECT * FROM purchase_order_items WHERE purchase_order_id = :orderId AND deleted_at IS NULL")
    fun getItemsForOrder(orderId: String): Flow<List<PurchaseOrderItemEntity>>

    /**
     * Gets items for a purchase order (sync).
     */
    @Query("SELECT * FROM purchase_order_items WHERE purchase_order_id = :orderId AND deleted_at IS NULL")
    suspend fun getItemsForOrderSync(orderId: String): List<PurchaseOrderItemEntity>

    /**
     * Gets an item by ID.
     */
    @Query("SELECT * FROM purchase_order_items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): PurchaseOrderItemEntity?

    /**
     * Deletes items for an order.
     */
    @Query("DELETE FROM purchase_order_items WHERE purchase_order_id = :orderId")
    suspend fun deleteItemsForOrder(orderId: String)

    /**
     * Gets items pending sync.
     */
    @Query("SELECT * FROM purchase_order_items WHERE sync_status = 'pending'")
    suspend fun getItemsPendingSync(): List<PurchaseOrderItemEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE purchase_order_items SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all items.
     */
    @Query("DELETE FROM purchase_order_items")
    suspend fun deleteAllItems()
}

