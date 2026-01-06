package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain model representing a savings goal.
 */
data class SavingsGoal(
    val id: String,
    val shopId: String,
    val name: String,
    val description: String? = null,
    val targetAmount: BigDecimal,
    val targetDate: LocalDate? = null,
    val currentAmount: BigDecimal = BigDecimal.ZERO,
    val amountWithdrawn: BigDecimal = BigDecimal.ZERO,
    val progressPercentage: Int = 0,
    val status: SavingsGoalStatus = SavingsGoalStatus.ACTIVE,
    val completedAt: LocalDateTime? = null,
    val startedAt: LocalDateTime? = null,
    val icon: String? = null,
    val color: String? = null,
    val priority: Int = 0,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val remainingAmount: BigDecimal get() = targetAmount.subtract(currentAmount).max(BigDecimal.ZERO)
    val isCompleted: Boolean get() = status == SavingsGoalStatus.COMPLETED
    val isActive: Boolean get() = status == SavingsGoalStatus.ACTIVE

    fun calculateProgress(): Int {
        if (targetAmount <= BigDecimal.ZERO) return 0
        return currentAmount.multiply(BigDecimal(100))
            .divide(targetAmount, 0, java.math.RoundingMode.HALF_UP)
            .toInt()
            .coerceIn(0, 100)
    }
}

enum class SavingsGoalStatus {
    ACTIVE, COMPLETED, CANCELLED, PAUSED
}

/**
 * Domain model representing a savings transaction.
 */
data class SavingsTransaction(
    val id: String,
    val shopId: String,
    val savingsGoalId: String? = null,
    val type: SavingsTransactionType,
    val amount: BigDecimal,
    val balanceBefore: BigDecimal,
    val balanceAfter: BigDecimal,
    val transactionDate: LocalDateTime,
    val dailyProfit: BigDecimal? = null,
    val isAutomatic: Boolean = true,
    val description: String? = null,
    val notes: String? = null,
    val processedBy: String? = null,
    val createdAt: LocalDateTime? = null
)

enum class SavingsTransactionType {
    DEPOSIT, WITHDRAWAL
}

/**
 * Domain model representing shop savings settings.
 */
data class ShopSavingsSettings(
    val id: String,
    val shopId: String,
    val isEnabled: Boolean = false,
    val savingsType: SavingsType = SavingsType.PERCENTAGE,
    val savingsPercentage: BigDecimal? = null,
    val fixedAmount: BigDecimal? = null,
    val targetAmount: BigDecimal? = null,
    val targetDate: LocalDate? = null,
    val withdrawalFrequency: WithdrawalFrequency = WithdrawalFrequency.MONTHLY,
    val autoWithdraw: Boolean = false,
    val minimumWithdrawalAmount: BigDecimal? = null,
    val currentBalance: BigDecimal = BigDecimal.ZERO,
    val totalSaved: BigDecimal = BigDecimal.ZERO,
    val totalWithdrawn: BigDecimal = BigDecimal.ZERO,
    val lastSavingsDate: LocalDateTime? = null,
    val lastWithdrawalDate: LocalDateTime? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

enum class SavingsType {
    PERCENTAGE, FIXED_AMOUNT
}

enum class WithdrawalFrequency {
    NONE, WEEKLY, BI_WEEKLY, MONTHLY, QUARTERLY, WHEN_GOAL_REACHED
}

