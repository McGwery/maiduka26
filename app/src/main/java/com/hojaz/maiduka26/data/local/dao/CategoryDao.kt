package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.CategoryEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Category entity operations.
 */
@Dao
interface CategoryDao : BaseDao<CategoryEntity> {

    /**
     * Gets all categories.
     */
    @Query("SELECT * FROM categories WHERE deleted_at IS NULL ORDER BY name ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    /**
     * Gets a category by ID.
     */
    @Query("SELECT * FROM categories WHERE id = :categoryId")
    suspend fun getCategoryById(categoryId: String): CategoryEntity?

    /**
     * Gets a category by name.
     */
    @Query("SELECT * FROM categories WHERE name = :name AND deleted_at IS NULL")
    suspend fun getCategoryByName(name: String): CategoryEntity?

    /**
     * Searches categories by name.
     */
    @Query("SELECT * FROM categories WHERE name LIKE '%' || :query || '%' AND deleted_at IS NULL ORDER BY name ASC")
    fun searchCategories(query: String): Flow<List<CategoryEntity>>

    /**
     * Gets categories pending sync.
     */
    @Query("SELECT * FROM categories WHERE sync_status = 'pending'")
    suspend fun getCategoriesPendingSync(): List<CategoryEntity>

    /**
     * Updates sync status for a category.
     */
    @Query("UPDATE categories SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :categoryId")
    suspend fun updateSyncStatus(categoryId: String, status: String, syncedAt: Long)

    /**
     * Soft deletes a category.
     */
    @Query("UPDATE categories SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :categoryId")
    suspend fun softDelete(categoryId: String, deletedAt: Long)

    /**
     * Deletes all categories.
     */
    @Query("DELETE FROM categories")
    suspend fun deleteAllCategories()
}

