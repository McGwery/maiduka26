package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.ShopMemberEntity
import com.hojaz.maiduka26.data.local.entity.ShopSettingsEntity
import com.hojaz.maiduka26.data.local.entity.SubscriptionEntity
import com.hojaz.maiduka26.domain.model.*
import com.hojaz.maiduka26.util.DateTimeUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.math.BigDecimal

/**
 * Mapper for Shop Configuration entity and domain model conversions.
 */
object ShopConfigurationMapper {

    private val gson = Gson()

    fun ShopMemberEntity.toDomain(user: User? = null, shop: Shop? = null): ShopMember {
        return ShopMember(
            id = id,
            shopId = shopId,
            userId = userId,
            role = MemberRole.valueOf(role.uppercase()),
            permissions = permissions?.let { parsePermissions(it) } ?: emptyList(),
            isActive = isActive,
            user = user,
            shop = shop,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun ShopMember.toEntity(
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ShopMemberEntity {
        return ShopMemberEntity(
            id = id,
            shopId = shopId,
            userId = userId,
            role = role.name.lowercase(),
            permissions = permissions.takeIf { it.isNotEmpty() }?.let {
                gson.toJson(it.map { p -> p.name })
            },
            isActive = isActive,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    private fun parsePermissions(json: String): List<Permission> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            val names: List<String> = gson.fromJson(json, type)
            names.mapNotNull { name ->
                try { Permission.valueOf(name) } catch (e: Exception) { null }
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun ShopSettingsEntity.toDomain(): ShopSettings {
        return ShopSettings(
            id = id,
            shopId = shopId,
            businessEmail = businessEmail,
            businessWebsite = businessWebsite,
            taxId = taxId,
            registrationNumber = registrationNumber,
            enableSmsNotifications = enableSmsNotifications,
            enableEmailNotifications = enableEmailNotifications,
            notifyLowStock = notifyLowStock,
            lowStockThreshold = lowStockThreshold,
            notifyDailySalesSummary = notifyDailySalesSummary,
            dailySummaryTime = dailySummaryTime,
            autoPrintReceipt = autoPrintReceipt,
            allowCreditSales = allowCreditSales,
            creditLimitDays = creditLimitDays,
            requireCustomerForCredit = requireCustomerForCredit,
            allowDiscounts = allowDiscounts,
            maxDiscountPercentage = BigDecimal(maxDiscountPercentage),
            trackStock = trackStock,
            allowNegativeStock = allowNegativeStock,
            autoDeductStockOnSale = autoDeductStockOnSale,
            stockValuationMethod = StockValuationMethod.valueOf(stockValuationMethod.uppercase()),
            receiptHeader = receiptHeader,
            receiptFooter = receiptFooter,
            showShopLogoOnReceipt = showShopLogoOnReceipt,
            showTaxOnReceipt = showTaxOnReceipt,
            taxPercentage = BigDecimal(taxPercentage),
            openingTime = openingTime,
            closingTime = closingTime,
            workingDays = workingDays?.let { parseWorkingDays(it) } ?: listOf(1, 2, 3, 4, 5, 6),
            language = language,
            timezone = timezone,
            dateFormat = dateFormat,
            timeFormat = timeFormat,
            requirePinForRefunds = requirePinForRefunds,
            requirePinForDiscounts = requirePinForDiscounts,
            enableTwoFactorAuth = enableTwoFactorAuth,
            autoBackup = autoBackup,
            backupFrequency = BackupFrequency.valueOf(backupFrequency.uppercase()),
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun ShopSettings.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): ShopSettingsEntity {
        return ShopSettingsEntity(
            id = id,
            shopId = shopId,
            businessEmail = businessEmail,
            businessWebsite = businessWebsite,
            taxId = taxId,
            registrationNumber = registrationNumber,
            enableSmsNotifications = enableSmsNotifications,
            enableEmailNotifications = enableEmailNotifications,
            notifyLowStock = notifyLowStock,
            lowStockThreshold = lowStockThreshold,
            notifyDailySalesSummary = notifyDailySalesSummary,
            dailySummaryTime = dailySummaryTime,
            autoPrintReceipt = autoPrintReceipt,
            allowCreditSales = allowCreditSales,
            creditLimitDays = creditLimitDays,
            requireCustomerForCredit = requireCustomerForCredit,
            allowDiscounts = allowDiscounts,
            maxDiscountPercentage = maxDiscountPercentage.toPlainString(),
            trackStock = trackStock,
            allowNegativeStock = allowNegativeStock,
            autoDeductStockOnSale = autoDeductStockOnSale,
            stockValuationMethod = stockValuationMethod.name.lowercase(),
            receiptHeader = receiptHeader,
            receiptFooter = receiptFooter,
            showShopLogoOnReceipt = showShopLogoOnReceipt,
            showTaxOnReceipt = showTaxOnReceipt,
            taxPercentage = taxPercentage.toPlainString(),
            openingTime = openingTime,
            closingTime = closingTime,
            workingDays = gson.toJson(workingDays),
            language = language,
            timezone = timezone,
            dateFormat = dateFormat,
            timeFormat = timeFormat,
            requirePinForRefunds = requirePinForRefunds,
            requirePinForDiscounts = requirePinForDiscounts,
            enableTwoFactorAuth = enableTwoFactorAuth,
            autoBackup = autoBackup,
            backupFrequency = backupFrequency.name.lowercase(),
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    private fun parseWorkingDays(json: String): List<Int> {
        return try {
            val type = object : TypeToken<List<Int>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            listOf(1, 2, 3, 4, 5, 6)
        }
    }

    fun SubscriptionEntity.toDomain(): Subscription {
        return Subscription(
            id = id,
            shopId = shopId,
            plan = SubscriptionPlan.valueOf(plan.uppercase()),
            type = SubscriptionType.valueOf(type.uppercase()),
            status = SubscriptionStatus.valueOf(status.uppercase()),
            price = BigDecimal(price),
            currency = currency,
            startsAt = startsAt?.let { DateTimeUtil.fromMillis(it) },
            expiresAt = expiresAt?.let { DateTimeUtil.fromMillis(it) },
            autoRenew = autoRenew,
            paymentMethod = paymentMethod,
            transactionReference = transactionReference,
            features = features?.let { parseFeatures(it) } ?: emptyList(),
            maxUsers = maxUsers,
            maxProducts = maxProducts,
            notes = notes,
            cancelledAt = cancelledAt?.let { DateTimeUtil.fromMillis(it) },
            cancelledReason = cancelledReason,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Subscription.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): SubscriptionEntity {
        return SubscriptionEntity(
            id = id,
            shopId = shopId,
            plan = plan.name.lowercase(),
            type = type.name.lowercase(),
            status = status.name.lowercase(),
            price = price.toPlainString(),
            currency = currency,
            startsAt = startsAt?.let { DateTimeUtil.toMillis(it) },
            expiresAt = expiresAt?.let { DateTimeUtil.toMillis(it) },
            autoRenew = autoRenew,
            paymentMethod = paymentMethod,
            transactionReference = transactionReference,
            features = features.takeIf { it.isNotEmpty() }?.let { gson.toJson(it) },
            maxUsers = maxUsers,
            maxProducts = maxProducts,
            notes = notes,
            cancelledAt = cancelledAt?.let { DateTimeUtil.toMillis(it) },
            cancelledReason = cancelledReason,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    private fun parseFeatures(json: String): List<String> {
        return try {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(json, type)
        } catch (e: Exception) {
            emptyList()
        }
    }

    fun List<ShopMemberEntity>.toMemberDomainList(): List<ShopMember> = map { it.toDomain() }
    fun List<SubscriptionEntity>.toSubscriptionDomainList(): List<Subscription> = map { it.toDomain() }
}

