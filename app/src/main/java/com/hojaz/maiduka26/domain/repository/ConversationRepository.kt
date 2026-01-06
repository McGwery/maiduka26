package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Conversation
import com.hojaz.maiduka26.domain.model.Message
import com.hojaz.maiduka26.domain.model.MessageReaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for messaging/conversation operations.
 */
interface ConversationRepository {

    // ==================== Conversations ====================

    /**
     * Get all conversations for a shop.
     */
    fun getConversations(shopId: String): Flow<List<Conversation>>

    /**
     * Get a conversation by ID.
     */
    suspend fun getConversationById(conversationId: String): Either<Throwable, Conversation?>

    /**
     * Get or create a conversation between two shops.
     */
    suspend fun getOrCreateConversation(shopOneId: String, shopTwoId: String): Either<Throwable, Conversation>

    /**
     * Archive a conversation.
     */
    suspend fun archiveConversation(conversationId: String, shopId: String): Either<Throwable, Unit>

    /**
     * Get unread conversations count.
     */
    suspend fun getUnreadConversationsCount(shopId: String): Int

    // ==================== Messages ====================

    /**
     * Get messages for a conversation.
     */
    fun getMessages(conversationId: String): Flow<List<Message>>

    /**
     * Get a message by ID.
     */
    suspend fun getMessageById(messageId: String): Either<Throwable, Message?>

    /**
     * Send a message.
     */
    suspend fun sendMessage(message: Message): Either<Throwable, Message>

    /**
     * Mark messages as read.
     */
    suspend fun markMessagesAsRead(conversationId: String, shopId: String): Either<Throwable, Unit>

    /**
     * Delete a message.
     */
    suspend fun deleteMessage(messageId: String, forBoth: Boolean): Either<Throwable, Unit>

    /**
     * Get unread messages for a shop.
     */
    fun getUnreadMessages(shopId: String): Flow<List<Message>>

    // ==================== Reactions ====================

    /**
     * Add a reaction to a message.
     */
    suspend fun addReaction(messageId: String, userId: String, reaction: String): Either<Throwable, MessageReaction>

    /**
     * Remove a reaction from a message.
     */
    suspend fun removeReaction(messageId: String, userId: String): Either<Throwable, Unit>

    // ==================== Sync ====================

    /**
     * Sync conversations with remote server.
     */
    suspend fun syncConversations(shopId: String): Either<Throwable, Unit>
}

