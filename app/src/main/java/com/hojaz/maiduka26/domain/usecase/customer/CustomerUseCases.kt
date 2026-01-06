package com.hojaz.maiduka26.domain.usecase.customer

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Customer
import com.hojaz.maiduka26.domain.repository.CustomerRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for getting customers from a shop.
 */
class GetCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(shopId: String): Flow<List<Customer>> {
        return customerRepository.getCustomers(shopId)
    }
}

/**
 * Use case for getting a customer by ID.
 */
class GetCustomerByIdUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: String): Either<Throwable, Customer?> {
        return customerRepository.getCustomerById(customerId)
    }
}

/**
 * Use case for getting a customer by phone.
 */
class GetCustomerByPhoneUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(shopId: String, phone: String): Either<Throwable, Customer?> {
        return customerRepository.getCustomerByPhone(shopId, phone)
    }
}

/**
 * Use case for searching customers.
 */
class SearchCustomersUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(shopId: String, query: String): Flow<List<Customer>> {
        return customerRepository.searchCustomers(shopId, query)
    }
}

/**
 * Use case for getting customers with debt.
 */
class GetCustomersWithDebtUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    operator fun invoke(shopId: String): Flow<List<Customer>> {
        return customerRepository.getCustomersWithDebt(shopId)
    }
}

/**
 * Use case for getting total debt.
 */
class GetTotalDebtUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(shopId: String): Double {
        return customerRepository.getTotalDebt(shopId)
    }
}

/**
 * Use case for creating a customer.
 */
class CreateCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): Either<Throwable, Customer> {
        return customerRepository.createCustomer(customer)
    }
}

/**
 * Use case for updating a customer.
 */
class UpdateCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customer: Customer): Either<Throwable, Customer> {
        return customerRepository.updateCustomer(customer)
    }
}

/**
 * Use case for deleting a customer.
 */
class DeleteCustomerUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: String): Either<Throwable, Unit> {
        return customerRepository.deleteCustomer(customerId)
    }
}

/**
 * Use case for updating customer debt.
 */
class UpdateCustomerDebtUseCase @Inject constructor(
    private val customerRepository: CustomerRepository
) {
    suspend operator fun invoke(customerId: String, newDebt: Double): Either<Throwable, Unit> {
        return customerRepository.updateDebt(customerId, newDebt)
    }
}

