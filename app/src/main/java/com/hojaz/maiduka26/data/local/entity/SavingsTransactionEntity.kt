package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * SavingsTransaction entity representing the savings_transactions table.
 * Stores deposit and withdrawal transactions for savings.
 */
@Entity(
    tableName = "savings_transactions",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = SavingsGoalEntity::class,
            parentColumns = ["id"],
            childColumns = ["savings_goal_id"],
            onDelete = ForeignKey.SET_NULL
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["processed_by"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["savings_goal_id"]),
        Index(value = ["processed_by"]),
        Index(value = ["shop_id", "type"]),
        Index(value = ["shop_id", "transaction_date"])
    ]
)
data class SavingsTransactionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "savings_goal_id")
    val savingsGoalId: String? = null,

    @ColumnInfo(name = "type")
    val type: String, // deposit, withdrawal

    @ColumnInfo(name = "amount")
    val amount: String,

    @ColumnInfo(name = "balance_before")
    val balanceBefore: String,

    @ColumnInfo(name = "balance_after")
    val balanceAfter: String,

    @ColumnInfo(name = "transaction_date")
    val transactionDate: Long,

    @ColumnInfo(name = "daily_profit")
    val dailyProfit: String? = null,

    @ColumnInfo(name = "is_automatic")
    val isAutomatic: Boolean = true,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "notes")
    val notes: String? = null,

    @ColumnInfo(name = "processed_by")
    val processedBy: String? = null,

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

