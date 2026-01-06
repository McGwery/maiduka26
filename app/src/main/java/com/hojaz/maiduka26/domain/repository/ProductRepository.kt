package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Product
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for product operations.
 */
interface ProductRepository {

    /**
     * Get all products for a shop.
     */
    fun getProducts(shopId: String): Flow<List<Product>>

    /**
     * Get a product by ID.
     */
    suspend fun getProductById(productId: String): Either<Throwable, Product?>

    /**
     * Get a product by ID as Flow.
     */
    fun getProductByIdFlow(productId: String): Flow<Product?>

    /**
     * Get products by category.
     */
    fun getProductsByCategory(shopId: String, categoryId: String): Flow<List<Product>>

    /**
     * Get a product by SKU.
     */
    suspend fun getProductBySku(sku: String): Either<Throwable, Product?>

    /**
     * Get a product by barcode.
     */
    suspend fun getProductByBarcode(barcode: String): Either<Throwable, Product?>

    /**
     * Search products.
     */
    fun searchProducts(shopId: String, query: String): Flow<List<Product>>

    /**
     * Get low stock products.
     */
    fun getLowStockProducts(shopId: String): Flow<List<Product>>

    /**
     * Get out of stock products.
     */
    fun getOutOfStockProducts(shopId: String): Flow<List<Product>>

    /**
     * Create a new product.
     */
    suspend fun createProduct(product: Product): Either<Throwable, Product>

    /**
     * Update an existing product.
     */
    suspend fun updateProduct(product: Product): Either<Throwable, Product>

    /**
     * Delete a product.
     */
    suspend fun deleteProduct(productId: String): Either<Throwable, Unit>

    /**
     * Update product stock.
     */
    suspend fun updateStock(productId: String, newStock: Int): Either<Throwable, Unit>

    /**
     * Get product count for a shop.
     */
    suspend fun getProductCount(shopId: String): Int

    /**
     * Get total inventory value for a shop.
     */
    suspend fun getTotalInventoryValue(shopId: String): Double

    /**
     * Sync products with remote server.
     */
    suspend fun syncProducts(shopId: String): Either<Throwable, Unit>
}

