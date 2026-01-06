package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a product.
 */
data class Product(
    val id: String,
    val shopId: String,
    val categoryId: String,
    val productType: ProductType = ProductType.PHYSICAL,
    val serviceDuration: BigDecimal? = null,
    val hourlyRate: BigDecimal? = null,
    val productName: String,
    val description: String? = null,
    val sku: String? = null,
    val barcode: String? = null,
    val purchaseQuantity: Int? = null,
    val totalAmountPaid: BigDecimal? = null,
    val costPerUnit: BigDecimal? = null,
    val unitType: UnitType,
    val breakDownCountPerUnit: Int? = null,
    val smallItemName: String? = null,
    val sellWholeUnits: Boolean = true,
    val pricePerUnit: BigDecimal? = null,
    val sellIndividualItems: Boolean = false,
    val pricePerItem: BigDecimal? = null,
    val sellInBundles: Boolean = false,
    val currentStock: Int? = null,
    val lowStockThreshold: Int? = null,
    val trackInventory: Boolean = true,
    val imageUrl: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val isLowStock: Boolean
        get() = trackInventory && currentStock != null && lowStockThreshold != null && currentStock <= lowStockThreshold

    val isOutOfStock: Boolean
        get() = trackInventory && currentStock != null && currentStock <= 0

    val displayPrice: BigDecimal
        get() = pricePerUnit ?: pricePerItem ?: BigDecimal.ZERO

    val profitMargin: BigDecimal
        get() {
            val cost = costPerUnit ?: return BigDecimal.ZERO
            val price = displayPrice
            if (price.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO
            return price.subtract(cost).multiply(BigDecimal(100)).divide(price, 2, java.math.RoundingMode.HALF_UP)
        }
}

enum class ProductType {
    PHYSICAL, SERVICE, DIGITAL
}

enum class UnitType {
    BOX, CARTON, PIECE, PACK, BOTTLE, BAG, SACHET, KG, GRAM, LITER, ML
}

