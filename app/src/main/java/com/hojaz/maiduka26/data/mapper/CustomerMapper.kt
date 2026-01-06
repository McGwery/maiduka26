package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.CustomerEntity
import com.hojaz.maiduka26.domain.model.Customer
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for Customer entity and domain model conversions.
 */
object CustomerMapper {

    fun CustomerEntity.toDomain(): Customer {
        return Customer(
            id = id,
            shopId = shopId,
            name = name,
            phone = phone,
            email = email,
            address = address,
            creditLimit = BigDecimal(creditLimit),
            currentDebt = BigDecimal(currentDebt),
            totalPurchases = BigDecimal(totalPurchases),
            totalPaid = BigDecimal(totalPaid),
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.fromMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.fromMillis(it) }
        )
    }

    fun Customer.toEntity(
        deletedAt: Long? = null,
        syncStatus: String = "synced",
        lastSyncedAt: Long? = null
    ): CustomerEntity {
        return CustomerEntity(
            id = id,
            shopId = shopId,
            name = name,
            phone = phone,
            email = email,
            address = address,
            creditLimit = creditLimit.toPlainString(),
            currentDebt = currentDebt.toPlainString(),
            totalPurchases = totalPurchases.toPlainString(),
            totalPaid = totalPaid.toPlainString(),
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.toMillis(it) },
            deletedAt = deletedAt,
            syncStatus = syncStatus,
            lastSyncedAt = lastSyncedAt
        )
    }

    fun List<CustomerEntity>.toDomainList(): List<Customer> = map { it.toDomain() }
}

