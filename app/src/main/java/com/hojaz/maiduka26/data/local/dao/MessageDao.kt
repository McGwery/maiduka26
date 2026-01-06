package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Message entity operations.
 */
@Dao
interface MessageDao : BaseDao<MessageEntity> {

    /**
     * Gets messages for a conversation.
     */
    @Query("""
        SELECT * FROM messages 
        WHERE conversation_id = :conversationId 
        AND deleted_at IS NULL
        ORDER BY created_at DESC
    """)
    fun getMessagesForConversation(conversationId: String): Flow<List<MessageEntity>>

    /**
     * Gets a message by ID.
     */
    @Query("SELECT * FROM messages WHERE id = :messageId")
    suspend fun getMessageById(messageId: String): MessageEntity?

    /**
     * Gets unread messages for a shop.
     */
    @Query("""
        SELECT * FROM messages 
        WHERE receiver_shop_id = :shopId 
        AND is_read = 0 
        AND deleted_at IS NULL
        ORDER BY created_at DESC
    """)
    fun getUnreadMessagesForShop(shopId: String): Flow<List<MessageEntity>>

    /**
     * Gets unread message count for a conversation.
     */
    @Query("""
        SELECT COUNT(*) FROM messages 
        WHERE conversation_id = :conversationId 
        AND receiver_shop_id = :shopId 
        AND is_read = 0 
        AND deleted_at IS NULL
    """)
    suspend fun getUnreadMessageCount(conversationId: String, shopId: String): Int

    /**
     * Marks messages as read.
     */
    @Query("""
        UPDATE messages SET 
        is_read = 1, 
        read_at = :readAt,
        updated_at = :readAt
        WHERE conversation_id = :conversationId 
        AND receiver_shop_id = :shopId 
        AND is_read = 0
    """)
    suspend fun markMessagesAsRead(conversationId: String, shopId: String, readAt: Long)

    /**
     * Marks a message as delivered.
     */
    @Query("UPDATE messages SET is_delivered = 1, delivered_at = :deliveredAt, updated_at = :deliveredAt WHERE id = :messageId")
    suspend fun markMessageAsDelivered(messageId: String, deliveredAt: Long)

    /**
     * Soft deletes a message for sender.
     */
    @Query("UPDATE messages SET is_deleted_by_sender = 1, updated_at = :updatedAt WHERE id = :messageId")
    suspend fun deleteForSender(messageId: String, updatedAt: Long)

    /**
     * Soft deletes a message for receiver.
     */
    @Query("UPDATE messages SET is_deleted_by_receiver = 1, updated_at = :updatedAt WHERE id = :messageId")
    suspend fun deleteForReceiver(messageId: String, updatedAt: Long)

    /**
     * Gets messages pending sync.
     */
    @Query("SELECT * FROM messages WHERE sync_status = 'pending'")
    suspend fun getMessagesPendingSync(): List<MessageEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE messages SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all messages.
     */
    @Query("DELETE FROM messages")
    suspend fun deleteAllMessages()
}

