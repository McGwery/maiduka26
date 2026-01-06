package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.SavingsGoalDao
import com.hojaz.maiduka26.data.local.dao.SavingsTransactionDao
import com.hojaz.maiduka26.data.local.dao.ShopSavingsSettingsDao
import com.hojaz.maiduka26.data.local.entity.SavingsTransactionEntity
import com.hojaz.maiduka26.data.local.entity.ShopSavingsSettingsEntity
import com.hojaz.maiduka26.data.mapper.SavingsMapper.toDomain
import com.hojaz.maiduka26.data.mapper.SavingsMapper.toEntity
import com.hojaz.maiduka26.data.mapper.SavingsMapper.toGoalDomainList
import com.hojaz.maiduka26.data.mapper.SavingsMapper.toTransactionDomainList
import com.hojaz.maiduka26.domain.model.SavingsGoal
import com.hojaz.maiduka26.domain.model.SavingsTransaction
import com.hojaz.maiduka26.domain.model.SavingsTransactionType
import com.hojaz.maiduka26.domain.model.ShopSavingsSettings
import com.hojaz.maiduka26.domain.repository.SavingsRepository
import com.hojaz.maiduka26.util.DateTimeUtil
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SavingsRepository.
 */
@Singleton
class SavingsRepositoryImpl @Inject constructor(
    private val savingsGoalDao: SavingsGoalDao,
    private val savingsTransactionDao: SavingsTransactionDao,
    private val shopSavingsSettingsDao: ShopSavingsSettingsDao,
    private val networkMonitor: NetworkMonitor
) : SavingsRepository {

    // ==================== Savings Settings ====================

    override fun getSavingsSettings(shopId: String): Flow<ShopSavingsSettings?> {
        return shopSavingsSettingsDao.getSettingsByShopFlow(shopId).map { it?.toDomain() }
    }

    override suspend fun updateSavingsSettings(settings: ShopSavingsSettings): Either<Throwable, ShopSavingsSettings> {
        return try {
            val entity = settings.toEntity(syncStatus = "pending")
            shopSavingsSettingsDao.update(entity)
            Timber.d("Savings settings updated: ${settings.shopId}")
            settings.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating savings settings")
            e.left()
        }
    }

    override suspend fun toggleSavings(shopId: String, enabled: Boolean): Either<Throwable, Unit> {
        return try {
            shopSavingsSettingsDao.updateEnabledStatus(shopId, enabled, System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error toggling savings")
            e.left()
        }
    }

    // ==================== Savings Goals ====================

    override fun getSavingsGoals(shopId: String): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getSavingsGoalsByShop(shopId).map { it.toGoalDomainList() }
    }

    override fun getActiveSavingsGoals(shopId: String): Flow<List<SavingsGoal>> {
        return savingsGoalDao.getActiveSavingsGoals(shopId).map { it.toGoalDomainList() }
    }

    override suspend fun getSavingsGoalById(goalId: String): Either<Throwable, SavingsGoal?> {
        return try {
            savingsGoalDao.getSavingsGoalById(goalId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting savings goal: $goalId")
            e.left()
        }
    }

    override suspend fun createSavingsGoal(goal: SavingsGoal): Either<Throwable, SavingsGoal> {
        return try {
            val entity = goal.toEntity(syncStatus = "pending")
            savingsGoalDao.insert(entity)
            Timber.d("Savings goal created: ${goal.id}")
            goal.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating savings goal")
            e.left()
        }
    }

    override suspend fun updateSavingsGoal(goal: SavingsGoal): Either<Throwable, SavingsGoal> {
        return try {
            val entity = goal.toEntity(syncStatus = "pending")
            savingsGoalDao.update(entity)
            Timber.d("Savings goal updated: ${goal.id}")
            goal.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating savings goal")
            e.left()
        }
    }

    override suspend fun deleteSavingsGoal(goalId: String): Either<Throwable, Unit> {
        return try {
            val goal = savingsGoalDao.getSavingsGoalById(goalId)
            goal?.let {
                savingsGoalDao.delete(it)
            }
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting savings goal")
            e.left()
        }
    }

    // ==================== Savings Transactions ====================

    override fun getSavingsTransactions(shopId: String): Flow<List<SavingsTransaction>> {
        return savingsTransactionDao.getTransactionsByShop(shopId).map { it.toTransactionDomainList() }
    }

    override fun getTransactionsForGoal(goalId: String): Flow<List<SavingsTransaction>> {
        return savingsTransactionDao.getTransactionsByGoal(goalId).map { it.toTransactionDomainList() }
    }

    override suspend fun deposit(
        shopId: String,
        amount: Double,
        goalId: String?,
        description: String?
    ): Either<Throwable, SavingsTransaction> {
        return try {
            val settings = shopSavingsSettingsDao.getSettingsByShop(shopId)
            val currentBalance = BigDecimal(settings?.currentBalance ?: "0")
            val newBalance = currentBalance.add(BigDecimal(amount))

            val transaction = SavingsTransactionEntity(
                id = UUID.randomUUID().toString(),
                shopId = shopId,
                savingsGoalId = goalId,
                type = "deposit",
                amount = BigDecimal(amount).toPlainString(),
                balanceBefore = currentBalance.toPlainString(),
                balanceAfter = newBalance.toPlainString(),
                transactionDate = System.currentTimeMillis(),
                isAutomatic = false,
                description = description,
                createdAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )

            savingsTransactionDao.insert(transaction)

            // Update settings balance
            val totalSaved = BigDecimal(settings?.totalSaved ?: "0").add(BigDecimal(amount))
            shopSavingsSettingsDao.updateBalance(
                shopId,
                newBalance.toPlainString(),
                totalSaved.toPlainString(),
                System.currentTimeMillis(),
                System.currentTimeMillis()
            )

            // Update goal progress if goalId provided
            goalId?.let {
                val goal = savingsGoalDao.getSavingsGoalById(it)
                goal?.let { g ->
                    val newAmount = BigDecimal(g.currentAmount).add(BigDecimal(amount))
                    val progress = if (BigDecimal(g.targetAmount) > BigDecimal.ZERO) {
                        newAmount.multiply(BigDecimal(100))
                            .divide(BigDecimal(g.targetAmount), 0, java.math.RoundingMode.HALF_UP)
                            .toInt()
                    } else 0
                    savingsGoalDao.updateProgress(it, newAmount.toPlainString(), progress, System.currentTimeMillis())
                }
            }

            Timber.d("Deposit made: $amount to shop $shopId")
            transaction.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error making deposit")
            e.left()
        }
    }

    override suspend fun withdraw(
        shopId: String,
        amount: Double,
        goalId: String?,
        reason: String?
    ): Either<Throwable, SavingsTransaction> {
        return try {
            val settings = shopSavingsSettingsDao.getSettingsByShop(shopId)
            val currentBalance = BigDecimal(settings?.currentBalance ?: "0")

            if (BigDecimal(amount) > currentBalance) {
                return Exception("Insufficient balance").left()
            }

            val newBalance = currentBalance.subtract(BigDecimal(amount))

            val transaction = SavingsTransactionEntity(
                id = UUID.randomUUID().toString(),
                shopId = shopId,
                savingsGoalId = goalId,
                type = "withdrawal",
                amount = BigDecimal(amount).toPlainString(),
                balanceBefore = currentBalance.toPlainString(),
                balanceAfter = newBalance.toPlainString(),
                transactionDate = System.currentTimeMillis(),
                isAutomatic = false,
                description = reason,
                createdAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )

            savingsTransactionDao.insert(transaction)

            // Update settings balance
            val totalWithdrawn = BigDecimal(settings?.totalWithdrawn ?: "0").add(BigDecimal(amount))
            shopSavingsSettingsDao.updateBalance(
                shopId,
                newBalance.toPlainString(),
                settings?.totalSaved ?: "0",
                System.currentTimeMillis(),
                System.currentTimeMillis()
            )

            Timber.d("Withdrawal made: $amount from shop $shopId")
            transaction.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error making withdrawal")
            e.left()
        }
    }

    override suspend fun getCurrentBalance(shopId: String): Double {
        val settings = shopSavingsSettingsDao.getSettingsByShop(shopId)
        return settings?.currentBalance?.toDoubleOrNull() ?: 0.0
    }

    override suspend fun syncSavings(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pendingGoals = savingsGoalDao.getGoalsPendingSync()
            pendingGoals.forEach {
                savingsGoalDao.updateSyncStatus(it.id, "synced", System.currentTimeMillis())
            }

            val pendingTransactions = savingsTransactionDao.getTransactionsPendingSync()
            pendingTransactions.forEach {
                savingsTransactionDao.updateSyncStatus(it.id, "synced", System.currentTimeMillis())
            }

            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing savings")
            e.left()
        }
    }
}

