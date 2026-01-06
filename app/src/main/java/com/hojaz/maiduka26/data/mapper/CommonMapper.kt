package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.*
import com.hojaz.maiduka26.domain.model.*
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for common/miscellaneous entity and domain model conversions.
 */
object CommonMapper {

    // StockAdjustment Mapper
    fun StockAdjustmentEntity.toDomain(): StockAdjustment {
        return StockAdjustment(
            id = id,
            productId = productId,
            userId = userId,
            type = StockAdjustmentType.valueOf(type.uppercase()),
            quantity = quantity,
            valueAtTime = BigDecimal(valueAtTime),
            previousStock = previousStock,
            newStock = newStock,
            reason = reason,
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun StockAdjustment.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): StockAdjustmentEntity {
        return StockAdjustmentEntity(
            id = id,
            productId = productId,
            userId = userId,
            type = type.name.lowercase(),
            quantity = quantity,
            valueAtTime = valueAtTime.toPlainString(),
            previousStock = previousStock,
            newStock = newStock,
            reason = reason,
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    // StockTransfer Mapper
    fun StockTransferEntity.toDomain(product: Product? = null): StockTransfer {
        return StockTransfer(
            id = id,
            purchaseOrderId = purchaseOrderId,
            productId = productId,
            quantity = quantity,
            transferredAt = DateTimeUtil.fromMillis(transferredAt),
            transferredBy = transferredBy,
            notes = notes,
            product = product,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun StockTransfer.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): StockTransferEntity {
        return StockTransferEntity(
            id = id,
            purchaseOrderId = purchaseOrderId,
            productId = productId,
            quantity = quantity,
            transferredAt = DateTimeUtil.toMillis(transferredAt),
            transferredBy = transferredBy,
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    // ShopSupplier Mapper
    fun ShopSupplierEntity.toDomain(supplierShop: Shop? = null): ShopSupplier {
        return ShopSupplier(
            id = id,
            shopId = shopId,
            supplierId = supplierId,
            supplierShop = supplierShop,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun ShopSupplier.toEntity(
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ShopSupplierEntity {
        return ShopSupplierEntity(
            id = id,
            shopId = shopId,
            supplierId = supplierId,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    // ActiveShop Mapper
    fun ActiveShopEntity.toDomain(shop: Shop? = null): ActiveShop {
        return ActiveShop(
            id = id,
            userId = userId,
            shopId = shopId,
            shop = shop,
            selectedAt = DateTimeUtil.fromMillis(selectedAt)
        )
    }

    fun ActiveShop.toEntity(
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ActiveShopEntity {
        return ActiveShopEntity(
            id = id,
            userId = userId,
            shopId = shopId,
            selectedAt = DateTimeUtil.toMillis(selectedAt),
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    // BlockedShop Mapper
    fun BlockedShopEntity.toDomain(blockedShop: Shop? = null): BlockedShop {
        return BlockedShop(
            id = id,
            shopId = shopId,
            blockedShopId = blockedShopId,
            blockedBy = blockedBy,
            reason = reason,
            blockedShop = blockedShop,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun BlockedShop.toEntity(
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): BlockedShopEntity {
        return BlockedShopEntity(
            id = id,
            shopId = shopId,
            blockedShopId = blockedShopId,
            blockedBy = blockedBy,
            reason = reason,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    // SaleRefund Mapper
    fun SaleRefundEntity.toDomain(): SaleRefund {
        return SaleRefund(
            id = id,
            saleId = saleId,
            userId = userId,
            amount = BigDecimal(amount),
            reason = reason,
            notes = notes,
            refundDate = DateTimeUtil.fromMillis(refundDate),
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun SaleRefund.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): SaleRefundEntity {
        return SaleRefundEntity(
            id = id,
            saleId = saleId,
            userId = userId,
            amount = amount.toPlainString(),
            reason = reason,
            notes = notes,
            refundDate = DateTimeUtil.toMillis(refundDate),
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    // List converters
    fun List<StockAdjustmentEntity>.toAdjustmentDomainList(): List<StockAdjustment> = map { it.toDomain() }
    fun List<StockTransferEntity>.toTransferDomainList(): List<StockTransfer> = map { it.toDomain() }
    fun List<ShopSupplierEntity>.toSupplierDomainList(): List<ShopSupplier> = map { it.toDomain() }
    fun List<BlockedShopEntity>.toBlockedShopDomainList(): List<BlockedShop> = map { it.toDomain() }
    fun List<SaleRefundEntity>.toRefundDomainList(): List<SaleRefund> = map { it.toDomain() }
}

