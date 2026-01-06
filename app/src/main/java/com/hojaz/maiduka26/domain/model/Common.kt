package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a stock transfer.
 */
data class StockTransfer(
    val id: String,
    val purchaseOrderId: String,
    val productId: String,
    val quantity: Int,
    val transferredAt: LocalDateTime,
    val transferredBy: String,
    val notes: String? = null,
    val product: Product? = null,
    val createdAt: LocalDateTime? = null
)

/**
 * Domain model representing a shop supplier relationship.
 */
data class ShopSupplier(
    val id: Long,
    val shopId: String,
    val supplierId: String,
    val supplierShop: Shop? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

/**
 * Domain model representing an active shop selection.
 */
data class ActiveShop(
    val id: String,
    val userId: String,
    val shopId: String,
    val shop: Shop? = null,
    val selectedAt: LocalDateTime
)

/**
 * Domain model representing a blocked shop relationship.
 */
data class BlockedShop(
    val id: String,
    val shopId: String,
    val blockedShopId: String,
    val blockedBy: String,
    val reason: String? = null,
    val blockedShop: Shop? = null,
    val createdAt: LocalDateTime? = null
)

/**
 * Domain model representing a sale refund.
 */
data class SaleRefund(
    val id: String,
    val saleId: String,
    val userId: String,
    val amount: BigDecimal,
    val reason: String,
    val notes: String? = null,
    val refundDate: LocalDateTime,
    val createdAt: LocalDateTime? = null
)

/**
 * Domain model representing a dashboard summary.
 */
data class DashboardSummary(
    val todaySales: BigDecimal = BigDecimal.ZERO,
    val todayProfit: BigDecimal = BigDecimal.ZERO,
    val todaySalesCount: Int = 0,
    val todayExpenses: BigDecimal = BigDecimal.ZERO,
    val totalDebt: BigDecimal = BigDecimal.ZERO,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val customerCount: Int = 0,
    val productCount: Int = 0
) {
    val netProfit: BigDecimal get() = todayProfit.subtract(todayExpenses)
}

/**
 * Domain model representing a sales report.
 */
data class SalesReport(
    val periodStart: LocalDateTime,
    val periodEnd: LocalDateTime,
    val totalSales: BigDecimal = BigDecimal.ZERO,
    val totalProfit: BigDecimal = BigDecimal.ZERO,
    val totalRefunds: BigDecimal = BigDecimal.ZERO,
    val totalDiscounts: BigDecimal = BigDecimal.ZERO,
    val salesCount: Int = 0,
    val averageSaleValue: BigDecimal = BigDecimal.ZERO,
    val topProducts: List<ProductSalesSummary> = emptyList(),
    val paymentMethodBreakdown: Map<String, BigDecimal> = emptyMap()
)

data class ProductSalesSummary(
    val productId: String,
    val productName: String,
    val quantitySold: BigDecimal,
    val totalRevenue: BigDecimal,
    val totalProfit: BigDecimal
)

/**
 * Domain model representing an inventory report.
 */
data class InventoryReport(
    val totalProducts: Int = 0,
    val totalValue: BigDecimal = BigDecimal.ZERO,
    val lowStockProducts: List<Product> = emptyList(),
    val outOfStockProducts: List<Product> = emptyList(),
    val categoryBreakdown: Map<String, Int> = emptyMap()
)

