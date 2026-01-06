package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

/**
 * Product entity representing the products table.
 * Stores product information with inventory tracking support.
 */
@Entity(
    tableName = "products",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["category_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shop_id", "category_id"]),
        Index(value = ["product_name"]),
        Index(value = ["sku"], unique = true),
        Index(value = ["barcode"], unique = true),
        Index(value = ["current_stock", "low_stock_threshold"])
    ]
)
data class ProductEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "category_id")
    val categoryId: String,

    @ColumnInfo(name = "product_type")
    val productType: String = "physical", // physical, service, digital

    @ColumnInfo(name = "service_duration")
    val serviceDuration: String? = null, // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "hourly_rate")
    val hourlyRate: String? = null, // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "product_name")
    val productName: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "sku")
    val sku: String? = null,

    @ColumnInfo(name = "barcode")
    val barcode: String? = null,

    @ColumnInfo(name = "purchase_quantity")
    val purchaseQuantity: Int? = null,

    @ColumnInfo(name = "total_amount_paid")
    val totalAmountPaid: String? = null, // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "cost_per_unit")
    val costPerUnit: String? = null, // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "unit_type")
    val unitType: String, // box, carton, piece, pack, etc.

    @ColumnInfo(name = "break_down_count_per_unit")
    val breakDownCountPerUnit: Int? = null,

    @ColumnInfo(name = "small_item_name")
    val smallItemName: String? = null,

    @ColumnInfo(name = "sell_whole_units")
    val sellWholeUnits: Boolean = true,

    @ColumnInfo(name = "price_per_unit")
    val pricePerUnit: String? = null, // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "sell_individual_items")
    val sellIndividualItems: Boolean = false,

    @ColumnInfo(name = "price_per_item")
    val pricePerItem: String? = null, // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "sell_in_bundles")
    val sellInBundles: Boolean = false,

    @ColumnInfo(name = "current_stock")
    val currentStock: Int? = null,

    @ColumnInfo(name = "low_stock_threshold")
    val lowStockThreshold: Int? = null,

    @ColumnInfo(name = "track_inventory")
    val trackInventory: Boolean = true,

    @ColumnInfo(name = "image_url")
    val imageUrl: String? = null,

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

