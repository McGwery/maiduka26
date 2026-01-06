package com.hojaz.maiduka26.domain.usecase.savings

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.SavingsGoal
import com.hojaz.maiduka26.domain.model.SavingsTransaction
import com.hojaz.maiduka26.domain.model.ShopSavingsSettings
import com.hojaz.maiduka26.domain.repository.SavingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting savings settings.
 */
class GetSavingsSettingsUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    operator fun invoke(shopId: String): Flow<ShopSavingsSettings?> {
        return savingsRepository.getSavingsSettings(shopId)
    }
}

/**
 * Use case for updating savings settings.
 */
class UpdateSavingsSettingsUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(settings: ShopSavingsSettings): Either<Throwable, ShopSavingsSettings> {
        return savingsRepository.updateSavingsSettings(settings)
    }
}

/**
 * Use case for toggling savings.
 */
class ToggleSavingsUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(shopId: String, enabled: Boolean): Either<Throwable, Unit> {
        return savingsRepository.toggleSavings(shopId, enabled)
    }
}

/**
 * Use case for getting savings goals.
 */
class GetSavingsGoalsUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    operator fun invoke(shopId: String): Flow<List<SavingsGoal>> {
        return savingsRepository.getSavingsGoals(shopId)
    }
}

/**
 * Use case for getting active savings goals.
 */
class GetActiveSavingsGoalsUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    operator fun invoke(shopId: String): Flow<List<SavingsGoal>> {
        return savingsRepository.getActiveSavingsGoals(shopId)
    }
}

/**
 * Use case for getting a savings goal by ID.
 */
class GetSavingsGoalByIdUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(goalId: String): Either<Throwable, SavingsGoal?> {
        return savingsRepository.getSavingsGoalById(goalId)
    }
}

/**
 * Use case for creating a savings goal.
 */
class CreateSavingsGoalUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(goal: SavingsGoal): Either<Throwable, SavingsGoal> {
        return savingsRepository.createSavingsGoal(goal)
    }
}

/**
 * Use case for updating a savings goal.
 */
class UpdateSavingsGoalUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(goal: SavingsGoal): Either<Throwable, SavingsGoal> {
        return savingsRepository.updateSavingsGoal(goal)
    }
}

/**
 * Use case for deleting a savings goal.
 */
class DeleteSavingsGoalUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(goalId: String): Either<Throwable, Unit> {
        return savingsRepository.deleteSavingsGoal(goalId)
    }
}

/**
 * Use case for getting savings transactions.
 */
class GetSavingsTransactionsUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    operator fun invoke(shopId: String): Flow<List<SavingsTransaction>> {
        return savingsRepository.getSavingsTransactions(shopId)
    }
}

/**
 * Use case for making a deposit.
 */
class MakeDepositUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(
        shopId: String,
        amount: Double,
        goalId: String? = null,
        description: String? = null
    ): Either<Throwable, SavingsTransaction> {
        return savingsRepository.deposit(shopId, amount, goalId, description)
    }
}

/**
 * Use case for making a withdrawal.
 */
class MakeWithdrawalUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(
        shopId: String,
        amount: Double,
        goalId: String? = null,
        reason: String? = null
    ): Either<Throwable, SavingsTransaction> {
        return savingsRepository.withdraw(shopId, amount, goalId, reason)
    }
}

/**
 * Use case for getting current balance.
 */
class GetCurrentBalanceUseCase @Inject constructor(
    private val savingsRepository: SavingsRepository
) {
    suspend operator fun invoke(shopId: String): Double {
        return savingsRepository.getCurrentBalance(shopId)
    }
}

