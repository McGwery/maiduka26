package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.ExpenseDao
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.mapper.ExpenseMapper.toDomain
import com.hojaz.maiduka26.data.mapper.ExpenseMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.ExpenseMapper.toEntity
import com.hojaz.maiduka26.data.remote.api.ApiService
import com.hojaz.maiduka26.domain.model.Expense
import com.hojaz.maiduka26.domain.repository.ExpenseRepository
import com.hojaz.maiduka26.util.DateTimeUtil
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ExpenseRepository.
 * Follows offline-first approach: local database is the source of truth.
 */
@Singleton
class ExpenseRepositoryImpl @Inject constructor(
    private val expenseDao: ExpenseDao,
    private val apiService: ApiService,
    private val networkMonitor: NetworkMonitor,
    private val preferencesManager: PreferencesManager
) : ExpenseRepository {

    override fun getExpenses(shopId: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByShop(shopId).map { it.toDomainList() }
    }

    override suspend fun getExpenseById(expenseId: String): Either<Throwable, Expense?> {
        return try {
            expenseDao.getExpenseById(expenseId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting expense by ID: $expenseId")
            e.left()
        }
    }

    override fun getExpensesByCategory(shopId: String, category: String): Flow<List<Expense>> {
        return expenseDao.getExpensesByCategory(shopId, category).map { it.toDomainList() }
    }

    override fun getExpensesByDateRange(shopId: String, startDate: LocalDate, endDate: LocalDate): Flow<List<Expense>> {
        val startMillis = DateTimeUtil.toMillis(DateTimeUtil.startOfDay(startDate))
        val endMillis = DateTimeUtil.toMillis(DateTimeUtil.endOfDay(endDate))
        return expenseDao.getExpensesByDateRange(shopId, startMillis, endMillis).map { it.toDomainList() }
    }

    override fun getTodayExpenses(shopId: String): Flow<List<Expense>> {
        val startOfDay = DateTimeUtil.getStartOfDay()
        return expenseDao.getExpensesByDateRange(
            shopId,
            startOfDay,
            System.currentTimeMillis()
        ).map { it.toDomainList() }
    }

    override suspend fun getTodayTotalExpenses(shopId: String): Double {
        val startOfDay = DateTimeUtil.getStartOfDay()
        return expenseDao.getTodayTotalExpenses(shopId, startOfDay)
    }

    override suspend fun createExpense(expense: Expense): Either<Throwable, Expense> {
        return try {
            val entity = expense.toEntity(syncStatus = "pending")
            expenseDao.insert(entity)
            Timber.d("Expense created locally: ${expense.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            expense.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating expense")
            e.left()
        }
    }

    override suspend fun updateExpense(expense: Expense): Either<Throwable, Expense> {
        return try {
            val entity = expense.toEntity(syncStatus = "pending")
            expenseDao.update(entity)
            Timber.d("Expense updated locally: ${expense.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            expense.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating expense")
            e.left()
        }
    }

    override suspend fun deleteExpense(expenseId: String): Either<Throwable, Unit> {
        return try {
            expenseDao.softDelete(expenseId, System.currentTimeMillis())
            Timber.d("Expense soft deleted: $expenseId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting expense")
            e.left()
        }
    }

    override suspend fun getExpenseSummaryByCategory(shopId: String, startDate: LocalDate, endDate: LocalDate): Map<String, Double> {
        // TODO: Implement category summary aggregation
        return emptyMap()
    }

    override suspend fun syncExpenses(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pendingExpenses = expenseDao.getExpensesPendingSync()

            // TODO: Upload pending expenses to server
            // TODO: Download updated expenses from server

            pendingExpenses.forEach { expense ->
                expenseDao.updateSyncStatus(expense.id, "synced", System.currentTimeMillis())
            }

            Timber.d("Expenses synced: ${pendingExpenses.size} uploaded")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing expenses")
            e.left()
        }
    }
}

