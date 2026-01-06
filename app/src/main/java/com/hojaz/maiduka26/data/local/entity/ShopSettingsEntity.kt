package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ShopSettings entity representing the shop_settings table.
 * Stores shop-specific configuration and preferences.
 */
@Entity(
    tableName = "shop_settings",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shop_id"], unique = true)
    ]
)
data class ShopSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "business_email")
    val businessEmail: String? = null,

    @ColumnInfo(name = "business_website")
    val businessWebsite: String? = null,

    @ColumnInfo(name = "tax_id")
    val taxId: String? = null,

    @ColumnInfo(name = "registration_number")
    val registrationNumber: String? = null,

    @ColumnInfo(name = "enable_sms_notifications")
    val enableSmsNotifications: Boolean = true,

    @ColumnInfo(name = "enable_email_notifications")
    val enableEmailNotifications: Boolean = false,

    @ColumnInfo(name = "notify_low_stock")
    val notifyLowStock: Boolean = true,

    @ColumnInfo(name = "low_stock_threshold")
    val lowStockThreshold: Int = 10,

    @ColumnInfo(name = "notify_daily_sales_summary")
    val notifyDailySalesSummary: Boolean = false,

    @ColumnInfo(name = "daily_summary_time")
    val dailySummaryTime: String = "18:00",

    @ColumnInfo(name = "auto_print_receipt")
    val autoPrintReceipt: Boolean = false,

    @ColumnInfo(name = "allow_credit_sales")
    val allowCreditSales: Boolean = true,

    @ColumnInfo(name = "credit_limit_days")
    val creditLimitDays: Int = 30,

    @ColumnInfo(name = "require_customer_for_credit")
    val requireCustomerForCredit: Boolean = true,

    @ColumnInfo(name = "allow_discounts")
    val allowDiscounts: Boolean = true,

    @ColumnInfo(name = "max_discount_percentage")
    val maxDiscountPercentage: String = "20.00",

    @ColumnInfo(name = "track_stock")
    val trackStock: Boolean = true,

    @ColumnInfo(name = "allow_negative_stock")
    val allowNegativeStock: Boolean = false,

    @ColumnInfo(name = "auto_deduct_stock_on_sale")
    val autoDeductStockOnSale: Boolean = true,

    @ColumnInfo(name = "stock_valuation_method")
    val stockValuationMethod: String = "fifo",

    @ColumnInfo(name = "receipt_header")
    val receiptHeader: String? = null,

    @ColumnInfo(name = "receipt_footer")
    val receiptFooter: String? = null,

    @ColumnInfo(name = "show_shop_logo_on_receipt")
    val showShopLogoOnReceipt: Boolean = true,

    @ColumnInfo(name = "show_tax_on_receipt")
    val showTaxOnReceipt: Boolean = false,

    @ColumnInfo(name = "tax_percentage")
    val taxPercentage: String = "0.00",

    @ColumnInfo(name = "opening_time")
    val openingTime: String = "08:00",

    @ColumnInfo(name = "closing_time")
    val closingTime: String = "20:00",

    @ColumnInfo(name = "working_days")
    val workingDays: String? = null, // JSON array of days

    @ColumnInfo(name = "language")
    val language: String = "sw",

    @ColumnInfo(name = "timezone")
    val timezone: String = "Africa/Dar_es_Salaam",

    @ColumnInfo(name = "date_format")
    val dateFormat: String = "d/m/Y",

    @ColumnInfo(name = "time_format")
    val timeFormat: String = "H:i",

    @ColumnInfo(name = "require_pin_for_refunds")
    val requirePinForRefunds: Boolean = true,

    @ColumnInfo(name = "require_pin_for_discounts")
    val requirePinForDiscounts: Boolean = false,

    @ColumnInfo(name = "enable_two_factor_auth")
    val enableTwoFactorAuth: Boolean = false,

    @ColumnInfo(name = "auto_backup")
    val autoBackup: Boolean = false,

    @ColumnInfo(name = "backup_frequency")
    val backupFrequency: String = "weekly",

    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null,

    @ColumnInfo(name = "deleted_at")
    val deletedAt: Long? = null,

    // Sync metadata
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "synced",

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)

