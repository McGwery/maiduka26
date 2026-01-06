package com.hojaz.maiduka26.domain.model

import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Domain model representing a shop member (staff/employee).
 */
data class ShopMember(
    val id: String,
    val shopId: String,
    val userId: String,
    val role: MemberRole,
    val permissions: List<Permission> = emptyList(),
    val isActive: Boolean = true,
    val user: User? = null,
    val shop: Shop? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    fun hasPermission(permission: Permission): Boolean {
        // Owners have all permissions
        if (role == MemberRole.OWNER) return true
        return permissions.contains(permission)
    }
}

enum class MemberRole {
    OWNER, MANAGER, CASHIER, STAFF
}

enum class Permission {
    // Sales
    CREATE_SALE,
    VIEW_SALES,
    EDIT_SALE,
    DELETE_SALE,
    REFUND_SALE,
    APPLY_DISCOUNT,

    // Products
    CREATE_PRODUCT,
    VIEW_PRODUCTS,
    EDIT_PRODUCT,
    DELETE_PRODUCT,
    MANAGE_STOCK,

    // Customers
    CREATE_CUSTOMER,
    VIEW_CUSTOMERS,
    EDIT_CUSTOMER,
    DELETE_CUSTOMER,
    MANAGE_DEBT,

    // Expenses
    CREATE_EXPENSE,
    VIEW_EXPENSES,
    EDIT_EXPENSE,
    DELETE_EXPENSE,

    // Reports
    VIEW_REPORTS,
    VIEW_PROFIT,
    EXPORT_REPORTS,

    // Settings
    MANAGE_SETTINGS,
    MANAGE_MEMBERS,
    MANAGE_SHOP
}

/**
 * Domain model representing shop settings.
 */
data class ShopSettings(
    val id: String,
    val shopId: String,
    val businessEmail: String? = null,
    val businessWebsite: String? = null,
    val taxId: String? = null,
    val registrationNumber: String? = null,

    // Notifications
    val enableSmsNotifications: Boolean = true,
    val enableEmailNotifications: Boolean = false,
    val notifyLowStock: Boolean = true,
    val lowStockThreshold: Int = 10,
    val notifyDailySalesSummary: Boolean = false,
    val dailySummaryTime: String = "18:00",

    // Sales
    val autoPrintReceipt: Boolean = false,
    val allowCreditSales: Boolean = true,
    val creditLimitDays: Int = 30,
    val requireCustomerForCredit: Boolean = true,
    val allowDiscounts: Boolean = true,
    val maxDiscountPercentage: BigDecimal = BigDecimal("20.00"),

    // Inventory
    val trackStock: Boolean = true,
    val allowNegativeStock: Boolean = false,
    val autoDeductStockOnSale: Boolean = true,
    val stockValuationMethod: StockValuationMethod = StockValuationMethod.FIFO,

    // Receipt
    val receiptHeader: String? = null,
    val receiptFooter: String? = null,
    val showShopLogoOnReceipt: Boolean = true,
    val showTaxOnReceipt: Boolean = false,
    val taxPercentage: BigDecimal = BigDecimal.ZERO,

    // Business Hours
    val openingTime: String = "08:00",
    val closingTime: String = "20:00",
    val workingDays: List<Int> = listOf(1, 2, 3, 4, 5, 6), // Monday to Saturday

    // Localization
    val language: String = "sw",
    val timezone: String = "Africa/Dar_es_Salaam",
    val dateFormat: String = "d/m/Y",
    val timeFormat: String = "H:i",

    // Security
    val requirePinForRefunds: Boolean = true,
    val requirePinForDiscounts: Boolean = false,
    val enableTwoFactorAuth: Boolean = false,

    // Backup
    val autoBackup: Boolean = false,
    val backupFrequency: BackupFrequency = BackupFrequency.WEEKLY,

    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

enum class StockValuationMethod {
    FIFO, LIFO, WEIGHTED_AVERAGE
}

enum class BackupFrequency {
    DAILY, WEEKLY, MONTHLY
}

/**
 * Domain model representing a subscription.
 */
data class Subscription(
    val id: String,
    val shopId: String,
    val plan: SubscriptionPlan = SubscriptionPlan.FREE,
    val type: SubscriptionType = SubscriptionType.OFFLINE,
    val status: SubscriptionStatus = SubscriptionStatus.PENDING,
    val price: BigDecimal = BigDecimal.ZERO,
    val currency: String = "TZS",
    val startsAt: LocalDateTime? = null,
    val expiresAt: LocalDateTime? = null,
    val autoRenew: Boolean = false,
    val paymentMethod: String? = null,
    val transactionReference: String? = null,
    val features: List<String> = emptyList(),
    val maxUsers: Int? = null,
    val maxProducts: Int? = null,
    val notes: String? = null,
    val cancelledAt: LocalDateTime? = null,
    val cancelledReason: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val isActive: Boolean get() = status == SubscriptionStatus.ACTIVE
    val isExpired: Boolean get() = status == SubscriptionStatus.EXPIRED

    fun daysRemaining(): Long {
        val now = LocalDateTime.now()
        return expiresAt?.let {
            java.time.temporal.ChronoUnit.DAYS.between(now, it)
        } ?: 0
    }
}

enum class SubscriptionPlan {
    FREE, BASIC, PREMIUM, ENTERPRISE
}

enum class SubscriptionType {
    OFFLINE, ONLINE
}

enum class SubscriptionStatus {
    PENDING, ACTIVE, EXPIRED, CANCELLED
}

