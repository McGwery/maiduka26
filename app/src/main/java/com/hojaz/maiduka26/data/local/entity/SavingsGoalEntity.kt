package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * SavingsGoal entity representing the savings_goals table.
 * Stores shop savings goals for financial planning.
 */
@Entity(
    tableName = "savings_goals",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["shop_id", "status"])
    ]
)
data class SavingsGoalEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_id")
    val shopId: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "target_amount")
    val targetAmount: String,

    @ColumnInfo(name = "target_date")
    val targetDate: Long? = null,

    @ColumnInfo(name = "current_amount")
    val currentAmount: String = "0.00",

    @ColumnInfo(name = "amount_withdrawn")
    val amountWithdrawn: String = "0.00",

    @ColumnInfo(name = "progress_percentage")
    val progressPercentage: Int = 0,

    @ColumnInfo(name = "status")
    val status: String = "active", // active, completed, cancelled, paused

    @ColumnInfo(name = "completed_at")
    val completedAt: Long? = null,

    @ColumnInfo(name = "started_at")
    val startedAt: Long? = null,

    @ColumnInfo(name = "icon")
    val icon: String? = null,

    @ColumnInfo(name = "color")
    val color: String? = null,

    @ColumnInfo(name = "priority")
    val priority: Int = 0,

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

