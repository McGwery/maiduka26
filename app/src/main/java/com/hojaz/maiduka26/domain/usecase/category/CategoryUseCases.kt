package com.hojaz.maiduka26.domain.usecase.category

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Category
import com.hojaz.maiduka26.domain.repository.CategoryRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all categories.
 */
class GetCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(): Flow<List<Category>> {
        return categoryRepository.getCategories()
    }
}

/**
 * Use case for getting a category by ID.
 */
class GetCategoryByIdUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: String): Either<Throwable, Category?> {
        return categoryRepository.getCategoryById(categoryId)
    }
}

/**
 * Use case for creating a category.
 */
class CreateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Either<Throwable, Category> {
        return categoryRepository.createCategory(category)
    }
}

/**
 * Use case for updating a category.
 */
class UpdateCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(category: Category): Either<Throwable, Category> {
        return categoryRepository.updateCategory(category)
    }
}

/**
 * Use case for deleting a category.
 */
class DeleteCategoryUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    suspend operator fun invoke(categoryId: String): Either<Throwable, Unit> {
        return categoryRepository.deleteCategory(categoryId)
    }
}

/**
 * Use case for searching categories.
 */
class SearchCategoriesUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository
) {
    operator fun invoke(query: String): Flow<List<Category>> {
        return categoryRepository.searchCategories(query)
    }
}

