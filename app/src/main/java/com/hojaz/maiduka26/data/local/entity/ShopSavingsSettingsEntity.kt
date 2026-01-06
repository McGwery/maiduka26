package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * ShopSavingsSettings entity representing the shop_savings_settings table.
 * Stores shop-specific savings configuration.
 */
@Entity(
    tableName = "shop_savings_settings",
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
data class ShopSavingsSettingsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "is_enabled")
    val isEnabled: Boolean = false,

    @ColumnInfo(name = "savings_type")
    val savingsType: String = "percentage", // percentage, fixed_amount

    @ColumnInfo(name = "savings_percentage")
    val savingsPercentage: String? = null,

    @ColumnInfo(name = "fixed_amount")
    val fixedAmount: String? = null,

    @ColumnInfo(name = "target_amount")
    val targetAmount: String? = null,

    @ColumnInfo(name = "target_date")
    val targetDate: Long? = null,

    @ColumnInfo(name = "withdrawal_frequency")
    val withdrawalFrequency: String = "monthly", // none, weekly, bi_weekly, monthly, quarterly, when_goal_reached

    @ColumnInfo(name = "auto_withdraw")
    val autoWithdraw: Boolean = false,

    @ColumnInfo(name = "minimum_withdrawal_amount")
    val minimumWithdrawalAmount: String? = null,

    @ColumnInfo(name = "current_balance")
    val currentBalance: String = "0.00",

    @ColumnInfo(name = "total_saved")
    val totalSaved: String = "0.00",

    @ColumnInfo(name = "total_withdrawn")
    val totalWithdrawn: String = "0.00",

    @ColumnInfo(name = "last_savings_date")
    val lastSavingsDate: Long? = null,

    @ColumnInfo(name = "last_withdrawal_date")
    val lastWithdrawalDate: Long? = null,

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

