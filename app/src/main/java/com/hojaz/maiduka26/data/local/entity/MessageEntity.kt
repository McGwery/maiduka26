package com.hojaz.maiduka26.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Message entity representing the messages table.
 * Stores chat messages between shops.
 */
@Entity(
    tableName = "messages",
    foreignKeys = [
        ForeignKey(
            entity = ConversationEntity::class,
            parentColumns = ["id"],
            childColumns = ["conversation_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["sender_shop_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["id"],
            childColumns = ["sender_user_id"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ShopEntity::class,
            parentColumns = ["id"],
            childColumns = ["receiver_shop_id"],
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
        Index(value = ["conversation_id", "created_at"]),
        Index(value = ["receiver_shop_id", "is_read"]),
        Index(value = ["sender_shop_id"]),
        Index(value = ["sender_user_id"]),
        Index(value = ["product_id"]),
        Index(value = ["reply_to_message_id"])
    ]
)
data class MessageEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "conversation_id")
    val conversationId: String,

    @ColumnInfo(name = "sender_shop_id")
    val senderShopId: String,

    @ColumnInfo(name = "sender_user_id")
    val senderUserId: String,

    @ColumnInfo(name = "receiver_shop_id")
    val receiverShopId: String,

    @ColumnInfo(name = "message")
    val message: String? = null,

    @ColumnInfo(name = "message_type")
    val messageType: String = "text", // text, image, video, audio, document, product, location

    @ColumnInfo(name = "attachments")
    val attachments: String? = null, // JSON array

    @ColumnInfo(name = "product_id")
    val productId: String? = null,

    @ColumnInfo(name = "location_lat")
    val locationLat: String? = null,

    @ColumnInfo(name = "location_lng")
    val locationLng: String? = null,

    @ColumnInfo(name = "location_name")
    val locationName: String? = null,

    @ColumnInfo(name = "is_read")
    val isRead: Boolean = false,

    @ColumnInfo(name = "read_at")
    val readAt: Long? = null,

    @ColumnInfo(name = "is_delivered")
    val isDelivered: Boolean = false,

    @ColumnInfo(name = "delivered_at")
    val deliveredAt: Long? = null,

    @ColumnInfo(name = "reply_to_message_id")
    val replyToMessageId: String? = null,

    @ColumnInfo(name = "is_deleted_by_sender")
    val isDeletedBySender: Boolean = false,

    @ColumnInfo(name = "is_deleted_by_receiver")
    val isDeletedByReceiver: Boolean = false,

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

