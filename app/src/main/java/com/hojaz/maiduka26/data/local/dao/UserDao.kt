package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for User entity operations.
 */
@Dao
interface UserDao : BaseDao<UserEntity> {

    /**
     * Gets all users.
     */
    @Query("SELECT * FROM users ORDER BY name ASC")
    fun getAllUsers(): Flow<List<UserEntity>>

    /**
     * Gets a user by ID.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    suspend fun getUserById(userId: String): UserEntity?

    /**
     * Gets a user by ID as Flow.
     */
    @Query("SELECT * FROM users WHERE id = :userId")
    fun getUserByIdFlow(userId: String): Flow<UserEntity?>

    /**
     * Gets a user by email.
     */
    @Query("SELECT * FROM users WHERE email = :email")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Gets a user by phone.
     */
    @Query("SELECT * FROM users WHERE phone = :phone")
    suspend fun getUserByPhone(phone: String): UserEntity?

    /**
     * Gets users pending sync.
     */
    @Query("SELECT * FROM users WHERE sync_status = 'pending'")
    suspend fun getUsersPendingSync(): List<UserEntity>

    /**
     * Updates sync status for a user.
     */
    @Query("UPDATE users SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :userId")
    suspend fun updateSyncStatus(userId: String, status: String, syncedAt: Long)

    /**
     * Deletes all users.
     */
    @Query("DELETE FROM users")
    suspend fun deleteAllUsers()
}

