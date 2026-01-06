package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Subscription entity representing the subscriptions table.
 * Stores shop subscription/plan information.
 */
@Entity(
    tableName = "subscriptions",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shop_id", "status"]),
        Index(value = ["expires_at"])
    ]
)
data class SubscriptionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "plan")
    val plan: String = "free", // free, basic, premium, enterprise

    @ColumnInfo(name = "type")
    val type: String = "offline", // offline, online

    @ColumnInfo(name = "status")
    val status: String = "pending", // pending, active, expired, cancelled

    @ColumnInfo(name = "price")
    val price: String = "0.00",

    @ColumnInfo(name = "currency")
    val currency: String = "TZS",

    @ColumnInfo(name = "starts_at")
    val startsAt: Long? = null,

    @ColumnInfo(name = "expires_at")
    val expiresAt: Long? = null,

    @ColumnInfo(name = "auto_renew")
    val autoRenew: Boolean = false,

    @ColumnInfo(name = "payment_method")
    val paymentMethod: String? = null,

    @ColumnInfo(name = "transaction_reference")
    val transactionReference: String? = null,

    @ColumnInfo(name = "features")
    val features: String? = null, // JSON array of features

    @ColumnInfo(name = "max_users")
    val maxUsers: Int? = null,

    @ColumnInfo(name = "max_products")
    val maxProducts: Int? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "cancelled_at")
    val cancelledAt: Long? = null,

    @ColumnInfo(name = "cancelled_reason")
    val cancelledReason: String? = null,

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

