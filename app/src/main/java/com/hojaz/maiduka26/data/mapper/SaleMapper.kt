package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.SaleEntity
import com.hojaz.maiduka26.data.local.entity.SaleItemEntity
import com.hojaz.maiduka26.data.local.entity.SalePaymentEntity
import com.hojaz.maiduka26.domain.model.*
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for Sale entity and domain model conversions.
 */
object SaleMapper {

    fun SaleEntity.toDomain(
        items: List<SaleItem> = emptyList(),
        payments: List<SalePayment> = emptyList(),
        customer: Customer? = null
    ): Sale {
        return Sale(
            id = id,
            shopId = shopId,
            customerId = customerId,
            userId = userId,
            saleNumber = saleNumber,
            subtotal = BigDecimal(subtotal),
            taxRate = BigDecimal(taxRate),
            taxAmount = BigDecimal(taxAmount),
            discountAmount = BigDecimal(discountAmount),
            discountPercentage = BigDecimal(discountPercentage),
            totalAmount = BigDecimal(totalAmount),
            amountPaid = BigDecimal(amountPaid),
            changeAmount = BigDecimal(changeAmount),
            debtAmount = BigDecimal(debtAmount),
            profitAmount = BigDecimal(profitAmount),
            status = SaleStatus.valueOf(status.uppercase()),
            paymentStatus = PaymentStatus.valueOf(paymentStatus.uppercase()),
            notes = notes,
            saleDate = DateTimeUtil.fromMillis(saleDate),
            items = items,
            payments = payments,
            customer = customer,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Sale.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): SaleEntity {
        return SaleEntity(
            id = id,
            shopId = shopId,
            customerId = customerId,
            userId = userId,
            saleNumber = saleNumber,
            subtotal = subtotal.toPlainString(),
            taxRate = taxRate.toPlainString(),
            taxAmount = taxAmount.toPlainString(),
            discountAmount = discountAmount.toPlainString(),
            discountPercentage = discountPercentage.toPlainString(),
            totalAmount = totalAmount.toPlainString(),
            amountPaid = amountPaid.toPlainString(),
            changeAmount = changeAmount.toPlainString(),
            debtAmount = debtAmount.toPlainString(),
            profitAmount = profitAmount.toPlainString(),
            status = status.name.lowercase(),
            paymentStatus = paymentStatus.name.lowercase(),
            notes = notes,
            saleDate = DateTimeUtil.toMillis(saleDate),
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun SaleItemEntity.toDomain(): SaleItem {
        return SaleItem(
            id = id,
            saleId = saleId,
            productId = productId,
            productName = productName,
            productSku = productSku,
            quantity = BigDecimal(quantity),
            unitType = unitType,
            originalPrice = BigDecimal(originalPrice),
            sellingPrice = BigDecimal(sellingPrice),
            costPrice = BigDecimal(costPrice),
            discountAmount = BigDecimal(discountAmount),
            discountPercentage = BigDecimal(discountPercentage),
            taxAmount = BigDecimal(taxAmount),
            subtotal = BigDecimal(subtotal),
            total = BigDecimal(total),
            profit = BigDecimal(profit)
        )
    }

    fun SaleItem.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): SaleItemEntity {
        return SaleItemEntity(
            id = id,
            saleId = saleId,
            productId = productId,
            productName = productName,
            productSku = productSku,
            quantity = quantity.toPlainString(),
            unitType = unitType,
            originalPrice = originalPrice.toPlainString(),
            sellingPrice = sellingPrice.toPlainString(),
            costPrice = costPrice.toPlainString(),
            discountAmount = discountAmount.toPlainString(),
            discountPercentage = discountPercentage.toPlainString(),
            taxAmount = taxAmount.toPlainString(),
            subtotal = subtotal.toPlainString(),
            total = total.toPlainString(),
            profit = profit.toPlainString(),
            createdAt = System.currentTimeMillis(),
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun SalePaymentEntity.toDomain(): SalePayment {
        return SalePayment(
            id = id,
            saleId = saleId,
            userId = userId,
            paymentMethod = PaymentMethod.valueOf(paymentMethod.uppercase()),
            amount = BigDecimal(amount),
            referenceNumber = referenceNumber,
            notes = notes,
            paymentDate = DateTimeUtil.fromMillis(paymentDate)
        )
    }

    fun SalePayment.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): SalePaymentEntity {
        return SalePaymentEntity(
            id = id,
            saleId = saleId,
            userId = userId,
            paymentMethod = paymentMethod.name.lowercase(),
            amount = amount.toPlainString(),
            referenceNumber = referenceNumber,
            notes = notes,
            paymentDate = DateTimeUtil.toMillis(paymentDate),
            createdAt = System.currentTimeMillis(),
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<SaleEntity>.toDomainList(): List<Sale> = map { it.toDomain() }
    fun List<SaleItemEntity>.toSaleItemDomainList(): List<SaleItem> = map { it.toDomain() }
    fun List<SalePaymentEntity>.toSalePaymentDomainList(): List<SalePayment> = map { it.toDomain() }
}

