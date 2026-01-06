package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.SaleEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Sale entity operations.
 */
@Dao
interface SaleDao : BaseDao<SaleEntity> {

    /**
     * Gets all sales for a shop.
     */
    @Query("SELECT * FROM sales WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY sale_date DESC")
    fun getSalesByShop(shopId: String): Flow<List<SaleEntity>>

    /**
     * Gets a sale by ID.
     */
    @Query("SELECT * FROM sales WHERE id = :saleId")
    suspend fun getSaleById(saleId: String): SaleEntity?

    /**
     * Gets a sale by ID as Flow.
     */
    @Query("SELECT * FROM sales WHERE id = :saleId")
    fun getSaleByIdFlow(saleId: String): Flow<SaleEntity?>

    /**
     * Gets a sale by sale number.
     */
    @Query("SELECT * FROM sales WHERE sale_number = :saleNumber")
    suspend fun getSaleBySaleNumber(saleNumber: String): SaleEntity?

    /**
     * Gets sales by customer.
     */
    @Query("SELECT * FROM sales WHERE shop_id = :shopId AND customer_id = :customerId AND deleted_at IS NULL ORDER BY sale_date DESC")
    fun getSalesByCustomer(shopId: String, customerId: String): Flow<List<SaleEntity>>

    /**
     * Gets sales by status.
     */
    @Query("SELECT * FROM sales WHERE shop_id = :shopId AND status = :status AND deleted_at IS NULL ORDER BY sale_date DESC")
    fun getSalesByStatus(shopId: String, status: String): Flow<List<SaleEntity>>

    /**
     * Gets sales by payment status.
     */
    @Query("SELECT * FROM sales WHERE shop_id = :shopId AND payment_status = :paymentStatus AND deleted_at IS NULL ORDER BY sale_date DESC")
    fun getSalesByPaymentStatus(shopId: String, paymentStatus: String): Flow<List<SaleEntity>>

    /**
     * Gets sales within a date range.
     */
    @Query("""
        SELECT * FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND sale_date BETWEEN :startDate AND :endDate
        ORDER BY sale_date DESC
    """)
    fun getSalesByDateRange(shopId: String, startDate: Long, endDate: Long): Flow<List<SaleEntity>>

    /**
     * Gets today's sales.
     */
    @Query("""
        SELECT * FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND sale_date >= :startOfDay
        ORDER BY sale_date DESC
    """)
    fun getTodaySales(shopId: String, startOfDay: Long): Flow<List<SaleEntity>>

    /**
     * Gets total sales amount for today.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(total_amount AS REAL)), 0) 
        FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND status = 'completed'
        AND sale_date >= :startOfDay
    """)
    suspend fun getTodayTotalSales(shopId: String, startOfDay: Long): Double

    /**
     * Gets total profit for today.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(profit_amount AS REAL)), 0) 
        FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND status = 'completed'
        AND sale_date >= :startOfDay
    """)
    suspend fun getTodayTotalProfit(shopId: String, startOfDay: Long): Double

    /**
     * Gets total sales amount for a date range.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(total_amount AS REAL)), 0) 
        FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND status = 'completed'
        AND sale_date BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalSalesForDateRange(shopId: String, startDate: Long, endDate: Long): Double

    /**
     * Gets total profit for a date range.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(profit_amount AS REAL)), 0) 
        FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND status = 'completed'
        AND sale_date BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalProfitForDateRange(shopId: String, startDate: Long, endDate: Long): Double

    /**
     * Gets sales count for today.
     */
    @Query("""
        SELECT COUNT(*) 
        FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND status = 'completed'
        AND sale_date >= :startOfDay
    """)
    suspend fun getTodaySalesCount(shopId: String, startOfDay: Long): Int

    /**
     * Gets sales with pending debt.
     */
    @Query("""
        SELECT * FROM sales 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND payment_status IN ('debt', 'partially_paid')
        ORDER BY sale_date DESC
    """)
    fun getSalesWithDebt(shopId: String): Flow<List<SaleEntity>>

    /**
     * Updates sale status.
     */
    @Query("UPDATE sales SET status = :status, updated_at = :updatedAt WHERE id = :saleId")
    suspend fun updateStatus(saleId: String, status: String, updatedAt: Long)

    /**
     * Updates payment status and amounts.
     */
    @Query("""
        UPDATE sales SET 
        payment_status = :paymentStatus,
        amount_paid = :amountPaid,
        debt_amount = :debtAmount,
        updated_at = :updatedAt 
        WHERE id = :saleId
    """)
    suspend fun updatePaymentInfo(
        saleId: String,
        paymentStatus: String,
        amountPaid: String,
        debtAmount: String,
        updatedAt: Long
    )

    /**
     * Gets sales pending sync.
     */
    @Query("SELECT * FROM sales WHERE sync_status = 'pending'")
    suspend fun getSalesPendingSync(): List<SaleEntity>

    /**
     * Updates sync status for a sale.
     */
    @Query("UPDATE sales SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :saleId")
    suspend fun updateSyncStatus(saleId: String, status: String, syncedAt: Long)

    /**
     * Soft deletes a sale.
     */
    @Query("UPDATE sales SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :saleId")
    suspend fun softDelete(saleId: String, deletedAt: Long)

    /**
     * Deletes all sales.
     */
    @Query("DELETE FROM sales")
    suspend fun deleteAllSales()
}

