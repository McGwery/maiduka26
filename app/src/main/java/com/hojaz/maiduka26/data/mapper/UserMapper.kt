package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.UserEntity
import com.hojaz.maiduka26.domain.model.User
import com.hojaz.maiduka26.util.DateTimeUtil

/**
 * Mapper for User entity and domain model conversions.
 */
object UserMapper {

    fun UserEntity.toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            emailVerifiedAt = emailVerifiedAt?.let { DateTimeUtil.fromMillis(it) },
            phoneVerifiedAt = phoneVerifiedAt?.let { DateTimeUtil.fromMillis(it) },
            isPhoneLoginEnabled = isPhoneLoginEnabled,
            twoFactorEnabled = twoFactorEnabled,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun User.toEntity(
        passwordHash: String? = null,
        rememberToken: String? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            email = email,
            phone = phone,
            emailVerifiedAt = emailVerifiedAt?.let { DateTimeUtil.toMillis(it) },
            phoneVerifiedAt = phoneVerifiedAt?.let { DateTimeUtil.toMillis(it) },
            isPhoneLoginEnabled = isPhoneLoginEnabled,
            passwordHash = passwordHash,
            twoFactorEnabled = twoFactorEnabled,
            rememberToken = rememberToken,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<UserEntity>.toDomainList(): List<User> = map { it.toDomain() }
}

