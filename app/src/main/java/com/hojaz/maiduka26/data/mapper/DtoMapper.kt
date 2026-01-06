package com.hojaz.maiduka26.data.mapper

import com.hojaz.maiduka26.data.local.entity.*
import com.hojaz.maiduka26.data.remote.dto.response.*
import com.hojaz.maiduka26.domain.model.*
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal

/**
 * Mapper for converting API DTOs to entities and domain models.
 */
object DtoMapper {

    // ==================== User DTO Mappers ====================

    fun UserResponse.toEntity(): UserEntity {
        return UserEntity(
            id = id,
            name = name,
            email = email,
            phone = phone,
            emailVerifiedAt = emailVerifiedAt?.let { DateTimeUtil.parseMillis(it) },
            phoneVerifiedAt = phoneVerifiedAt?.let { DateTimeUtil.parseMillis(it) },
            isPhoneLoginEnabled = isPhoneLoginEnabled,
            twoFactorEnabled = twoFactorEnabled,
            createdAt = createdAt?.let { DateTimeUtil.parseMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun UserResponse.toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            emailVerifiedAt = emailVerifiedAt?.let { DateTimeUtil.parseDateTime(it) },
            phoneVerifiedAt = phoneVerifiedAt?.let { DateTimeUtil.parseDateTime(it) },
            isPhoneLoginEnabled = isPhoneLoginEnabled,
            twoFactorEnabled = twoFactorEnabled,
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }
        )
    }

    // ==================== Shop DTO Mappers ====================

    fun ShopResponse.toEntity(): ShopEntity {
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
            createdAt = createdAt?.let { DateTimeUtil.parseMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun ShopResponse.toDomain(): Shop {
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
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }
        )
    }

    // ==================== Product DTO Mappers ====================

    fun ProductResponse.toEntity(): ProductEntity {
        return ProductEntity(
            id = id,
            shopId = shopId,
            categoryId = categoryId,
            productType = productType,
            productName = productName,
            description = description,
            sku = sku,
            barcode = barcode,
            costPerUnit = costPerUnit,
            unitType = unitType,
            breakDownCountPerUnit = breakDownCountPerUnit,
            smallItemName = smallItemName,
            sellWholeUnits = sellWholeUnits,
            pricePerUnit = pricePerUnit,
            sellIndividualItems = sellIndividualItems,
            pricePerItem = pricePerItem,
            currentStock = currentStock,
            lowStockThreshold = lowStockThreshold,
            trackInventory = trackInventory,
            imageUrl = imageUrl,
            createdAt = createdAt?.let { DateTimeUtil.parseMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun ProductResponse.toDomain(): Product {
        return Product(
            id = id,
            shopId = shopId,
            categoryId = categoryId,
            productType = ProductType.valueOf(productType.uppercase()),
            productName = productName,
            description = description,
            sku = sku,
            barcode = barcode,
            costPerUnit = costPerUnit?.let { BigDecimal(it) },
            unitType = UnitType.valueOf(unitType.uppercase()),
            breakDownCountPerUnit = breakDownCountPerUnit,
            smallItemName = smallItemName,
            sellWholeUnits = sellWholeUnits,
            pricePerUnit = pricePerUnit?.let { BigDecimal(it) },
            sellIndividualItems = sellIndividualItems,
            pricePerItem = pricePerItem?.let { BigDecimal(it) },
            currentStock = currentStock,
            lowStockThreshold = lowStockThreshold,
            trackInventory = trackInventory,
            imageUrl = imageUrl,
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }
        )
    }

    // ==================== Customer DTO Mappers ====================

    fun CustomerResponse.toEntity(): CustomerEntity {
        return CustomerEntity(
            id = id,
            shopId = shopId,
            name = name,
            phone = phone,
            email = email,
            address = address,
            creditLimit = creditLimit,
            currentDebt = currentDebt,
            totalPurchases = totalPurchases,
            totalPaid = totalPaid,
            notes = notes,
            createdAt = createdAt?.let { DateTimeUtil.parseMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun CustomerResponse.toDomain(): Customer {
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
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }
        )
    }

    // ==================== Category DTO Mappers ====================

    fun CategoryResponse.toEntity(): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt?.let { DateTimeUtil.parseMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun CategoryResponse.toDomain(): Category {
        return Category(
            id = id,
            name = name,
            description = description,
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }
        )
    }

    // ==================== Sale DTO Mappers ====================

    fun SaleResponse.toEntity(): SaleEntity {
        return SaleEntity(
            id = id,
            shopId = shopId,
            customerId = customerId,
            userId = userId,
            saleNumber = saleNumber,
            subtotal = subtotal,
            taxRate = taxRate,
            taxAmount = taxAmount,
            discountAmount = discountAmount,
            discountPercentage = discountPercentage,
            totalAmount = totalAmount,
            amountPaid = amountPaid,
            changeAmount = changeAmount,
            debtAmount = debtAmount,
            profitAmount = profitAmount,
            status = status,
            paymentStatus = paymentStatus,
            notes = notes,
            saleDate = DateTimeUtil.parseMillis(saleDate) ?: System.currentTimeMillis(),
            createdAt = createdAt?.let { DateTimeUtil.parseMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun SaleItemResponse.toEntity(): SaleItemEntity {
        return SaleItemEntity(
            id = id,
            saleId = saleId,
            productId = productId,
            productName = productName,
            productSku = productSku,
            quantity = quantity,
            unitType = unitType,
            originalPrice = originalPrice,
            sellingPrice = sellingPrice,
            costPrice = costPrice,
            discountAmount = discountAmount,
            subtotal = subtotal,
            total = total,
            profit = profit,
            createdAt = System.currentTimeMillis(),
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    fun SalePaymentResponse.toEntity(): SalePaymentEntity {
        return SalePaymentEntity(
            id = id,
            saleId = saleId,
            userId = userId,
            paymentMethod = paymentMethod,
            amount = amount,
            referenceNumber = referenceNumber,
            notes = notes,
            paymentDate = DateTimeUtil.parseMillis(paymentDate) ?: System.currentTimeMillis(),
            createdAt = System.currentTimeMillis(),
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    // ==================== Expense DTO Mappers ====================

    fun ExpenseResponse.toEntity(): ExpenseEntity {
        return ExpenseEntity(
            id = id,
            shopId = shopId,
            title = title,
            description = description,
            category = category,
            amount = amount,
            expenseDate = DateTimeUtil.parseMillis(expenseDate) ?: System.currentTimeMillis(),
            paymentMethod = paymentMethod,
            receiptNumber = receiptNumber,
            recordedBy = recordedBy,
            createdAt = createdAt?.let { DateTimeUtil.parseMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    // ==================== List Converters ====================

    fun List<ShopResponse>.toShopEntityList(): List<ShopEntity> = map { it.toEntity() }
    fun List<ShopResponse>.toShopDomainList(): List<Shop> = map { it.toDomain() }
    fun List<ProductResponse>.toProductEntityList(): List<ProductEntity> = map { it.toEntity() }
    fun List<ProductResponse>.toProductDomainList(): List<Product> = map { it.toDomain() }
    fun List<CustomerResponse>.toCustomerEntityList(): List<CustomerEntity> = map { it.toEntity() }
    fun List<CustomerResponse>.toCustomerDomainList(): List<Customer> = map { it.toDomain() }
    fun List<CategoryResponse>.toCategoryEntityList(): List<CategoryEntity> = map { it.toEntity() }
    fun List<CategoryResponse>.toCategoryDomainList(): List<Category> = map { it.toDomain() }
    fun List<SaleResponse>.toSaleEntityList(): List<SaleEntity> = map { it.toEntity() }
}

