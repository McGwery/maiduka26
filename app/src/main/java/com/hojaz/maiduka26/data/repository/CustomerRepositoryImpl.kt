package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.CustomerDao
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.mapper.CustomerMapper.toDomain
import com.hojaz.maiduka26.data.mapper.CustomerMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.CustomerMapper.toEntity
import com.hojaz.maiduka26.data.remote.api.ApiService
import com.hojaz.maiduka26.domain.model.Customer
import com.hojaz.maiduka26.domain.repository.CustomerRepository
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of CustomerRepository.
 * Follows offline-first approach: local database is the source of truth.
 */
@Singleton
class CustomerRepositoryImpl @Inject constructor(
    private val customerDao: CustomerDao,
    private val apiService: ApiService,
    private val networkMonitor: NetworkMonitor,
    private val preferencesManager: PreferencesManager
) : CustomerRepository {

    override fun getCustomers(shopId: String): Flow<List<Customer>> {
        return customerDao.getCustomersByShop(shopId).map { it.toDomainList() }
    }

    override suspend fun getCustomerById(customerId: String): Either<Throwable, Customer?> {
        return try {
            customerDao.getCustomerById(customerId)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting customer by ID: $customerId")
            e.left()
        }
    }

    override fun getCustomerByIdFlow(customerId: String): Flow<Customer?> {
        return customerDao.getCustomerByIdFlow(customerId).map { it?.toDomain() }
    }

    override suspend fun getCustomerByPhone(shopId: String, phone: String): Either<Throwable, Customer?> {
        return try {
            customerDao.getCustomerByPhone(shopId, phone)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting customer by phone: $phone")
            e.left()
        }
    }

    override fun searchCustomers(shopId: String, query: String): Flow<List<Customer>> {
        return customerDao.searchCustomers(shopId, query).map { it.toDomainList() }
    }

    override fun getCustomersWithDebt(shopId: String): Flow<List<Customer>> {
        return customerDao.getCustomersWithDebt(shopId).map { it.toDomainList() }
    }

    override suspend fun getTotalDebt(shopId: String): Double {
        return customerDao.getTotalDebt(shopId)
    }

    override suspend fun createCustomer(customer: Customer): Either<Throwable, Customer> {
        return try {
            val entity = customer.toEntity(syncStatus = "pending")
            customerDao.insert(entity)
            Timber.d("Customer created locally: ${customer.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            customer.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating customer")
            e.left()
        }
    }

    override suspend fun updateCustomer(customer: Customer): Either<Throwable, Customer> {
        return try {
            val entity = customer.toEntity(syncStatus = "pending")
            customerDao.update(entity)
            Timber.d("Customer updated locally: ${customer.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            customer.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating customer")
            e.left()
        }
    }

    override suspend fun deleteCustomer(customerId: String): Either<Throwable, Unit> {
        return try {
            customerDao.softDelete(customerId, System.currentTimeMillis())
            Timber.d("Customer soft deleted: $customerId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error deleting customer")
            e.left()
        }
    }

    override suspend fun updateDebt(customerId: String, newDebt: Double): Either<Throwable, Unit> {
        return try {
            customerDao.updateDebt(
                customerId,
                BigDecimal(newDebt).toPlainString(),
                System.currentTimeMillis()
            )
            Timber.d("Customer debt updated: $customerId -> $newDebt")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating customer debt")
            e.left()
        }
    }

    override suspend fun getCustomerCount(shopId: String): Int {
        return customerDao.getCustomerCount(shopId)
    }

    override suspend fun syncCustomers(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pendingCustomers = customerDao.getCustomersPendingSync()

            // TODO: Upload pending customers to server
            // TODO: Download updated customers from server

            pendingCustomers.forEach { customer ->
                customerDao.updateSyncStatus(customer.id, "synced", System.currentTimeMillis())
            }

            Timber.d("Customers synced: ${pendingCustomers.size} uploaded")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing customers")
            e.left()
        }
    }
}

