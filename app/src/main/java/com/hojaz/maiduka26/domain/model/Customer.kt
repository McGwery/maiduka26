package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a customer.
 */
data class Customer(
    val id: String,
    val shopId: String,
    val name: String,
    val phone: String? = null,
    val email: String? = null,
    val address: String? = null,
    val creditLimit: BigDecimal = BigDecimal.ZERO,
    val currentDebt: BigDecimal = BigDecimal.ZERO,
    val totalPurchases: BigDecimal = BigDecimal.ZERO,
    val totalPaid: BigDecimal = BigDecimal.ZERO,
    val notes: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val hasDebt: Boolean get() = currentDebt > BigDecimal.ZERO

    val isOverCreditLimit: Boolean
        get() = creditLimit > BigDecimal.ZERO && currentDebt > creditLimit

    val availableCredit: BigDecimal
        get() = if (creditLimit > BigDecimal.ZERO) creditLimit.subtract(currentDebt).max(BigDecimal.ZERO) else BigDecimal.ZERO

    val displayContact: String? get() = phone ?: email
}

