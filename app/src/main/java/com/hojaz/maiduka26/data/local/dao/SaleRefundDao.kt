package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.SaleRefundEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SaleRefund entity operations.
 */
@Dao
interface SaleRefundDao : BaseDao<SaleRefundEntity> {

    /**
     * Gets refunds for a sale.
     */
    @Query("SELECT * FROM sale_refunds WHERE sale_id = :saleId AND deleted_at IS NULL ORDER BY refund_date DESC")
    fun getRefundsForSale(saleId: String): Flow<List<SaleRefundEntity>>

    /**
     * Gets refunds for a sale (sync).
     */
    @Query("SELECT * FROM sale_refunds WHERE sale_id = :saleId AND deleted_at IS NULL")
    suspend fun getRefundsForSaleSync(saleId: String): List<SaleRefundEntity>

    /**
     * Gets a refund by ID.
     */
    @Query("SELECT * FROM sale_refunds WHERE id = :refundId")
    suspend fun getRefundById(refundId: String): SaleRefundEntity?

    /**
     * Gets total refunded for a sale.
     */
    @Query("SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) FROM sale_refunds WHERE sale_id = :saleId AND deleted_at IS NULL")
    suspend fun getTotalRefundedForSale(saleId: String): Double

    /**
     * Gets refunds within a date range.
     */
    @Query("""
        SELECT * FROM sale_refunds 
        WHERE deleted_at IS NULL 
        AND refund_date BETWEEN :startDate AND :endDate
        ORDER BY refund_date DESC
    """)
    fun getRefundsByDateRange(startDate: Long, endDate: Long): Flow<List<SaleRefundEntity>>

    /**
     * Gets refunds pending sync.
     */
    @Query("SELECT * FROM sale_refunds WHERE sync_status = 'pending'")
    suspend fun getRefundsPendingSync(): List<SaleRefundEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE sale_refunds SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all refunds.
     */
    @Query("DELETE FROM sale_refunds")
    suspend fun deleteAllRefunds()
}
