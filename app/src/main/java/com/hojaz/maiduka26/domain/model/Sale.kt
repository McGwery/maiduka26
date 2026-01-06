package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a sale transaction.
 */
data class Sale(
    val id: String,
    val shopId: String,
    val customerId: String? = null,
    val userId: String,
    val saleNumber: String,
    val subtotal: BigDecimal,
    val taxRate: BigDecimal = BigDecimal.ZERO,
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val discountPercentage: BigDecimal = BigDecimal.ZERO,
    val totalAmount: BigDecimal,
    val amountPaid: BigDecimal = BigDecimal.ZERO,
    val changeAmount: BigDecimal = BigDecimal.ZERO,
    val debtAmount: BigDecimal = BigDecimal.ZERO,
    val profitAmount: BigDecimal = BigDecimal.ZERO,
    val status: SaleStatus = SaleStatus.COMPLETED,
    val paymentStatus: PaymentStatus = PaymentStatus.PAID,
    val notes: String? = null,
    val saleDate: LocalDateTime,
    val items: List<SaleItem> = emptyList(),
    val payments: List<SalePayment> = emptyList(),
    val customer: Customer? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val itemCount: Int get() = items.size
    val hasDebt: Boolean get() = debtAmount > BigDecimal.ZERO
    val isPaid: Boolean get() = paymentStatus == PaymentStatus.PAID
    val isCompleted: Boolean get() = status == SaleStatus.COMPLETED
}

enum class SaleStatus {
    COMPLETED, PENDING, CANCELLED, REFUNDED, PARTIALLY_REFUNDED
}

enum class PaymentStatus {
    PAID, PARTIALLY_PAID, PENDING, DEBT
}

/**
 * Domain model representing an item in a sale.
 */
data class SaleItem(
    val id: String,
    val saleId: String,
    val productId: String? = null,
    val productName: String,
    val productSku: String? = null,
    val quantity: BigDecimal,
    val unitType: String? = null,
    val originalPrice: BigDecimal,
    val sellingPrice: BigDecimal,
    val costPrice: BigDecimal,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val discountPercentage: BigDecimal = BigDecimal.ZERO,
    val taxAmount: BigDecimal = BigDecimal.ZERO,
    val subtotal: BigDecimal,
    val total: BigDecimal,
    val profit: BigDecimal = BigDecimal.ZERO
)

/**
 * Domain model representing a payment for a sale.
 */
data class SalePayment(
    val id: String,
    val saleId: String,
    val userId: String,
    val paymentMethod: PaymentMethod,
    val amount: BigDecimal,
    val referenceNumber: String? = null,
    val notes: String? = null,
    val paymentDate: LocalDateTime
)

enum class PaymentMethod {
    CASH, MOBILE_MONEY, BANK_TRANSFER, CREDIT, CHEQUE
}

