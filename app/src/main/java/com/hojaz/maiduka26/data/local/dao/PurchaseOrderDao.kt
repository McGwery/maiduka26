package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.PurchaseOrderEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for PurchaseOrder entity operations.
 */
@Dao
interface PurchaseOrderDao : BaseDao<PurchaseOrderEntity> {

    /**
     * Gets purchase orders where shop is buyer.
     */
    @Query("SELECT * FROM purchase_orders WHERE buyer_shop_id = :shopId AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getPurchaseOrdersAsBuyer(shopId: String): Flow<List<PurchaseOrderEntity>>

    /**
     * Gets purchase orders where shop is seller.
     */
    @Query("SELECT * FROM purchase_orders WHERE seller_shop_id = :shopId AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getPurchaseOrdersAsSeller(shopId: String): Flow<List<PurchaseOrderEntity>>

    /**
     * Gets a purchase order by ID.
     */
    @Query("SELECT * FROM purchase_orders WHERE id = :orderId")
    suspend fun getPurchaseOrderById(orderId: String): PurchaseOrderEntity?

    /**
     * Gets a purchase order by reference number.
     */
    @Query("SELECT * FROM purchase_orders WHERE reference_number = :referenceNumber")
    suspend fun getPurchaseOrderByReference(referenceNumber: String): PurchaseOrderEntity?

    /**
     * Gets purchase orders by status.
     */
    @Query("SELECT * FROM purchase_orders WHERE buyer_shop_id = :shopId AND status = :status AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getPurchaseOrdersByStatus(shopId: String, status: String): Flow<List<PurchaseOrderEntity>>

    /**
     * Updates purchase order status.
     */
    @Query("UPDATE purchase_orders SET status = :status, updated_at = :updatedAt WHERE id = :orderId")
    suspend fun updateStatus(orderId: String, status: String, updatedAt: Long)

    /**
     * Approves a purchase order.
     */
    @Query("UPDATE purchase_orders SET status = 'approved', approved_at = :approvedAt, approved_by = :approvedBy, updated_at = :approvedAt WHERE id = :orderId")
    suspend fun approvePurchaseOrder(orderId: String, approvedBy: String, approvedAt: Long)

    /**
     * Gets purchase orders pending sync.
     */
    @Query("SELECT * FROM purchase_orders WHERE sync_status = 'pending'")
    suspend fun getPurchaseOrdersPendingSync(): List<PurchaseOrderEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE purchase_orders SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Soft deletes a purchase order.
     */
    @Query("UPDATE purchase_orders SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :orderId")
    suspend fun softDelete(orderId: String, deletedAt: Long)

    /**
     * Deletes all purchase orders.
     */
    @Query("DELETE FROM purchase_orders")
    suspend fun deleteAllPurchaseOrders()
}

