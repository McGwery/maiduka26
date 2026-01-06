package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * SalePayment entity representing the sale_payments table.
 * Stores payment records for sales (supports multiple payments per sale).
 */
@Entity(
    tableName = "sale_payments",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["user_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["sale_id"]),
        Index(value = ["user_id"]),
        Index(value = ["payment_method"]),
        Index(value = ["payment_date"])
    ]
)
data class SalePaymentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "sale_id")
    val saleId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: String, // cash, mobile_money, bank_transfer, credit, cheque

    @ColumnInfo(name = "amount")
    val amount: String,

    @ColumnInfo(name = "reference_number")
    val referenceNumber: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "payment_date")
    val paymentDate: Long,

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

