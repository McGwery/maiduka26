package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.ConversationEntity
import com.hojaz.maiduka26.data.local.entity.MessageEntity
import com.hojaz.maiduka26.data.local.entity.MessageReactionEntity
import com.hojaz.maiduka26.data.local.entity.TypingIndicatorEntity
import com.hojaz.maiduka26.domain.model.*
import com.hojaz.maiduka26.util.DateTimeUtil

/**
 * Mapper for Conversation and Message entity and domain model conversions.
 */
object ConversationMapper {

    fun ConversationEntity.toDomain(
        unreadCount: Int = 0,
        otherShop: Shop? = null
    ): Conversation {
        return Conversation(
            id = id,
            shopOneId = shopOneId,
            shopTwoId = shopTwoId,
            lastMessage = lastMessage,
            lastMessageBy = lastMessageBy,
            lastMessageAt = lastMessageAt?.let { DateTimeUtil.fromMillis(it) },
            isActive = isActive,
            isArchivedByShopOne = isArchivedByShopOne,
            isArchivedByShopTwo = isArchivedByShopTwo,
            unreadCount = unreadCount,
            otherShop = otherShop,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Conversation.toEntity(
        metadata: String? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ConversationEntity {
        return ConversationEntity(
            id = id,
            shopOneId = shopOneId,
            shopTwoId = shopTwoId,
            lastMessage = lastMessage,
            lastMessageBy = lastMessageBy,
            lastMessageAt = lastMessageAt?.let { DateTimeUtil.toMillis(it) },
            isActive = isActive,
            isArchivedByShopOne = isArchivedByShopOne,
            isArchivedByShopTwo = isArchivedByShopTwo,
            metadata = metadata,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun MessageEntity.toDomain(
        product: Product? = null,
        replyToMessage: Message? = null,
        reactions: List<MessageReaction> = emptyList()
    ): Message {
        return Message(
            id = id,
            conversationId = conversationId,
            senderShopId = senderShopId,
            senderUserId = senderUserId,
            receiverShopId = receiverShopId,
            message = message,
            messageType = MessageType.valueOf(messageType.uppercase()),
            attachments = attachments?.split(",")?.filter { it.isNotEmpty() } ?: emptyList(),
            productId = productId,
            product = product,
            locationLat = locationLat?.toDoubleOrNull(),
            locationLng = locationLng?.toDoubleOrNull(),
            locationName = locationName,
            isRead = isRead,
            readAt = readAt?.let { DateTimeUtil.fromMillis(it) },
            isDelivered = isDelivered,
            deliveredAt = deliveredAt?.let { DateTimeUtil.fromMillis(it) },
            replyToMessageId = replyToMessageId,
            replyToMessage = replyToMessage,
            reactions = reactions,
            isDeletedBySender = isDeletedBySender,
            isDeletedByReceiver = isDeletedByReceiver,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Message.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): MessageEntity {
        return MessageEntity(
            id = id,
            conversationId = conversationId,
            senderShopId = senderShopId,
            senderUserId = senderUserId,
            receiverShopId = receiverShopId,
            message = message,
            messageType = messageType.name.lowercase(),
            attachments = attachments.joinToString(","),
            productId = productId,
            locationLat = locationLat?.toString(),
            locationLng = locationLng?.toString(),
            locationName = locationName,
            isRead = isRead,
            readAt = readAt?.let { DateTimeUtil.toMillis(it) },
            isDelivered = isDelivered,
            deliveredAt = deliveredAt?.let { DateTimeUtil.toMillis(it) },
            replyToMessageId = replyToMessageId,
            isDeletedBySender = isDeletedBySender,
            isDeletedByReceiver = isDeletedByReceiver,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun MessageReactionEntity.toDomain(): MessageReaction {
        return MessageReaction(
            id = id,
            messageId = messageId,
            userId = userId,
            reaction = reaction,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun MessageReaction.toEntity(
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): MessageReactionEntity {
        return MessageReactionEntity(
            id = id,
            messageId = messageId,
            userId = userId,
            reaction = reaction,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun TypingIndicatorEntity.toDomain(userName: String? = null): TypingIndicator {
        return TypingIndicator(
            id = id,
            conversationId = conversationId,
            shopId = shopId,
            userId = userId,
            userName = userName,
            startedAt = DateTimeUtil.fromMillis(startedAt),
            expiresAt = expiresAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun TypingIndicator.toEntity(
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): TypingIndicatorEntity {
        return TypingIndicatorEntity(
            id = id,
            conversationId = conversationId,
            shopId = shopId,
            userId = userId,
            startedAt = DateTimeUtil.toMillis(startedAt),
            expiresAt = expiresAt?.let { DateTimeUtil.toMillis(it) },
            createdAt = System.currentTimeMillis(),
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<ConversationEntity>.toDomainList(): List<Conversation> = map { it.toDomain() }
    fun List<MessageEntity>.toMessageDomainList(): List<Message> = map { it.toDomain() }
    fun List<MessageReactionEntity>.toReactionDomainList(): List<MessageReaction> = map { it.toDomain() }
}

