package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.PurchaseOrderEntity
import com.hojaz.maiduka26.data.local.entity.PurchaseOrderItemEntity
import com.hojaz.maiduka26.data.local.entity.PurchasePaymentEntity
import com.hojaz.maiduka26.domain.model.PurchaseOrder
import com.hojaz.maiduka26.domain.model.PurchaseOrderItem
import com.hojaz.maiduka26.domain.model.PurchaseOrderStatus
import com.hojaz.maiduka26.domain.model.PurchasePayment
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for PurchaseOrder entity and domain model conversions.
 */
object PurchaseOrderMapper {

    fun PurchaseOrderEntity.toDomain(
        items: List<PurchaseOrderItem> = emptyList(),
        payments: List<PurchasePayment> = emptyList()
    ): PurchaseOrder {
        return PurchaseOrder(
            id = id,
            buyerShopId = buyerShopId,
            sellerShopId = sellerShopId,
            isInternal = isInternal,
            referenceNumber = referenceNumber,
            status = PurchaseOrderStatus.valueOf(status.uppercase()),
            totalAmount = BigDecimal(totalAmount),
            totalPaid = BigDecimal(totalPaid),
            notes = notes,
            approvedAt = approvedAt?.let { DateTimeUtil.fromMillis(it) },
            approvedBy = approvedBy,
            items = items,
            payments = payments,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun PurchaseOrder.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): PurchaseOrderEntity {
        return PurchaseOrderEntity(
            id = id,
            buyerShopId = buyerShopId,
            sellerShopId = sellerShopId,
            isInternal = isInternal,
            referenceNumber = referenceNumber,
            status = status.name.lowercase(),
            totalAmount = totalAmount.toPlainString(),
            totalPaid = totalPaid.toPlainString(),
            notes = notes,
            approvedAt = approvedAt?.let { DateTimeUtil.toMillis(it) },
            approvedBy = approvedBy,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun PurchaseOrderItemEntity.toDomain(): PurchaseOrderItem {
        return PurchaseOrderItem(
            id = id,
            purchaseOrderId = purchaseOrderId,
            productId = productId,
            quantity = quantity,
            unitPrice = BigDecimal(unitPrice),
            totalPrice = BigDecimal(totalPrice),
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun PurchaseOrderItem.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): PurchaseOrderItemEntity {
        return PurchaseOrderItemEntity(
            id = id,
            purchaseOrderId = purchaseOrderId,
            productId = productId,
            quantity = quantity,
            unitPrice = unitPrice.toPlainString(),
            totalPrice = totalPrice.toPlainString(),
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun PurchasePaymentEntity.toDomain(): PurchasePayment {
        return PurchasePayment(
            id = id,
            purchaseOrderId = purchaseOrderId,
            amount = BigDecimal(amount),
            paymentMethod = paymentMethod,
            referenceNumber = referenceNumber,
            notes = notes,
            recordedBy = recordedBy,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun PurchasePayment.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): PurchasePaymentEntity {
        return PurchasePaymentEntity(
            id = id,
            purchaseOrderId = purchaseOrderId,
            amount = amount.toPlainString(),
            paymentMethod = paymentMethod,
            referenceNumber = referenceNumber,
            notes = notes,
            recordedBy = recordedBy,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<PurchaseOrderEntity>.toDomainList(): List<PurchaseOrder> = map { it.toDomain() }
    fun List<PurchaseOrderItemEntity>.toItemDomainList(): List<PurchaseOrderItem> = map { it.toDomain() }
    fun List<PurchasePaymentEntity>.toPaymentDomainList(): List<PurchasePayment> = map { it.toDomain() }
}

