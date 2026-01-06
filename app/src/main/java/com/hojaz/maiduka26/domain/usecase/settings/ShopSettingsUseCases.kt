package com.hojaz.maiduka26.domain.usecase.settings

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.MemberRole
import com.hojaz.maiduka26.domain.model.Permission
import com.hojaz.maiduka26.domain.model.ShopMember
import com.hojaz.maiduka26.domain.model.ShopSettings
import com.hojaz.maiduka26.domain.repository.ShopSettingsRepository
import kotlinx.coroutines.flow.Flow
import java.util.UUID
import javax.inject.Inject

// ==================== Shop Settings Use Cases ====================

/**
 * Use case for getting shop settings.
 */
class GetShopSettingsUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    operator fun invoke(shopId: String): Flow<ShopSettings?> {
        return repository.getShopSettings(shopId)
    }
}

/**
 * Use case for saving shop settings.
 */
class SaveShopSettingsUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(settings: ShopSettings): Either<Throwable, ShopSettings> {
        return repository.saveShopSettings(settings)
    }
}

/**
 * Use case for updating notification settings.
 */
class UpdateNotificationSettingsUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(
        shopId: String,
        enableSms: Boolean,
        enableEmail: Boolean,
        notifyLowStock: Boolean
    ): Either<Throwable, Unit> {
        return repository.updateNotificationSettings(shopId, enableSms, enableEmail, notifyLowStock)
    }
}

/**
 * Use case for updating sales settings.
 */
class UpdateSalesSettingsUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(
        shopId: String,
        allowCreditSales: Boolean,
        allowDiscounts: Boolean,
        maxDiscountPercentage: Double
    ): Either<Throwable, Unit> {
        return repository.updateSalesSettings(shopId, allowCreditSales, allowDiscounts, maxDiscountPercentage)
    }
}

// ==================== Shop Member Use Cases ====================

/**
 * Use case for getting shop members.
 */
class GetShopMembersUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    operator fun invoke(shopId: String): Flow<List<ShopMember>> {
        return repository.getShopMembers(shopId)
    }
}

/**
 * Use case for getting active shop members.
 */
class GetActiveShopMembersUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    operator fun invoke(shopId: String): Flow<List<ShopMember>> {
        return repository.getActiveShopMembers(shopId)
    }
}

/**
 * Use case for getting a member by ID.
 */
class GetMemberByIdUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(memberId: String): Either<Throwable, ShopMember?> {
        return repository.getMemberById(memberId)
    }
}

/**
 * Use case for adding a new member to the shop.
 */
class AddShopMemberUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(
        shopId: String,
        userId: String,
        role: MemberRole,
        permissions: List<Permission> = emptyList()
    ): Either<Throwable, ShopMember> {
        val member = ShopMember(
            id = UUID.randomUUID().toString(),
            shopId = shopId,
            userId = userId,
            role = role,
            permissions = permissions,
            isActive = true
        )
        return repository.addMember(member)
    }
}

/**
 * Use case for updating member role.
 */
class UpdateMemberRoleUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(memberId: String, role: MemberRole): Either<Throwable, Unit> {
        return repository.updateMemberRole(memberId, role.name.lowercase())
    }
}

/**
 * Use case for updating member permissions.
 */
class UpdateMemberPermissionsUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(memberId: String, permissions: List<Permission>): Either<Throwable, Unit> {
        return repository.updateMemberPermissions(memberId, permissions.map { it.name })
    }
}

/**
 * Use case for deactivating a member.
 */
class DeactivateMemberUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(memberId: String): Either<Throwable, Unit> {
        return repository.deactivateMember(memberId)
    }
}

/**
 * Use case for reactivating a member.
 */
class ReactivateMemberUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(memberId: String): Either<Throwable, Unit> {
        return repository.reactivateMember(memberId)
    }
}

/**
 * Use case for removing a member.
 */
class RemoveMemberUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(memberId: String): Either<Throwable, Unit> {
        return repository.removeMember(memberId)
    }
}

/**
 * Use case for getting member count.
 */
class GetMemberCountUseCase @Inject constructor(
    private val repository: ShopSettingsRepository
) {
    suspend operator fun invoke(shopId: String): Int {
        return repository.getMemberCount(shopId)
    }
}

