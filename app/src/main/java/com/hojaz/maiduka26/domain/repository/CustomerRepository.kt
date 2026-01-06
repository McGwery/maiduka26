package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Customer
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for customer operations.
 */
interface CustomerRepository {

    /**
     * Get all customers for a shop.
     */
    fun getCustomers(shopId: String): Flow<List<Customer>>

    /**
     * Get a customer by ID.
     */
    suspend fun getCustomerById(customerId: String): Either<Throwable, Customer?>

    /**
     * Get a customer by ID as Flow.
     */
    fun getCustomerByIdFlow(customerId: String): Flow<Customer?>

    /**
     * Get a customer by phone.
     */
    suspend fun getCustomerByPhone(shopId: String, phone: String): Either<Throwable, Customer?>

    /**
     * Search customers.
     */
    fun searchCustomers(shopId: String, query: String): Flow<List<Customer>>

    /**
     * Get customers with debt.
     */
    fun getCustomersWithDebt(shopId: String): Flow<List<Customer>>

    /**
     * Get total debt for a shop.
     */
    suspend fun getTotalDebt(shopId: String): Double

    /**
     * Create a new customer.
     */
    suspend fun createCustomer(customer: Customer): Either<Throwable, Customer>

    /**
     * Update an existing customer.
     */
    suspend fun updateCustomer(customer: Customer): Either<Throwable, Customer>

    /**
     * Delete a customer.
     */
    suspend fun deleteCustomer(customerId: String): Either<Throwable, Unit>

    /**
     * Update customer debt.
     */
    suspend fun updateDebt(customerId: String, newDebt: Double): Either<Throwable, Unit>

    /**
     * Get customer count for a shop.
     */
    suspend fun getCustomerCount(shopId: String): Int

    /**
     * Sync customers with remote server.
     */
    suspend fun syncCustomers(shopId: String): Either<Throwable, Unit>
}

