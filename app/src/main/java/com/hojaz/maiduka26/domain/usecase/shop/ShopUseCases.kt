package com.hojaz.maiduka26.domain.usecase.shop

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.domain.repository.ShopRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting all shops.
 */
class GetShopsUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<List<Shop>> {
        return shopRepository.getShops()
    }
}

/**
 * Use case for getting a shop by ID.
 */
class GetShopByIdUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shopId: String): Either<Throwable, Shop?> {
        return shopRepository.getShopById(shopId)
    }
}

/**
 * Use case for getting the active shop.
 */
class GetActiveShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(): Flow<Shop?> {
        return shopRepository.getActiveShop()
    }
}

/**
 * Use case for setting the active shop.
 */
class SetActiveShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shopId: String): Either<Throwable, Unit> {
        return shopRepository.setActiveShop(shopId)
    }
}

/**
 * Use case for creating a shop.
 */
class CreateShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shop: Shop): Either<Throwable, Shop> {
        return shopRepository.createShop(shop)
    }
}

/**
 * Use case for updating a shop.
 */
class UpdateShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shop: Shop): Either<Throwable, Shop> {
        return shopRepository.updateShop(shop)
    }
}

/**
 * Use case for deleting a shop.
 */
class DeleteShopUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    suspend operator fun invoke(shopId: String): Either<Throwable, Unit> {
        return shopRepository.deleteShop(shopId)
    }
}

/**
 * Use case for searching shops.
 */
class SearchShopsUseCase @Inject constructor(
    private val shopRepository: ShopRepository
) {
    operator fun invoke(query: String): Flow<List<Shop>> {
        return shopRepository.searchShops(query)
    }
}

