package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.SavingsGoalEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for SavingsGoal entity operations.
 */
@Dao
interface SavingsGoalDao : BaseDao<SavingsGoalEntity> {

    /**
     * Gets all savings goals for a shop.
     */
    @Query("SELECT * FROM savings_goals WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY priority DESC, created_at DESC")
    fun getSavingsGoalsByShop(shopId: String): Flow<List<SavingsGoalEntity>>

    /**
     * Gets active savings goals for a shop.
     */
    @Query("SELECT * FROM savings_goals WHERE shop_id = :shopId AND status = 'active' AND deleted_at IS NULL ORDER BY priority DESC")
    fun getActiveSavingsGoals(shopId: String): Flow<List<SavingsGoalEntity>>

    /**
     * Gets a savings goal by ID.
     */
    @Query("SELECT * FROM savings_goals WHERE id = :goalId")
    suspend fun getSavingsGoalById(goalId: String): SavingsGoalEntity?

    /**
     * Updates goal progress.
     */
    @Query("""
        UPDATE savings_goals SET 
        current_amount = :currentAmount,
        progress_percentage = :progressPercentage,
        updated_at = :updatedAt
        WHERE id = :goalId
    """)
    suspend fun updateProgress(goalId: String, currentAmount: String, progressPercentage: Int, updatedAt: Long)

    /**
     * Completes a goal.
     */
    @Query("UPDATE savings_goals SET status = 'completed', completed_at = :completedAt, updated_at = :completedAt WHERE id = :goalId")
    suspend fun completeGoal(goalId: String, completedAt: Long)

    /**
     * Gets goals pending sync.
     */
    @Query("SELECT * FROM savings_goals WHERE sync_status = 'pending'")
    suspend fun getGoalsPendingSync(): List<SavingsGoalEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE savings_goals SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all goals.
     */
    @Query("DELETE FROM savings_goals")
    suspend fun deleteAllGoals()
}

