package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Customer entity representing the customers table.
 * Stores customer information for debt tracking and loyalty.
 */
@Entity(
    tableName = "customers",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shop_id", "name"]),
        Index(value = ["phone"]),
        Index(value = ["current_debt"])
    ]
)
data class CustomerEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "phone")
    val phone: String? = null,

    @ColumnInfo(name = "email")
    val email: String? = null,

    @ColumnInfo(name = "address")
    val address: String? = null,

    @ColumnInfo(name = "credit_limit")
    val creditLimit: String = "0.00", // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "current_debt")
    val currentDebt: String = "0.00", // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "total_purchases")
    val totalPurchases: String = "0.00", // Stored as String, parsed as BigDecimal

    @ColumnInfo(name = "total_paid")
    val totalPaid: String = "0.00", // Stored as String, parsed as BigDecimal

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

