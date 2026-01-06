package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Category
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for category operations.
 */
interface CategoryRepository {

    /**
     * Get all categories.
     */
    fun getCategories(): Flow<List<Category>>

    /**
     * Get a category by ID.
     */
    suspend fun getCategoryById(categoryId: String): Either<Throwable, Category?>

    /**
     * Create a new category.
     */
    suspend fun createCategory(category: Category): Either<Throwable, Category>

    /**
     * Update an existing category.
     */
    suspend fun updateCategory(category: Category): Either<Throwable, Category>

    /**
     * Delete a category.
     */
    suspend fun deleteCategory(categoryId: String): Either<Throwable, Unit>

    /**
     * Search categories by name.
     */
    fun searchCategories(query: String): Flow<List<Category>>

    /**
     * Sync categories with remote server.
     */
    suspend fun syncCategories(): Either<Throwable, Unit>
}

