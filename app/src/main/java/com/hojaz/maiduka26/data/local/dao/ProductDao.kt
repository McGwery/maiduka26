package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ProductEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Product entity operations.
 */
@Dao
interface ProductDao : BaseDao<ProductEntity> {

    /**
     * Gets all products for a shop.
     */
    @Query("SELECT * FROM products WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY product_name ASC")
    fun getProductsByShop(shopId: String): Flow<List<ProductEntity>>

    /**
     * Gets a product by ID.
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): ProductEntity?

    /**
     * Gets a product by ID as Flow.
     */
    @Query("SELECT * FROM products WHERE id = :productId")
    fun getProductByIdFlow(productId: String): Flow<ProductEntity?>

    /**
     * Gets products by category.
     */
    @Query("SELECT * FROM products WHERE shop_id = :shopId AND category_id = :categoryId AND deleted_at IS NULL ORDER BY product_name ASC")
    fun getProductsByCategory(shopId: String, categoryId: String): Flow<List<ProductEntity>>

    /**
     * Gets a product by SKU.
     */
    @Query("SELECT * FROM products WHERE sku = :sku AND deleted_at IS NULL")
    suspend fun getProductBySku(sku: String): ProductEntity?

    /**
     * Gets a product by barcode.
     */
    @Query("SELECT * FROM products WHERE barcode = :barcode AND deleted_at IS NULL")
    suspend fun getProductByBarcode(barcode: String): ProductEntity?

    /**
     * Searches products by name, SKU, or barcode.
     */
    @Query("""
        SELECT * FROM products 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND (product_name LIKE '%' || :query || '%' 
            OR sku LIKE '%' || :query || '%' 
            OR barcode LIKE '%' || :query || '%')
        ORDER BY product_name ASC
    """)
    fun searchProducts(shopId: String, query: String): Flow<List<ProductEntity>>

    /**
     * Gets low stock products.
     */
    @Query("""
        SELECT * FROM products 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND track_inventory = 1
        AND current_stock IS NOT NULL 
        AND low_stock_threshold IS NOT NULL
        AND current_stock <= low_stock_threshold
        ORDER BY current_stock ASC
    """)
    fun getLowStockProducts(shopId: String): Flow<List<ProductEntity>>

    /**
     * Gets out of stock products.
     */
    @Query("""
        SELECT * FROM products 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND track_inventory = 1
        AND current_stock IS NOT NULL 
        AND current_stock <= 0
        ORDER BY product_name ASC
    """)
    fun getOutOfStockProducts(shopId: String): Flow<List<ProductEntity>>

    /**
     * Updates product stock.
     */
    @Query("UPDATE products SET current_stock = :newStock, updated_at = :updatedAt WHERE id = :productId")
    suspend fun updateStock(productId: String, newStock: Int, updatedAt: Long)

    /**
     * Gets products pending sync.
     */
    @Query("SELECT * FROM products WHERE sync_status = 'pending'")
    suspend fun getProductsPendingSync(): List<ProductEntity>

    /**
     * Updates sync status for a product.
     */
    @Query("UPDATE products SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :productId")
    suspend fun updateSyncStatus(productId: String, status: String, syncedAt: Long)

    /**
     * Soft deletes a product.
     */
    @Query("UPDATE products SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :productId")
    suspend fun softDelete(productId: String, deletedAt: Long)

    /**
     * Gets product count for a shop.
     */
    @Query("SELECT COUNT(*) FROM products WHERE shop_id = :shopId AND deleted_at IS NULL")
    suspend fun getProductCount(shopId: String): Int

    /**
     * Gets total inventory value for a shop.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(cost_per_unit AS REAL) * current_stock), 0) 
        FROM products 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND current_stock IS NOT NULL
    """)
    suspend fun getTotalInventoryValue(shopId: String): Double

    /**
     * Deletes all products.
     */
    @Query("DELETE FROM products")
    suspend fun deleteAllProducts()
}

