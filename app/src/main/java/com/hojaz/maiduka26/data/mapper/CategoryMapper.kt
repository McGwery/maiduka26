package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.CategoryEntity
import com.hojaz.maiduka26.domain.model.Category
import com.hojaz.maiduka26.util.DateTimeUtil

/**
 * Mapper for Category entity and domain model conversions.
 */
object CategoryMapper {

    fun CategoryEntity.toDomain(): Category {
        return Category(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Category.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<CategoryEntity>.toDomainList(): List<Category> = map { it.toDomain() }
}

