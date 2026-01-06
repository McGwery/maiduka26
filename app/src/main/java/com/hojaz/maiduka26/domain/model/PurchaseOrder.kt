package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a purchase order between shops.
 */
data class PurchaseOrder(
    val id: String,
    val buyerShopId: String,
    val sellerShopId: String,
    val isInternal: Boolean = false,
    val referenceNumber: String,
    val status: PurchaseOrderStatus = PurchaseOrderStatus.PENDING,
    val totalAmount: BigDecimal = BigDecimal.ZERO,
    val totalPaid: BigDecimal = BigDecimal.ZERO,
    val notes: String? = null,
    val approvedAt: LocalDateTime? = null,
    val approvedBy: String? = null,
    val items: List<PurchaseOrderItem> = emptyList(),
    val payments: List<PurchasePayment> = emptyList(),
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val outstandingAmount: BigDecimal get() = totalAmount.subtract(totalPaid)
    val isPaid: Boolean get() = totalPaid >= totalAmount
    val itemCount: Int get() = items.size
}

enum class PurchaseOrderStatus {
    PENDING, APPROVED, REJECTED, COMPLETED, CANCELLED
}

/**
 * Domain model representing an item in a purchase order.
 */
data class PurchaseOrderItem(
    val id: String,
    val purchaseOrderId: String,
    val productId: String,
    val quantity: Int,
    val unitPrice: BigDecimal,
    val totalPrice: BigDecimal,
    val notes: String? = null,
    val createdAt: LocalDateTime? = null
)

/**
 * Domain model representing a payment for a purchase order.
 */
data class PurchasePayment(
    val id: String,
    val purchaseOrderId: String,
    val amount: BigDecimal,
    val paymentMethod: String,
    val referenceNumber: String? = null,
    val notes: String? = null,
    val recordedBy: String,
    val createdAt: LocalDateTime? = null
)

