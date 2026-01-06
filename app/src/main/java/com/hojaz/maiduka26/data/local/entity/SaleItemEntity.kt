package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * SaleItem entity representing the sale_items table.
 * Stores individual items within a sale transaction.
 */
@Entity(
    tableName = "sale_items",
    foreignKeys = [
        ForeignKey(
            entity = SaleEntity::class,
            parentColumns = ["id"],
            childColumns = ["sale_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["id"],
            childColumns = ["product_id"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["sale_id"]),
        Index(value = ["product_id"])
    ]
)
data class SaleItemEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "sale_id")
    val saleId: String,

    @ColumnInfo(name = "product_id")
    val productId: String? = null,

    @ColumnInfo(name = "product_name")
    val productName: String,

    @ColumnInfo(name = "product_sku")
    val productSku: String? = null,

    @ColumnInfo(name = "quantity")
    val quantity: String, // Stored as String for decimal support

    @ColumnInfo(name = "unit_type")
    val unitType: String? = null,

    @ColumnInfo(name = "original_price")
    val originalPrice: String,

    @ColumnInfo(name = "selling_price")
    val sellingPrice: String,

    @ColumnInfo(name = "cost_price")
    val costPrice: String,

    @ColumnInfo(name = "discount_amount")
    val discountAmount: String = "0.00",

    @ColumnInfo(name = "discount_percentage")
    val discountPercentage: String = "0.00",

    @ColumnInfo(name = "tax_amount")
    val taxAmount: String = "0.00",

    @ColumnInfo(name = "subtotal")
    val subtotal: String,

    @ColumnInfo(name = "total")
    val total: String,

    @ColumnInfo(name = "profit")
    val profit: String = "0.00",

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

