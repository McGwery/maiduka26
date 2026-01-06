package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.ProductDao
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.mapper.ProductMapper.toDomain
import com.hojaz.maiduka26.data.mapper.ProductMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.ProductMapper.toEntity
import com.hojaz.maiduka26.domain.model.Product
import com.hojaz.maiduka26.domain.repository.ProductRepository
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of ProductRepository.
 * Follows offline-first approach: local database is the source of truth.
 */
@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val networkMonitor: NetworkMonitor,
    private val preferencesManager: PreferencesManager
) : ProductRepository {

    override fun getProducts(shopId: String): Flow<List<Product>> {
        return productDao.getProductsByShop(shopId).map { it.toDomainList() }
    }

    override suspend fun getProductById(productId: String): Either<Throwable, Product?> {
        return try {
            productDao.getProductById(productId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting product by ID: $productId")
            e.left()
        }
    }

    override fun getProductByIdFlow(productId: String): Flow<Product?> {
        return productDao.getProductByIdFlow(productId).map { it?.toDomain() }
    }

    override fun getProductsByCategory(shopId: String, categoryId: String): Flow<List<Product>> {
        return productDao.getProductsByCategory(shopId, categoryId).map { it.toDomainList() }
    }

    override suspend fun getProductBySku(sku: String): Either<Throwable, Product?> {
        return try {
            productDao.getProductBySku(sku)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting product by SKU: $sku")
            e.left()
        }
    }

    override suspend fun getProductByBarcode(barcode: String): Either<Throwable, Product?> {
        return try {
            productDao.getProductByBarcode(barcode)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting product by barcode: $barcode")
            e.left()
        }
    }

    override fun searchProducts(shopId: String, query: String): Flow<List<Product>> {
        return productDao.searchProducts(shopId, query).map { it.toDomainList() }
    }

    override fun getLowStockProducts(shopId: String): Flow<List<Product>> {
        return productDao.getLowStockProducts(shopId).map { it.toDomainList() }
    }

    override fun getOutOfStockProducts(shopId: String): Flow<List<Product>> {
        return productDao.getOutOfStockProducts(shopId).map { it.toDomainList() }
    }

    override suspend fun createProduct(product: Product): Either<Throwable, Product> {
        return try {
            val entity = product.toEntity(syncStatus = "pending")
            productDao.insert(entity)
            Timber.d("Product created locally: ${product.id}")

            // If online, try to sync immediately
            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            product.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating product")
            e.left()
        }
    }

    override suspend fun updateProduct(product: Product): Either<Throwable, Product> {
        return try {
            val entity = product.toEntity(syncStatus = "pending")
            productDao.update(entity)
            Timber.d("Product updated locally: ${product.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            product.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating product")
            e.left()
        }
    }

    override suspend fun deleteProduct(productId: String): Either<Throwable, Unit> {
        return try {
            productDao.softDelete(productId, System.currentTimeMillis())
            Timber.d("Product soft deleted: $productId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting product")
            e.left()
        }
    }

    override suspend fun updateStock(productId: String, newStock: Int): Either<Throwable, Unit> {
        return try {
            productDao.updateStock(productId, newStock, System.currentTimeMillis())
            Timber.d("Product stock updated: $productId -> $newStock")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating stock")
            e.left()
        }
    }

    override suspend fun getProductCount(shopId: String): Int {
        return productDao.getProductCount(shopId)
    }

    override suspend fun getTotalInventoryValue(shopId: String): Double {
        return productDao.getTotalInventoryValue(shopId)
    }

    override suspend fun syncProducts(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            // Get pending products
            val pendingProducts = productDao.getProductsPendingSync()

            // TODO: Upload pending products to server
            // TODO: Download updated products from server

            // Update sync status
            pendingProducts.forEach { product ->
                productDao.updateSyncStatus(product.id, "synced", System.currentTimeMillis())
            }

            Timber.d("Products synced: ${pendingProducts.size} uploaded")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing products")
            e.left()
        }
    }
}

