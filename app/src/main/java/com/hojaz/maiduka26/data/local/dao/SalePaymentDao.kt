package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.SalePaymentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SalePayment entity operations.
 */
@Dao
interface SalePaymentDao : BaseDao<SalePaymentEntity> {

    /**
     * Gets all payments for a sale.
     */
    @Query("SELECT * FROM sale_payments WHERE sale_id = :saleId AND deleted_at IS NULL ORDER BY payment_date DESC")
    fun getPaymentsBySale(saleId: String): Flow<List<SalePaymentEntity>>

    /**
     * Gets all payments for a sale (suspend version).
     */
    @Query("SELECT * FROM sale_payments WHERE sale_id = :saleId AND deleted_at IS NULL")
    suspend fun getPaymentsBySaleSync(saleId: String): List<SalePaymentEntity>

    /**
     * Gets a payment by ID.
     */
    @Query("SELECT * FROM sale_payments WHERE id = :paymentId")
    suspend fun getPaymentById(paymentId: String): SalePaymentEntity?

    /**
     * Gets total paid for a sale.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) 
        FROM sale_payments 
        WHERE sale_id = :saleId 
        AND deleted_at IS NULL
    """)
    suspend fun getTotalPaidForSale(saleId: String): Double

    /**
     * Gets payments by payment method.
     */
    @Query("""
        SELECT * FROM sale_payments 
        WHERE sale_id = :saleId 
        AND payment_method = :paymentMethod 
        AND deleted_at IS NULL
    """)
    fun getPaymentsByMethod(saleId: String, paymentMethod: String): Flow<List<SalePaymentEntity>>

    /**
     * Gets payments pending sync.
     */
    @Query("SELECT * FROM sale_payments WHERE sync_status = 'pending'")
    suspend fun getPaymentsPendingSync(): List<SalePaymentEntity>

    /**
     * Updates sync status for a payment.
     */
    @Query("UPDATE sale_payments SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :paymentId")
    suspend fun updateSyncStatus(paymentId: String, status: String, syncedAt: Long)

    /**
     * Deletes all payments for a sale.
     */
    @Query("DELETE FROM sale_payments WHERE sale_id = :saleId")
    suspend fun deletePaymentsForSale(saleId: String)

    /**
     * Deletes all sale payments.
     */
    @Query("DELETE FROM sale_payments")
    suspend fun deleteAllPayments()
}

