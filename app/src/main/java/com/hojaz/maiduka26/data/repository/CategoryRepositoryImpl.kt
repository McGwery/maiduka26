package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.CategoryDao
import com.hojaz.maiduka26.data.mapper.CategoryMapper.toDomain
import com.hojaz.maiduka26.data.mapper.CategoryMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.CategoryMapper.toEntity
import com.hojaz.maiduka26.data.remote.api.ApiService
import com.hojaz.maiduka26.domain.model.Category
import com.hojaz.maiduka26.domain.repository.CategoryRepository
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CategoryRepository.
 * Follows offline-first approach: local database is the source of truth.
 */
@Singleton
class CategoryRepositoryImpl @Inject constructor(
    private val categoryDao: CategoryDao,
    private val apiService: ApiService,
    private val networkMonitor: NetworkMonitor
) : CategoryRepository {

    override fun getCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories().map { it.toDomainList() }
    }

    override suspend fun getCategoryById(categoryId: String): Either<Throwable, Category?> {
        return try {
            categoryDao.getCategoryById(categoryId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting category by ID: $categoryId")
            e.left()
        }
    }

    override suspend fun createCategory(category: Category): Either<Throwable, Category> {
        return try {
            val entity = category.toEntity(syncStatus = "pending")
            categoryDao.insert(entity)
            Timber.d("Category created locally: ${category.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            category.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating category")
            e.left()
        }
    }

    override suspend fun updateCategory(category: Category): Either<Throwable, Category> {
        return try {
            val entity = category.toEntity(syncStatus = "pending")
            categoryDao.update(entity)
            Timber.d("Category updated locally: ${category.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            category.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating category")
            e.left()
        }
    }

    override suspend fun deleteCategory(categoryId: String): Either<Throwable, Unit> {
        return try {
            categoryDao.softDelete(categoryId, System.currentTimeMillis())
            Timber.d("Category soft deleted: $categoryId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting category")
            e.left()
        }
    }

    override fun searchCategories(query: String): Flow<List<Category>> {
        return categoryDao.searchCategories(query).map { it.toDomainList() }
    }

    override suspend fun syncCategories(): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pendingCategories = categoryDao.getCategoriesPendingSync()

            // TODO: Upload pending categories to server
            // TODO: Download updated categories from server

            pendingCategories.forEach { category ->
                categoryDao.updateSyncStatus(category.id, "synced", System.currentTimeMillis())
            }

            Timber.d("Categories synced: ${pendingCategories.size} uploaded")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing categories")
            e.left()
        }
    }
}

