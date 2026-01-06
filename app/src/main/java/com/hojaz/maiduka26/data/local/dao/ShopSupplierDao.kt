package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.ShopSupplierEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ShopSupplier entity operations.
 */
@Dao
interface ShopSupplierDao : BaseDao<ShopSupplierEntity> {

    /**
     * Gets all suppliers for a shop.
     */
    @Query("SELECT * FROM shop_suppliers WHERE shop_id = :shopId")
    fun getSuppliersForShop(shopId: String): Flow<List<ShopSupplierEntity>>

    /**
     * Gets all shops supplied by a supplier.
     */
    @Query("SELECT * FROM shop_suppliers WHERE supplier_id = :supplierId")
    fun getShopsForSupplier(supplierId: String): Flow<List<ShopSupplierEntity>>

    /**
     * Checks if a supplier relationship exists.
     */
    @Query("SELECT * FROM shop_suppliers WHERE shop_id = :shopId AND supplier_id = :supplierId")
    suspend fun getSupplierRelationship(shopId: String, supplierId: String): ShopSupplierEntity?

    /**
     * Removes a supplier relationship.
     */
    @Query("DELETE FROM shop_suppliers WHERE shop_id = :shopId AND supplier_id = :supplierId")
    suspend fun removeSupplier(shopId: String, supplierId: String)

    /**
     * Gets suppliers pending sync.
     */
    @Query("SELECT * FROM shop_suppliers WHERE sync_status = 'pending'")
    suspend fun getSuppliersPendingSync(): List<ShopSupplierEntity>

    /**
     * Updates sync status.
     */
    @Query("UPDATE shop_suppliers SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :id")
    suspend fun updateSyncStatus(id: Long, status: String, syncedAt: Long)

    /**
     * Deletes all suppliers.
     */
    @Query("DELETE FROM shop_suppliers")
    suspend fun deleteAllSuppliers()
}

