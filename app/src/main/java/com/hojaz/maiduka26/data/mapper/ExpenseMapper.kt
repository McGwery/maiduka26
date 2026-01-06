package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.ExpenseEntity
import com.hojaz.maiduka26.domain.model.Expense
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for Expense entity and domain model conversions.
 */
object ExpenseMapper {

    fun ExpenseEntity.toDomain(): Expense {
        return Expense(
            id = id,
            shopId = shopId,
            saleId = saleId,
            title = title,
            description = description,
            category = category,
            amount = BigDecimal(amount),
            expenseDate = DateTimeUtil.fromMillis(expenseDate).toLocalDate(),
            paymentMethod = paymentMethod,
            receiptNumber = receiptNumber,
            attachmentUrl = attachmentUrl,
            recordedBy = recordedBy,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Expense.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ExpenseEntity {
        return ExpenseEntity(
            id = id,
            shopId = shopId,
            saleId = saleId,
            title = title,
            description = description,
            category = category,
            amount = amount.toPlainString(),
            expenseDate = expenseDate.atStartOfDay().let { DateTimeUtil.toMillis(it) },
            paymentMethod = paymentMethod,
            receiptNumber = receiptNumber,
            attachmentUrl = attachmentUrl,
            recordedBy = recordedBy,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<ExpenseEntity>.toDomainList(): List<Expense> = map { it.toDomain() }
}

