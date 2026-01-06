package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.StockAdjustmentEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for StockAdjustment entity operations.
 */
@Dao
interface StockAdjustmentDao : BaseDao<StockAdjustmentEntity> {

    /**
     * Gets all adjustments for a product.
     */
    @Query("SELECT * FROM stock_adjustments WHERE product_id = :productId AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getAdjustmentsByProduct(productId: String): Flow<List<StockAdjustmentEntity>>

    /**
     * Gets an adjustment by ID.
     */
    @Query("SELECT * FROM stock_adjustments WHERE id = :adjustmentId")
    suspend fun getAdjustmentById(adjustmentId: String): StockAdjustmentEntity?

    /**
     * Gets adjustments by type.
     */
    @Query("SELECT * FROM stock_adjustments WHERE product_id = :productId AND type = :type AND deleted_at IS NULL ORDER BY created_at DESC")
    fun getAdjustmentsByType(productId: String, type: String): Flow<List<StockAdjustmentEntity>>

    /**
     * Gets adjustments within a date range.
     */
    @Query("""
        SELECT * FROM stock_adjustments 
        WHERE product_id = :productId 
        AND deleted_at IS NULL 
        AND created_at BETWEEN :startDate AND :endDate
        ORDER BY created_at DESC
    """)
    fun getAdjustmentsByDateRange(productId: String, startDate: Long, endDate: Long): Flow<List<StockAdjustmentEntity>>

    /**
     * Gets total value lost to adjustments for a product.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(value_at_time AS REAL)), 0) 
        FROM stock_adjustments 
        WHERE product_id = :productId 
        AND deleted_at IS NULL
        AND type IN ('damaged', 'expired', 'lost', 'theft')
    """)
    suspend fun getTotalValueLost(productId: String): Double

    /**
     * Gets adjustments pending sync.
     */
    @Query("SELECT * FROM stock_adjustments WHERE sync_status = 'pending'")
    suspend fun getAdjustmentsPendingSync(): List<StockAdjustmentEntity>

    /**
     * Updates sync status for an adjustment.
     */
    @Query("UPDATE stock_adjustments SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :adjustmentId")
    suspend fun updateSyncStatus(adjustmentId: String, status: String, syncedAt: Long)

    /**
     * Deletes all adjustments.
     */
    @Query("DELETE FROM stock_adjustments")
    suspend fun deleteAllAdjustments()
}

