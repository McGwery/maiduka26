package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Sale entity representing the sales table.
 * Stores completed and pending sale transactions.
 */
@Entity(
    tableName = "sales",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CustomerEntity::class,
            parentColumns = ["id"],
            childColumns = ["customer_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sale_number"], unique = true),
        Index(value = ["shop_id", "sale_date"]),
        Index(value = ["shop_id", "status"]),
        Index(value = ["shop_id", "customer_id"]),
        Index(value = ["shop_id", "payment_status"]),
        Index(value = ["customer_id"]),
        Index(value = ["user_id"])
    ]
)
data class SaleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "customer_id")
    val customerId: String? = null,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "sale_number")
    val saleNumber: String,

    @ColumnInfo(name = "subtotal")
    val subtotal: String, // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "tax_rate")
    val taxRate: String = "0.00",

    @ColumnInfo(name = "tax_amount")
    val taxAmount: String = "0.00",

    @ColumnInfo(name = "discount_amount")
    val discountAmount: String = "0.00",

    @ColumnInfo(name = "discount_percentage")
    val discountPercentage: String = "0.00",

    @ColumnInfo(name = "total_amount")
    val totalAmount: String,

    @ColumnInfo(name = "amount_paid")
    val amountPaid: String = "0.00",

    @ColumnInfo(name = "change_amount")
    val changeAmount: String = "0.00",

    @ColumnInfo(name = "debt_amount")
    val debtAmount: String = "0.00",

    @ColumnInfo(name = "profit_amount")
    val profitAmount: String = "0.00",

    @ColumnInfo(name = "status")
    val status: String = "completed", // completed, pending, cancelled, refunded, partially_refunded

    @ColumnInfo(name = "payment_status")
    val paymentStatus: String = "paid", // paid, partially_paid, pending, debt

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "sale_date")
    val saleDate: Long,

    @ColumnInfo(name = "converted_to_expense_at")
    val convertedToExpenseAt: Long? = null,

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

