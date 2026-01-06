package com.hojaz.maiduka26.domain.usecase.product

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Product
import com.hojaz.maiduka26.domain.repository.ProductRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting products from a shop.
 */
class GetProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(shopId: String): Flow<List<Product>> {
        return productRepository.getProducts(shopId)
    }
}

/**
 * Use case for getting a product by ID.
 */
class GetProductByIdUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Either<Throwable, Product?> {
        return productRepository.getProductById(productId)
    }
}

/**
 * Use case for searching products.
 */
class SearchProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(shopId: String, query: String): Flow<List<Product>> {
        return productRepository.searchProducts(shopId, query)
    }
}

/**
 * Use case for getting products by barcode.
 */
class GetProductByBarcodeUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(barcode: String): Either<Throwable, Product?> {
        return productRepository.getProductByBarcode(barcode)
    }
}

/**
 * Use case for getting low stock products.
 */
class GetLowStockProductsUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    operator fun invoke(shopId: String): Flow<List<Product>> {
        return productRepository.getLowStockProducts(shopId)
    }
}

/**
 * Use case for creating a product.
 */
class CreateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Either<Throwable, Product> {
        return productRepository.createProduct(product)
    }
}

/**
 * Use case for updating a product.
 */
class UpdateProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(product: Product): Either<Throwable, Product> {
        return productRepository.updateProduct(product)
    }
}

/**
 * Use case for deleting a product.
 */
class DeleteProductUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String): Either<Throwable, Unit> {
        return productRepository.deleteProduct(productId)
    }
}

/**
 * Use case for updating product stock.
 */
class UpdateProductStockUseCase @Inject constructor(
    private val productRepository: ProductRepository
) {
    suspend operator fun invoke(productId: String, newStock: Int): Either<Throwable, Unit> {
        return productRepository.updateStock(productId, newStock)
    }
}

