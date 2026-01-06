package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.SavingsGoalEntity
import com.hojaz.maiduka26.data.local.entity.SavingsTransactionEntity
import com.hojaz.maiduka26.data.local.entity.ShopSavingsSettingsEntity
import com.hojaz.maiduka26.domain.model.*
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for Savings entity and domain model conversions.
 */
object SavingsMapper {

    fun SavingsGoalEntity.toDomain(): SavingsGoal {
        return SavingsGoal(
            id = id,
            shopId = shopId,
            name = name,
            description = description,
            targetAmount = BigDecimal(targetAmount),
            targetDate = targetDate?.let { DateTimeUtil.fromMillis(it).toLocalDate() },
            currentAmount = BigDecimal(currentAmount),
            amountWithdrawn = BigDecimal(amountWithdrawn),
            progressPercentage = progressPercentage,
            status = SavingsGoalStatus.valueOf(status.uppercase()),
            completedAt = completedAt?.let { DateTimeUtil.fromMillis(it) },
            startedAt = startedAt?.let { DateTimeUtil.fromMillis(it) },
            icon = icon,
            color = color,
            priority = priority,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun SavingsGoal.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): SavingsGoalEntity {
        return SavingsGoalEntity(
            id = id,
            shopId = shopId,
            name = name,
            description = description,
            targetAmount = targetAmount.toPlainString(),
            targetDate = targetDate?.atStartOfDay()?.let { DateTimeUtil.toMillis(it) },
            currentAmount = currentAmount.toPlainString(),
            amountWithdrawn = amountWithdrawn.toPlainString(),
            progressPercentage = progressPercentage,
            status = status.name.lowercase(),
            completedAt = completedAt?.let { DateTimeUtil.toMillis(it) },
            startedAt = startedAt?.let { DateTimeUtil.toMillis(it) },
            icon = icon,
            color = color,
            priority = priority,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun SavingsTransactionEntity.toDomain(): SavingsTransaction {
        return SavingsTransaction(
            id = id,
            shopId = shopId,
            savingsGoalId = savingsGoalId,
            type = SavingsTransactionType.valueOf(type.uppercase()),
            amount = BigDecimal(amount),
            balanceBefore = BigDecimal(balanceBefore),
            balanceAfter = BigDecimal(balanceAfter),
            transactionDate = DateTimeUtil.fromMillis(transactionDate),
            dailyProfit = dailyProfit?.let { BigDecimal(it) },
            isAutomatic = isAutomatic,
            description = description,
            notes = notes,
            processedBy = processedBy,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun SavingsTransaction.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): SavingsTransactionEntity {
        return SavingsTransactionEntity(
            id = id,
            shopId = shopId,
            savingsGoalId = savingsGoalId,
            type = type.name.lowercase(),
            amount = amount.toPlainString(),
            balanceBefore = balanceBefore.toPlainString(),
            balanceAfter = balanceAfter.toPlainString(),
            transactionDate = DateTimeUtil.toMillis(transactionDate),
            dailyProfit = dailyProfit?.toPlainString(),
            isAutomatic = isAutomatic,
            description = description,
            notes = notes,
            processedBy = processedBy,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun ShopSavingsSettingsEntity.toDomain(): ShopSavingsSettings {
        return ShopSavingsSettings(
            id = id,
            shopId = shopId,
            isEnabled = isEnabled,
            savingsType = SavingsType.valueOf(savingsType.uppercase()),
            savingsPercentage = savingsPercentage?.let { BigDecimal(it) },
            fixedAmount = fixedAmount?.let { BigDecimal(it) },
            targetAmount = targetAmount?.let { BigDecimal(it) },
            targetDate = targetDate?.let { DateTimeUtil.fromMillis(it).toLocalDate() },
            withdrawalFrequency = WithdrawalFrequency.valueOf(withdrawalFrequency.uppercase()),
            autoWithdraw = autoWithdraw,
            minimumWithdrawalAmount = minimumWithdrawalAmount?.let { BigDecimal(it) },
            currentBalance = BigDecimal(currentBalance),
            totalSaved = BigDecimal(totalSaved),
            totalWithdrawn = BigDecimal(totalWithdrawn),
            lastSavingsDate = lastSavingsDate?.let { DateTimeUtil.fromMillis(it) },
            lastWithdrawalDate = lastWithdrawalDate?.let { DateTimeUtil.fromMillis(it) },
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun ShopSavingsSettings.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ShopSavingsSettingsEntity {
        return ShopSavingsSettingsEntity(
            id = id,
            shopId = shopId,
            isEnabled = isEnabled,
            savingsType = savingsType.name.lowercase(),
            savingsPercentage = savingsPercentage?.toPlainString(),
            fixedAmount = fixedAmount?.toPlainString(),
            targetAmount = targetAmount?.toPlainString(),
            targetDate = targetDate?.atStartOfDay()?.let { DateTimeUtil.toMillis(it) },
            withdrawalFrequency = withdrawalFrequency.name.lowercase(),
            autoWithdraw = autoWithdraw,
            minimumWithdrawalAmount = minimumWithdrawalAmount?.toPlainString(),
            currentBalance = currentBalance.toPlainString(),
            totalSaved = totalSaved.toPlainString(),
            totalWithdrawn = totalWithdrawn.toPlainString(),
            lastSavingsDate = lastSavingsDate?.let { DateTimeUtil.toMillis(it) },
            lastWithdrawalDate = lastWithdrawalDate?.let { DateTimeUtil.toMillis(it) },
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<SavingsGoalEntity>.toGoalDomainList(): List<SavingsGoal> = map { it.toDomain() }
    fun List<SavingsTransactionEntity>.toTransactionDomainList(): List<SavingsTransaction> = map { it.toDomain() }
}

