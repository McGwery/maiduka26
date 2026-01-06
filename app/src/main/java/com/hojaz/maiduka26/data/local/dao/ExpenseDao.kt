package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Expense entity operations.
 */
@Dao
interface ExpenseDao : BaseDao<ExpenseEntity> {

    /**
     * Gets all expenses for a shop.
     */
    @Query("SELECT * FROM expenses WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY expense_date DESC")
    fun getExpensesByShop(shopId: String): Flow<List<ExpenseEntity>>

    /**
     * Gets an expense by ID.
     */
    @Query("SELECT * FROM expenses WHERE id = :expenseId")
    suspend fun getExpenseById(expenseId: String): ExpenseEntity?

    /**
     * Gets expenses by category.
     */
    @Query("SELECT * FROM expenses WHERE shop_id = :shopId AND category = :category AND deleted_at IS NULL ORDER BY expense_date DESC")
    fun getExpensesByCategory(shopId: String, category: String): Flow<List<ExpenseEntity>>

    /**
     * Gets expenses within a date range.
     */
    @Query("""
        SELECT * FROM expenses 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND expense_date BETWEEN :startDate AND :endDate
        ORDER BY expense_date DESC
    """)
    fun getExpensesByDateRange(shopId: String, startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>

    /**
     * Gets total expenses for a date range.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) 
        FROM expenses 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND expense_date BETWEEN :startDate AND :endDate
    """)
    suspend fun getTotalExpensesForDateRange(shopId: String, startDate: Long, endDate: Long): Double

    /**
     * Gets today's total expenses.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) 
        FROM expenses 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND expense_date >= :startOfDay
    """)
    suspend fun getTodayTotalExpenses(shopId: String, startOfDay: Long): Double

    /**
     * Gets today's expenses.
     */
    @Query("""
        SELECT * FROM expenses 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND expense_date >= :startOfDay
        ORDER BY expense_date DESC
    """)
    fun getTodayExpenses(shopId: String, startOfDay: Long): Flow<List<ExpenseEntity>>

    /**
     * Gets expenses pending sync.
     */
    @Query("SELECT * FROM expenses WHERE sync_status = 'pending'")
    suspend fun getExpensesPendingSync(): List<ExpenseEntity>

    /**
     * Updates sync status for an expense.
     */
    @Query("UPDATE expenses SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :expenseId")
    suspend fun updateSyncStatus(expenseId: String, status: String, syncedAt: Long)

    /**
     * Soft deletes an expense.
     */
    @Query("UPDATE expenses SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :expenseId")
    suspend fun softDelete(expenseId: String, deletedAt: Long)

    /**
     * Deletes all expenses.
     */
    @Query("DELETE FROM expenses")
    suspend fun deleteAllExpenses()
}

