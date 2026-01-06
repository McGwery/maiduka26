package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.ProductEntity
import com.hojaz.maiduka26.domain.model.Product
import com.hojaz.maiduka26.domain.model.ProductType
import com.hojaz.maiduka26.domain.model.UnitType
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for Product entity and domain model conversions.
 */
object ProductMapper {

    fun ProductEntity.toDomain(): Product {
        return Product(
            id = id,
            shopId = shopId,
            categoryId = categoryId,
            productType = ProductType.valueOf(productType.uppercase()),
            serviceDuration = serviceDuration?.let { BigDecimal(it) },
            hourlyRate = hourlyRate?.let { BigDecimal(it) },
            productName = productName,
            description = description,
            sku = sku,
            barcode = barcode,
            purchaseQuantity = purchaseQuantity,
            totalAmountPaid = totalAmountPaid?.let { BigDecimal(it) },
            costPerUnit = costPerUnit?.let { BigDecimal(it) },
            unitType = UnitType.valueOf(unitType.uppercase()),
            breakDownCountPerUnit = breakDownCountPerUnit,
            smallItemName = smallItemName,
            sellWholeUnits = sellWholeUnits,
            pricePerUnit = pricePerUnit?.let { BigDecimal(it) },
            sellIndividualItems = sellIndividualItems,
            pricePerItem = pricePerItem?.let { BigDecimal(it) },
            sellInBundles = sellInBundles,
            currentStock = currentStock,
            lowStockThreshold = lowStockThreshold,
            trackInventory = trackInventory,
            imageUrl = imageUrl,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Product.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ProductEntity {
        return ProductEntity(
            id = id,
            shopId = shopId,
            categoryId = categoryId,
            productType = productType.name.lowercase(),
            serviceDuration = serviceDuration?.toPlainString(),
            hourlyRate = hourlyRate?.toPlainString(),
            productName = productName,
            description = description,
            sku = sku,
            barcode = barcode,
            purchaseQuantity = purchaseQuantity,
            totalAmountPaid = totalAmountPaid?.toPlainString(),
            costPerUnit = costPerUnit?.toPlainString(),
            unitType = unitType.name.lowercase(),
            breakDownCountPerUnit = breakDownCountPerUnit,
            smallItemName = smallItemName,
            sellWholeUnits = sellWholeUnits,
            pricePerUnit = pricePerUnit?.toPlainString(),
            sellIndividualItems = sellIndividualItems,
            pricePerItem = pricePerItem?.toPlainString(),
            sellInBundles = sellInBundles,
            currentStock = currentStock,
            lowStockThreshold = lowStockThreshold,
            trackInventory = trackInventory,
            imageUrl = imageUrl,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<ProductEntity>.toDomainList(): List<Product> = map { it.toDomain() }
}

