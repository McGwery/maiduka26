package com.hojaz.maiduka26.domain.usecase.sale

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Sale
import com.hojaz.maiduka26.domain.model.SaleItem
import com.hojaz.maiduka26.domain.model.SalePayment
import com.hojaz.maiduka26.domain.repository.SaleRepository
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Use case for getting sales from a shop.
 */
class GetSalesUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(shopId: String): Flow<List<Sale>> {
        return saleRepository.getSales(shopId)
    }
}

/**
 * Use case for getting a sale by ID.
 */
class GetSaleByIdUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(saleId: String): Either<Throwable, Sale?> {
        return saleRepository.getSaleById(saleId)
    }
}

/**
 * Use case for getting today's sales.
 */
class GetTodaySalesUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(shopId: String): Flow<List<Sale>> {
        return saleRepository.getTodaySales(shopId)
    }
}

/**
 * Use case for getting today's sales summary.
 */
class GetTodaySalesSummaryUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(shopId: String): SalesSummary {
        return SalesSummary(
            totalSales = saleRepository.getTodayTotalSales(shopId),
            totalProfit = saleRepository.getTodayTotalProfit(shopId),
            salesCount = saleRepository.getTodaySalesCount(shopId)
        )
    }
}

data class SalesSummary(
    val totalSales: Double,
    val totalProfit: Double,
    val salesCount: Int
)

/**
 * Use case for getting sales by date range.
 */
class GetSalesByDateRangeUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(shopId: String, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Sale>> {
        return saleRepository.getSalesByDateRange(shopId, startDate, endDate)
    }
}

/**
 * Use case for getting sales by customer.
 */
class GetSalesByCustomerUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(shopId: String, customerId: String): Flow<List<Sale>> {
        return saleRepository.getSalesByCustomer(shopId, customerId)
    }
}

/**
 * Use case for getting sales with debt.
 */
class GetSalesWithDebtUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(shopId: String): Flow<List<Sale>> {
        return saleRepository.getSalesWithDebt(shopId)
    }
}

/**
 * Use case for creating a sale.
 */
class CreateSaleUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(
        sale: Sale,
        items: List<SaleItem>,
        payments: List<SalePayment>
    ): Either<Throwable, Sale> {
        return saleRepository.createSale(sale, items, payments)
    }
}

/**
 * Use case for adding a payment to a sale.
 */
class AddPaymentUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(saleId: String, payment: SalePayment): Either<Throwable, Unit> {
        return saleRepository.addPayment(saleId, payment)
    }
}

/**
 * Use case for refunding a sale.
 */
class RefundSaleUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(saleId: String, amount: Double, reason: String): Either<Throwable, Unit> {
        return saleRepository.refundSale(saleId, amount, reason)
    }
}

/**
 * Use case for cancelling a sale.
 */
class CancelSaleUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    suspend operator fun invoke(saleId: String): Either<Throwable, Unit> {
        return saleRepository.cancelSale(saleId)
    }
}

/**
 * Use case for getting sale items.
 */
class GetSaleItemsUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(saleId: String): Flow<List<SaleItem>> {
        return saleRepository.getSaleItems(saleId)
    }
}

/**
 * Use case for getting sale payments.
 */
class GetSalePaymentsUseCase @Inject constructor(
    private val saleRepository: SaleRepository
) {
    operator fun invoke(saleId: String): Flow<List<SalePayment>> {
        return saleRepository.getSalePayments(saleId)
    }
}

