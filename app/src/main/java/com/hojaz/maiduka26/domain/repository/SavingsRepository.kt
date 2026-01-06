package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.SavingsGoal
import com.hojaz.maiduka26.domain.model.SavingsTransaction
import com.hojaz.maiduka26.domain.model.ShopSavingsSettings
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for savings operations.
 */
interface SavingsRepository {

    // ==================== Savings Settings ====================

    /**
     * Get savings settings for a shop.
     */
    fun getSavingsSettings(shopId: String): Flow<ShopSavingsSettings?>

    /**
     * Update savings settings.
     */
    suspend fun updateSavingsSettings(settings: ShopSavingsSettings): Either<Throwable, ShopSavingsSettings>

    /**
     * Enable/disable savings for a shop.
     */
    suspend fun toggleSavings(shopId: String, enabled: Boolean): Either<Throwable, Unit>

    // ==================== Savings Goals ====================

    /**
     * Get all savings goals for a shop.
     */
    fun getSavingsGoals(shopId: String): Flow<List<SavingsGoal>>

    /**
     * Get active savings goals.
     */
    fun getActiveSavingsGoals(shopId: String): Flow<List<SavingsGoal>>

    /**
     * Get a savings goal by ID.
     */
    suspend fun getSavingsGoalById(goalId: String): Either<Throwable, SavingsGoal?>

    /**
     * Create a savings goal.
     */
    suspend fun createSavingsGoal(goal: SavingsGoal): Either<Throwable, SavingsGoal>

    /**
     * Update a savings goal.
     */
    suspend fun updateSavingsGoal(goal: SavingsGoal): Either<Throwable, SavingsGoal>

    /**
     * Delete a savings goal.
     */
    suspend fun deleteSavingsGoal(goalId: String): Either<Throwable, Unit>

    // ==================== Savings Transactions ====================

    /**
     * Get transactions for a shop.
     */
    fun getSavingsTransactions(shopId: String): Flow<List<SavingsTransaction>>

    /**
     * Get transactions for a goal.
     */
    fun getTransactionsForGoal(goalId: String): Flow<List<SavingsTransaction>>

    /**
     * Make a deposit.
     */
    suspend fun deposit(
        shopId: String,
        amount: Double,
        goalId: String? = null,
        description: String? = null
    ): Either<Throwable, SavingsTransaction>

    /**
     * Make a withdrawal.
     */
    suspend fun withdraw(
        shopId: String,
        amount: Double,
        goalId: String? = null,
        reason: String? = null
    ): Either<Throwable, SavingsTransaction>

    /**
     * Get current balance.
     */
    suspend fun getCurrentBalance(shopId: String): Double

    /**
     * Sync savings data with remote server.
     */
    suspend fun syncSavings(shopId: String): Either<Throwable, Unit>
}

