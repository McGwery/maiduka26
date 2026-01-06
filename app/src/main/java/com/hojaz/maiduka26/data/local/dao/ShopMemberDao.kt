package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ShopMemberEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ShopMember entity operations.
 */
@Dao
interface ShopMemberDao : BaseDao<ShopMemberEntity> {

    /**
     * Gets all members for a shop.
     */
    @Query("SELECT * FROM shop_members WHERE shop_id = :shopId")
    fun getMembersByShop(shopId: String): Flow<List<ShopMemberEntity>>

    /**
     * Gets active members for a shop.
     */
    @Query("SELECT * FROM shop_members WHERE shop_id = :shopId AND is_active = 1")
    fun getActiveMembersByShop(shopId: String): Flow<List<ShopMemberEntity>>

    /**
     * Gets a member by ID.
     */
    @Query("SELECT * FROM shop_members WHERE id = :memberId")
    suspend fun getMemberById(memberId: String): ShopMemberEntity?

    /**
     * Gets a member by shop and user.
     */
    @Query("SELECT * FROM shop_members WHERE shop_id = :shopId AND user_id = :userId")
    suspend fun getMemberByShopAndUser(shopId: String, userId: String): ShopMemberEntity?

    /**
     * Gets a member by user ID.
     */
    @Query("SELECT * FROM shop_members WHERE shop_id = :shopId AND user_id = :userId LIMIT 1")
    suspend fun getMemberByUserId(shopId: String, userId: String): ShopMemberEntity?

    /**
     * Gets all shops a user is a member of.
     */
    @Query("SELECT * FROM shop_members WHERE user_id = :userId AND is_active = 1")
    fun getShopsByUser(userId: String): Flow<List<ShopMemberEntity>>

    /**
     * Gets members by role.
     */
    @Query("SELECT * FROM shop_members WHERE shop_id = :shopId AND role = :role AND is_active = 1")
    fun getMembersByRole(shopId: String, role: String): Flow<List<ShopMemberEntity>>

    /**
     * Updates member active status.
     */
    @Query("UPDATE shop_members SET is_active = :isActive, updated_at = :updatedAt WHERE id = :memberId")
    suspend fun updateActiveStatus(memberId: String, isActive: Boolean, updatedAt: Long)

    /**
     * Updates member role.
     */
    @Query("UPDATE shop_members SET role = :role, updated_at = :updatedAt WHERE id = :memberId")
    suspend fun updateRole(memberId: String, role: String, updatedAt: Long)

    /**
     * Updates member permissions.
     */
    @Query("UPDATE shop_members SET permissions = :permissions, updated_at = :updatedAt WHERE id = :memberId")
    suspend fun updatePermissions(memberId: String, permissions: String?, updatedAt: Long)

    /**
     * Gets member count for a shop.
     */
    @Query("SELECT COUNT(*) FROM shop_members WHERE shop_id = :shopId AND is_active = 1")
    suspend fun getMemberCount(shopId: String): Int

    /**
     * Gets members pending sync.
     */
    @Query("SELECT * FROM shop_members WHERE sync_status = 'pending'")
    suspend fun getMembersPendingSync(): List<ShopMemberEntity>

    /**
     * Updates sync status for a member.
     */
    @Query("UPDATE shop_members SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :memberId")
    suspend fun updateSyncStatus(memberId: String, status: String, syncedAt: Long)

    /**
     * Deletes all members.
     */
    @Query("DELETE FROM shop_members")
    suspend fun deleteAllMembers()
}

