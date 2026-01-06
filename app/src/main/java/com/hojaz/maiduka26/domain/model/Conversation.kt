package com.hojaz.maiduka26.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a conversation between shops.
 */
data class Conversation(
    val id: String,
    val shopOneId: String,
    val shopTwoId: String,
    val lastMessage: String? = null,
    val lastMessageBy: String? = null,
    val lastMessageAt: LocalDateTime? = null,
    val isActive: Boolean = true,
    val isArchivedByShopOne: Boolean = false,
    val isArchivedByShopTwo: Boolean = false,
    val unreadCount: Int = 0,
    val otherShop: Shop? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    fun isArchivedBy(shopId: String): Boolean {
        return when (shopId) {
            shopOneId -> isArchivedByShopOne
            shopTwoId -> isArchivedByShopTwo
            else -> false
        }
    }

    fun getOtherShopId(currentShopId: String): String {
        return if (currentShopId == shopOneId) shopTwoId else shopOneId
    }
}

/**
 * Domain model representing a message in a conversation.
 */
data class Message(
    val id: String,
    val conversationId: String,
    val senderShopId: String,
    val senderUserId: String,
    val receiverShopId: String,
    val message: String? = null,
    val messageType: MessageType = MessageType.TEXT,
    val attachments: List<String> = emptyList(),
    val productId: String? = null,
    val product: Product? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationName: String? = null,
    val isRead: Boolean = false,
    val readAt: LocalDateTime? = null,
    val isDelivered: Boolean = false,
    val deliveredAt: LocalDateTime? = null,
    val replyToMessageId: String? = null,
    val replyToMessage: Message? = null,
    val reactions: List<MessageReaction> = emptyList(),
    val isDeletedBySender: Boolean = false,
    val isDeletedByReceiver: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val isDeleted: Boolean get() = isDeletedBySender && isDeletedByReceiver
    val hasAttachments: Boolean get() = attachments.isNotEmpty()
    val isProductMessage: Boolean get() = messageType == MessageType.PRODUCT && productId != null
    val isLocationMessage: Boolean get() = messageType == MessageType.LOCATION && locationLat != null
}

enum class MessageType {
    TEXT, IMAGE, VIDEO, AUDIO, DOCUMENT, PRODUCT, LOCATION
}

/**
 * Domain model representing a reaction to a message.
 */
data class MessageReaction(
    val id: String,
    val messageId: String,
    val userId: String,
    val reaction: String,
    val createdAt: LocalDateTime? = null
)

/**
 * Domain model representing a typing indicator.
 */
data class TypingIndicator(
    val id: String,
    val conversationId: String,
    val shopId: String,
    val userId: String,
    val userName: String? = null,
    val startedAt: LocalDateTime,
    val expiresAt: LocalDateTime? = null
) {
    fun isExpired(): Boolean {
        return expiresAt?.isBefore(LocalDateTime.now()) ?: false
    }
}

