package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ConversationEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Conversation entity operations.
 */
@Dao
interface ConversationDao : BaseDao<ConversationEntity> {

    /**
     * Gets all conversations for a shop.
     */
    @Query("""
        SELECT * FROM conversations 
        WHERE (shop_one_id = :shopId OR shop_two_id = :shopId) 
        AND is_active = 1
        ORDER BY last_message_at DESC
    """)
    fun getConversationsForShop(shopId: String): Flow<List<ConversationEntity>>

    /**
     * Gets a conversation by ID.
     */
    @Query("SELECT * FROM conversations WHERE id = :conversationId")
    suspend fun getConversationById(conversationId: String): ConversationEntity?

    /**
     * Gets a conversation between two shops.
     */
    @Query("""
        SELECT * FROM conversations 
        WHERE (shop_one_id = :shopOneId AND shop_two_id = :shopTwoId) 
        OR (shop_one_id = :shopTwoId AND shop_two_id = :shopOneId)
    """)
    suspend fun getConversationBetweenShops(shopOneId: String, shopTwoId: String): ConversationEntity?

    /**
     * Gets unread conversations count.
     */
    @Query("""
        SELECT COUNT(*) FROM conversations c
        INNER JOIN messages m ON c.id = m.conversation_id
        WHERE (c.shop_one_id = :shopId OR c.shop_two_id = :shopId)
        AND m.receiver_shop_id = :shopId
        AND m.is_read = 0
        AND c.is_active = 1
    """)
    suspend fun getUnreadConversationsCount(shopId: String): Int

    /**
     * Updates last message info.
     */
    @Query("""
        UPDATE conversations SET 
        last_message = :message,
        last_message_by = :messageBy,
        last_message_at = :messageAt,
        updated_at = :updatedAt
        WHERE id = :conversationId
    """)
    suspend fun updateLastMessage(
        conversationId: String,
        message: String?,
        messageBy: String?,
        messageAt: Long,
        updatedAt: Long
    )

    /**
     * Archives a conversation for a shop.
     */
    @Query("""
        UPDATE conversations SET 
        is_archived_by_shop_one = CASE WHEN shop_one_id = :shopId THEN 1 ELSE is_archived_by_shop_one END,
        is_archived_by_shop_two = CASE WHEN shop_two_id = :shopId THEN 1 ELSE is_archived_by_shop_two END,
        updated_at = :updatedAt
        WHERE id = :conversationId
    """)
    suspend fun archiveConversation(conversationId: String, shopId: String, updatedAt: Long)

    /**
     * Gets conversations pending sync.
     */
    @Query("SELECT * FROM conversations WHERE sync_status = 'pending'")
    suspend fun getConversationsPendingSync(): List<ConversationEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE conversations SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all conversations.
     */
    @Query("DELETE FROM conversations")
    suspend fun deleteAllConversations()
}

