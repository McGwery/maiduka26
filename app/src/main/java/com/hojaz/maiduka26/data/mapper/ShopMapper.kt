package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.ShopEntity
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.util.DateTimeUtil

/**
 * Mapper for Shop entity and domain model conversions.
 */
object ShopMapper {

    fun ShopEntity.toDomain(): Shop {
        return Shop(
            id = id,
            ownerId = ownerId,
            name = name,
            businessType = businessType,
            phoneNumber = phoneNumber,
            address = address,
            agentCode = agentCode,
            currency = currency,
            imageUrl = imageUrl,
            isActive = isActive,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Shop.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ShopEntity {
        return ShopEntity(
            id = id,
            ownerId = ownerId,
            name = name,
            businessType = businessType,
            phoneNumber = phoneNumber,
            address = address,
            agentCode = agentCode,
            currency = currency,
            imageUrl = imageUrl,
            isActive = isActive,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<ShopEntity>.toDomainList(): List<Shop> = map { it.toDomain() }
}

