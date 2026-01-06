package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.TypingIndicatorEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for TypingIndicator entity operations.
 */
@Dao
interface TypingIndicatorDao : BaseDao<TypingIndicatorEntity> {

    /**
     * Gets typing indicators for a conversation.
     */
    @Query("SELECT * FROM typing_indicators WHERE conversation_id = :conversationId AND expires_at > :currentTime")
    fun getTypingIndicatorsForConversation(conversationId: String, currentTime: Long): Flow<List<TypingIndicatorEntity>>

    /**
     * Gets typing indicator for a specific user in a conversation.
     */
    @Query("SELECT * FROM typing_indicators WHERE conversation_id = :conversationId AND user_id = :userId")
    suspend fun getTypingIndicator(conversationId: String, userId: String): TypingIndicatorEntity?

    /**
     * Removes typing indicator.
     */
    @Query("DELETE FROM typing_indicators WHERE conversation_id = :conversationId AND user_id = :userId")
    suspend fun removeTypingIndicator(conversationId: String, userId: String)

    /**
     * Clears expired typing indicators.
     */
    @Query("DELETE FROM typing_indicators WHERE expires_at <= :currentTime")
    suspend fun clearExpiredIndicators(currentTime: Long)

    /**
     * Gets indicators pending sync.
     */
    @Query("SELECT * FROM typing_indicators WHERE sync_status = 'pending'")
    suspend fun getIndicatorsPendingSync(): List<TypingIndicatorEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE typing_indicators SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: String, status: String, syncedAt: Long)

    /**
     * Deletes all indicators.
     */
    @Query("DELETE FROM typing_indicators")
    suspend fun deleteAllIndicators()
}

