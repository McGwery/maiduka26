package com.hojaz.maiduka26.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import com.hojaz.maiduka26.data.local.dao.base.BaseDao
import com.hojaz.maiduka26.data.local.entity.CustomerEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Customer entity operations.
 */
@Dao
interface CustomerDao : BaseDao<CustomerEntity> {

    /**
     * Gets all customers for a shop.
     */
    @Query("SELECT * FROM customers WHERE shop_id = :shopId AND deleted_at IS NULL ORDER BY name ASC")
    fun getCustomersByShop(shopId: String): Flow<List<CustomerEntity>>

    /**
     * Gets a customer by ID.
     */
    @Query("SELECT * FROM customers WHERE id = :customerId")
    suspend fun getCustomerById(customerId: String): CustomerEntity?

    /**
     * Gets a customer by ID as Flow.
     */
    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun getCustomerByIdFlow(customerId: String): Flow<CustomerEntity?>

    /**
     * Gets a customer by phone.
     */
    @Query("SELECT * FROM customers WHERE shop_id = :shopId AND phone = :phone AND deleted_at IS NULL")
    suspend fun getCustomerByPhone(shopId: String, phone: String): CustomerEntity?

    /**
     * Searches customers by name or phone.
     */
    @Query("""
        SELECT * FROM customers 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND (name LIKE '%' || :query || '%' OR phone LIKE '%' || :query || '%')
        ORDER BY name ASC
    """)
    fun searchCustomers(shopId: String, query: String): Flow<List<CustomerEntity>>

    /**
     * Gets customers with outstanding debt.
     */
    @Query("""
        SELECT * FROM customers 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL 
        AND CAST(current_debt AS REAL) > 0
        ORDER BY CAST(current_debt AS REAL) DESC
    """)
    fun getCustomersWithDebt(shopId: String): Flow<List<CustomerEntity>>

    /**
     * Gets total debt for a shop.
     */
    @Query("""
        SELECT COALESCE(SUM(CAST(current_debt AS REAL)), 0) 
        FROM customers 
        WHERE shop_id = :shopId 
        AND deleted_at IS NULL
    """)
    suspend fun getTotalDebt(shopId: String): Double

    /**
     * Updates customer debt.
     */
    @Query("UPDATE customers SET current_debt = :newDebt, updated_at = :updatedAt WHERE id = :customerId")
    suspend fun updateDebt(customerId: String, newDebt: String, updatedAt: Long)

    /**
     * Updates customer totals after a sale.
     */
    @Query("""
        UPDATE customers SET 
        total_purchases = :totalPurchases,
        total_paid = :totalPaid,
        current_debt = :currentDebt,
        updated_at = :updatedAt 
        WHERE id = :customerId
    """)
    suspend fun updateCustomerTotals(
        customerId: String,
        totalPurchases: String,
        totalPaid: String,
        currentDebt: String,
        updatedAt: Long
    )

    /**
     * Gets customers pending sync.
     */
    @Query("SELECT * FROM customers WHERE sync_status = 'pending'")
    suspend fun getCustomersPendingSync(): List<CustomerEntity>

    /**
     * Updates sync status for a customer.
     */
    @Query("UPDATE customers SET sync_status = :status, last_synced_at = :syncedAt WHERE id = :customerId")
    suspend fun updateSyncStatus(customerId: String, status: String, syncedAt: Long)

    /**
     * Soft deletes a customer.
     */
    @Query("UPDATE customers SET deleted_at = :deletedAt, updated_at = :deletedAt WHERE id = :customerId")
    suspend fun softDelete(customerId: String, deletedAt: Long)

    /**
     * Gets customer count for a shop.
     */
    @Query("SELECT COUNT(*) FROM customers WHERE shop_id = :shopId AND deleted_at IS NULL")
    suspend fun getCustomerCount(shopId: String): Int

    /**
     * Deletes all customers.
     */
    @Query("DELETE FROM customers")
    suspend fun deleteAllCustomers()
}

