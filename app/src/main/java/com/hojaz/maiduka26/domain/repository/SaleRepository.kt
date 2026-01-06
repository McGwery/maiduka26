package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.Sale
import com.hojaz.maiduka26.domain.model.SaleItem
import com.hojaz.maiduka26.domain.model.SalePayment
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

/**
 * Repository interface for sale operations.
 */
interface SaleRepository {

    /**
     * Get all sales for a shop.
     */
    fun getSales(shopId: String): Flow<List<Sale>>

    /**
     * Get a sale by ID.
     */
    suspend fun getSaleById(saleId: String): Either<Throwable, Sale?>

    /**
     * Get a sale by ID as Flow.
     */
    fun getSaleByIdFlow(saleId: String): Flow<Sale?>

    /**
     * Get a sale by sale number.
     */
    suspend fun getSaleBySaleNumber(saleNumber: String): Either<Throwable, Sale?>

    /**
     * Get sales by customer.
     */
    fun getSalesByCustomer(shopId: String, customerId: String): Flow<List<Sale>>

    /**
     * Get sales by status.
     */
    fun getSalesByStatus(shopId: String, status: String): Flow<List<Sale>>

    /**
     * Get sales by date range.
     */
    fun getSalesByDateRange(shopId: String, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Sale>>

    /**
     * Get today's sales.
     */
    fun getTodaySales(shopId: String): Flow<List<Sale>>

    /**
     * Get today's total sales amount.
     */
    suspend fun getTodayTotalSales(shopId: String): Double

    /**
     * Get today's total profit.
     */
    suspend fun getTodayTotalProfit(shopId: String): Double

    /**
     * Get today's sales count.
     */
    suspend fun getTodaySalesCount(shopId: String): Int

    /**
     * Get sales with debt.
     */
    fun getSalesWithDebt(shopId: String): Flow<List<Sale>>

    /**
     * Create a new sale.
     */
    suspend fun createSale(
        sale: Sale,
        items: List<SaleItem>,
        payments: List<SalePayment>
    ): Either<Throwable, Sale>

    /**
     * Update an existing sale.
     */
    suspend fun updateSale(sale: Sale): Either<Throwable, Sale>

    /**
     * Cancel a sale.
     */
    suspend fun cancelSale(saleId: String): Either<Throwable, Unit>

    /**
     * Add payment to a sale.
     */
    suspend fun addPayment(saleId: String, payment: SalePayment): Either<Throwable, Unit>

    /**
     * Refund a sale.
     */
    suspend fun refundSale(saleId: String, amount: Double, reason: String): Either<Throwable, Unit>

    /**
     * Get sale items for a sale.
     */
    fun getSaleItems(saleId: String): Flow<List<SaleItem>>

    /**
     * Get payments for a sale.
     */
    fun getSalePayments(saleId: String): Flow<List<SalePayment>>

    /**
     * Sync sales with remote server.
     */
    suspend fun syncSales(shopId: String): Either<Throwable, Unit>
}

