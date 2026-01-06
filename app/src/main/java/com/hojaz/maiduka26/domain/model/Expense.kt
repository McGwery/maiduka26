package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain model representing an expense.
 */
data class Expense(
    val id: String,
    val shopId: String,
    val saleId: String? = null,
    val title: String,
    val description: String? = null,
    val category: String,
    val amount: BigDecimal,
    val expenseDate: LocalDate,
    val paymentMethod: String,
    val receiptNumber: String? = null,
    val attachmentUrl: String? = null,
    val recordedBy: String,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Domain model representing a stock adjustment.
 */
data class StockAdjustment(
    val id: String,
    val productId: String,
    val userId: String,
    val type: StockAdjustmentType,
    val quantity: Int,
    val valueAtTime: BigDecimal,
    val previousStock: Int,
    val newStock: Int,
    val reason: String,
    val notes: String? = null,
    val createdAt: LocalDateTime? = null
)

enum class StockAdjustmentType {
    DAMAGED, EXPIRED, LOST, THEFT, PERSONAL_USE, DONATION, RETURN_TO_SUPPLIER, OTHER, RESTOCK, ADJUSTMENT
}

