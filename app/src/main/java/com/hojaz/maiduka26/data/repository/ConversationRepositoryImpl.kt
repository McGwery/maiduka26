package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.ConversationDao
import com.hojaz.maiduka26.data.local.dao.MessageDao
import com.hojaz.maiduka26.data.local.dao.MessageReactionDao
import com.hojaz.maiduka26.data.local.entity.ConversationEntity
import com.hojaz.maiduka26.data.local.entity.MessageReactionEntity
import com.hojaz.maiduka26.data.mapper.ConversationMapper.toDomain
import com.hojaz.maiduka26.data.mapper.ConversationMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.ConversationMapper.toEntity
import com.hojaz.maiduka26.data.mapper.ConversationMapper.toMessageDomainList
import com.hojaz.maiduka26.domain.model.Conversation
import com.hojaz.maiduka26.domain.model.Message
import com.hojaz.maiduka26.domain.model.MessageReaction
import com.hojaz.maiduka26.domain.repository.ConversationRepository
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ConversationRepository.
 * Follows offline-first approach: local database is the source of truth.
 */
@Singleton
class ConversationRepositoryImpl @Inject constructor(
    private val conversationDao: ConversationDao,
    private val messageDao: MessageDao,
    private val messageReactionDao: MessageReactionDao,
    private val networkMonitor: NetworkMonitor
) : ConversationRepository {

    override fun getConversations(shopId: String): Flow<List<Conversation>> {
        return conversationDao.getConversationsForShop(shopId).map { it.toDomainList() }
    }

    override suspend fun getConversationById(conversationId: String): Either<Throwable, Conversation?> {
        return try {
            conversationDao.getConversationById(conversationId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting conversation by ID: $conversationId")
            e.left()
        }
    }

    override suspend fun getOrCreateConversation(shopOneId: String, shopTwoId: String): Either<Throwable, Conversation> {
        return try {
            // Check if conversation exists
            val existing = conversationDao.getConversationBetweenShops(shopOneId, shopTwoId)
            if (existing != null) {
                return existing.toDomain().right()
            }

            // Create new conversation
            val conversation = ConversationEntity(
                id = UUID.randomUUID().toString(),
                shopOneId = shopOneId,
                shopTwoId = shopTwoId,
                isActive = true,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )
            conversationDao.insert(conversation)
            conversation.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating conversation")
            e.left()
        }
    }

    override suspend fun archiveConversation(conversationId: String, shopId: String): Either<Throwable, Unit> {
        return try {
            conversationDao.archiveConversation(conversationId, shopId, System.currentTimeMillis())
            Timber.d("Conversation archived: $conversationId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error archiving conversation")
            e.left()
        }
    }

    override suspend fun getUnreadConversationsCount(shopId: String): Int {
        return conversationDao.getUnreadConversationsCount(shopId)
    }

    override fun getMessages(conversationId: String): Flow<List<Message>> {
        return messageDao.getMessagesForConversation(conversationId).map { it.toMessageDomainList() }
    }

    override suspend fun getMessageById(messageId: String): Either<Throwable, Message?> {
        return try {
            messageDao.getMessageById(messageId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting message by ID: $messageId")
            e.left()
        }
    }

    override suspend fun sendMessage(message: Message): Either<Throwable, Message> {
        return try {
            val entity = message.toEntity(syncStatus = "pending")
            messageDao.insert(entity)

            // Update conversation last message
            conversationDao.updateLastMessage(
                conversationId = message.conversationId,
                message = message.message,
                messageBy = message.senderShopId,
                messageAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )

            Timber.d("Message sent locally: ${message.id}")

            if (networkMonitor.isOnline) {
                // TODO: Send via WebSocket or API
            }

            message.right()
        } catch (e: Exception) {
            Timber.e(e, "Error sending message")
            e.left()
        }
    }

    override suspend fun markMessagesAsRead(conversationId: String, shopId: String): Either<Throwable, Unit> {
        return try {
            messageDao.markMessagesAsRead(conversationId, shopId, System.currentTimeMillis())
            Timber.d("Messages marked as read: $conversationId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error marking messages as read")
            e.left()
        }
    }

    override suspend fun deleteMessage(messageId: String, forBoth: Boolean): Either<Throwable, Unit> {
        return try {
            if (forBoth) {
                messageDao.deleteForSender(messageId, System.currentTimeMillis())
                messageDao.deleteForReceiver(messageId, System.currentTimeMillis())
            } else {
                messageDao.deleteForSender(messageId, System.currentTimeMillis())
            }
            Timber.d("Message deleted: $messageId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting message")
            e.left()
        }
    }

    override fun getUnreadMessages(shopId: String): Flow<List<Message>> {
        return messageDao.getUnreadMessagesForShop(shopId).map { it.toMessageDomainList() }
    }

    override suspend fun addReaction(messageId: String, userId: String, reaction: String): Either<Throwable, MessageReaction> {
        return try {
            val reactionEntity = MessageReactionEntity(
                id = UUID.randomUUID().toString(),
                messageId = messageId,
                userId = userId,
                reaction = reaction,
                createdAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )
            messageReactionDao.insert(reactionEntity)

            MessageReaction(
                id = reactionEntity.id,
                messageId = messageId,
                userId = userId,
                reaction = reaction
            ).right()
        } catch (e: Exception) {
            Timber.e(e, "Error adding reaction")
            e.left()
        }
    }

    override suspend fun removeReaction(messageId: String, userId: String): Either<Throwable, Unit> {
        return try {
            messageReactionDao.removeReaction(messageId, userId)
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error removing reaction")
            e.left()
        }
    }

    override suspend fun syncConversations(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pendingMessages = messageDao.getMessagesPendingSync()

            // TODO: Upload pending messages to server
            // TODO: Download new messages from server

            pendingMessages.forEach { message ->
                messageDao.updateSyncStatus(message.id, "synced", System.currentTimeMillis())
            }

            Timber.d("Conversations synced: ${pendingMessages.size} messages uploaded")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing conversations")
            e.left()
        }
    }
}

