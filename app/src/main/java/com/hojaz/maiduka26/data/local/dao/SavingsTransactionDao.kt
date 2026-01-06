package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.SavingsTransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SavingsTransaction entity operations.
 */
@Dao
interface SavingsTransactionDao : BaseDao<SavingsTransactionEntity> {

    /**
     * Gets transactions for a shop.
     */
    @Query("SELECT * FROM savings_transactions WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY transaction_date DESC")
    fun getTransactionsByShop(shopId: String): Flow<List<SavingsTransactionEntity>>

    /**
     * Gets transactions for a savings goal.
     */
    @Query("SELECT * FROM savings_transactions WHERE savings_goal_id = :goalId AND deleted_at IS NULL ORDER BY transaction_date DESC")
    fun getTransactionsByGoal(goalId: String): Flow<List<SavingsTransactionEntity>>

    /**
     * Gets a transaction by ID.
     */
    @Query("SELECT * FROM savings_transactions WHERE id = :transactionId")
    suspend fun getTransactionById(transactionId: String): SavingsTransactionEntity?

    /**
     * Gets total deposits for a shop.
     */
    @Query("SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) FROM savings_transactions WHERE shop_id = :shopId AND type = 'deposit' AND deleted_at IS NULL")
    suspend fun getTotalDeposits(shopId: String): Double

    /**
     * Gets total withdrawals for a shop.
     */
    @Query("SELECT COALESCE(SUM(CAST(amount AS REAL)), 0) FROM savings_transactions WHERE shop_id = :shopId AND type = 'withdrawal' AND deleted_at IS NULL")
    suspend fun getTotalWithdrawals(shopId: String): Double

    /**
     * Gets transactions pending sync.
     */
    @Query("SELECT * FROM savings_transactions WHERE sync_status = 'pending'")
    suspend fun getTransactionsPendingSync(): List<SavingsTransactionEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE savings_transactions SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all transactions.
     */
    @Query("DELETE FROM savings_transactions")
    suspend fun deleteAllTransactions()
}

