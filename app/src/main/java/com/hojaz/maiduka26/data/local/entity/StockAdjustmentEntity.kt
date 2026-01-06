package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * StockAdjustment entity representing the stock_adjustments table.
 * Tracks inventory adjustments (damaged, expired, theft, etc.).
 */
@Entity(
    tableName = "stock_adjustments",
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
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
        Index(value = ["product_id", "type"]),
        Index(value = ["user_id"]),
        Index(value = ["type"]),
        Index(value = ["created_at"])
    ]
)
data class StockAdjustmentEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "product_id")
    val productId: String,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "type")
    val type: String, // damaged, expired, lost, theft, personal_use, donation, return_to_supplier, other, restock, adjustment

    @ColumnInfo(name = "quantity")
    val quantity: Int,

    @ColumnInfo(name = "value_at_time")
    val valueAtTime: String,

    @ColumnInfo(name = "previous_stock")
    val previousStock: Int,

    @ColumnInfo(name = "new_stock")
    val newStock: Int,

    @ColumnInfo(name = "reason")
    val reason: String,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

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

