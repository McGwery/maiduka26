package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Conversation entity representing the conversations table.
 * Stores chat conversations between shops.
 */
@Entity(
    tableName = "conversations",
    foreignKeys = [
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_one_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["shop_two_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["last_message_by"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [
        Index(value = ["shop_one_id", "shop_two_id"], unique = true),
        Index(value = ["last_message_by"]),
        Index(value = ["shop_one_id", "is_active"]),
        Index(value = ["shop_two_id", "is_active"]),
        Index(value = ["last_message_at"])
    ]
)
data class ConversationEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "shop_one_id")
    val shopOneId: String,

    @ColumnInfo(name = "shop_two_id")
    val shopTwoId: String,

    @ColumnInfo(name = "last_message")
    val lastMessage: String? = null,

    @ColumnInfo(name = "last_message_by")
    val lastMessageBy: String? = null,

    @ColumnInfo(name = "last_message_at")
    val lastMessageAt: Long? = null,

    @ColumnInfo(name = "is_active")
    val isActive: Boolean = true,

    @ColumnInfo(name = "is_archived_by_shop_one")
    val isArchivedByShopOne: Boolean = false,

    @ColumnInfo(name = "is_archived_by_shop_two")
    val isArchivedByShopTwo: Boolean = false,

    @ColumnInfo(name = "metadata")
    val metadata: String? = null, // JSON string

    @ColumnInfo(name = "created_at")
    val createdAt: Long? = null,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long? = null,

    // Sync metadata
    @ColumnInfo(name = "sync_status")
    val syncStatus: String = "synced",

    @ColumnInfo(name = "last_synced_at")
    val lastSyncedAt: Long? = null
)

