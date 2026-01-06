package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.PurchasePaymentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for PurchasePayment entity operations.
 */
@Dao
interface PurchasePaymentDao : BaseDao<PurchasePaymentEntity> {

    /**
     * Gets payments for a purchase order.
     */
    @Query("SELECT * FROM purchase_payments WHERE purchase_order_id = :orderId AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getPaymentsForOrder(orderId: String): Flow<List<PurchasePaymentEntity>>

    /**
     * Gets payments for a purchase order (sync).
     */
    @Query("SELECT * FROM purchase_payments WHERE purchase_order_id = :orderId AND deleted_at IS NULL")
    suspend fun getPaymentsForOrderSync(orderId: String): List<PurchasePaymentEntity>

    /**
     * Gets a payment by ID.
     */
    @Query("SELECT * FROM purchase_payments WHERE id = :paymentId")
    suspend fun getPaymentById(paymentId: String): PurchasePaymentEntity?

    /**
     * Gets total paid for an order.
     */
    @Query("SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) FROM purchase_payments WHERE purchase_order_id = :orderId AND deleted_at IS NULL")
    suspend fun getTotalPaidForOrder(orderId: String): Double

    /**
     * Gets payments pending sync.
     */
    @Query("SELECT * FROM purchase_payments WHERE sync_status = 'pending'")
    suspend fun getPaymentsPendingSync(): List<PurchasePaymentEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE purchase_payments SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all payments.
     */
    @Query("DELETE FROM purchase_payments")
    suspend fun deleteAllPayments()
}

