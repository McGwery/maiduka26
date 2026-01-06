package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Expense
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

/**
 * Repository interface for expense operations.
 */
interface ExpenseRepository {

    /**
     * Get all expenses for a shop.
     */
    fun getExpenses(shopId: String): Flow<List<Expense>>

    /**
     * Get an expense by ID.
     */
    suspend fun getExpenseById(expenseId: String): Either<Throwable, Expense?>

    /**
     * Get expenses by category.
     */
    fun getExpensesByCategory(shopId: String, category: String): Flow<List<Expense>>

    /**
     * Get expenses by date range.
     */
    fun getExpensesByDateRange(shopId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>>

    /**
     * Get today's expenses.
     */
    fun getTodayExpenses(shopId: String): Flow<List<Expense>>

    /**
     * Get today's total expenses.
     */
    suspend fun getTodayTotalExpenses(shopId: String): Double

    /**
     * Create a new expense.
     */
    suspend fun createExpense(expense: Expense): Either<Throwable, Expense>

    /**
     * Update an existing expense.
     */
    suspend fun updateExpense(expense: Expense): Either<Throwable, Expense>

    /**
     * Delete an expense.
     */
    suspend fun deleteExpense(expenseId: String): Either<Throwable, Unit>

    /**
     * Get expense summary by category.
     */
    suspend fun getExpenseSummaryByCategory(shopId: String, startDate: LocalDate, endDate: LocalDate): Map<String, Double>

    /**
     * Sync expenses with remote server.
     */
    suspend fun syncExpenses(shopId: String): Either<Throwable, Unit>
}

