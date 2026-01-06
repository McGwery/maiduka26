package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.MessageReactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for MessageReaction entity operations.
 */
@Dao
interface MessageReactionDao : BaseDao<MessageReactionEntity> {

    /**
     * Gets reactions for a message.
     */
    @Query("SELECT * FROM message_reactions WHERE message_id = :messageId")
    fun getReactionsForMessage(messageId: String): Flow<List<MessageReactionEntity>>

    /**
     * Gets a specific reaction.
     */
    @Query("SELECT * FROM message_reactions WHERE message_id = :messageId AND user_id = :userId")
    suspend fun getReaction(messageId: String, userId: String): MessageReactionEntity?

    /**
     * Removes a reaction.
     */
    @Query("DELETE FROM message_reactions WHERE message_id = :messageId AND user_id = :userId")
    suspend fun removeReaction(messageId: String, userId: String)

    /**
     * Gets reaction count for a message.
     */
    @Query("SELECT COUNT(*) FROM message_reactions WHERE message_id = :messageId")
    suspend fun getReactionCount(messageId: String): Int

    /**
     * Gets reactions pending sync.
     */
    @Query("SELECT * FROM message_reactions WHERE sync_status = 'pending'")
    suspend fun getReactionsPendingSync(): List<MessageReactionEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE message_reactions SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all reactions.
     */
    @Query("DELETE FROM message_reactions")
    suspend fun deleteAllReactions()
}

