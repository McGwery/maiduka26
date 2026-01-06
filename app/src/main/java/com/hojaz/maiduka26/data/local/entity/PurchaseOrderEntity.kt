package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * PurchaseOrder entity representing the purchase_orders table.
 * Stores purchase orders between shops.
 */
@Entity(
    tableName = "purchase_orders",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["buyer_shop_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["seller_shop_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["approved_by"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["reference_number"], unique = true),
        Index(value = ["buyer_shop_id"]),
        Index(value = ["seller_shop_id"]),
        Index(value = ["approved_by"])
    ]
)
data class PurchaseOrderEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "buyer_shop_id")
    val buyerShopId: String,

    @ColumnInfo(name = "seller_shop_id")
    val sellerShopId: String,

    @ColumnInfo(name = "is_internal")
    val isInternal: Boolean = false,

    @ColumnInfo(name = "reference_number")
    val referenceNumber: String,

    @ColumnInfo(name = "status")
    val status: String = "pending", // pending, approved, rejected, completed, cancelled

    @ColumnInfo(name = "total_amount")
    val totalAmount: String = "0.00",

    @ColumnInfo(name = "total_paid")
    val totalPaid: String = "0.00",

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "approved_at")
    val approvedAt: Long? = null,

    @ColumnInfo(name = "approved_by")
    val approvedBy: String? = null,

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

