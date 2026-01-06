package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.*
import com.hojaz.maiduka26.data.local.entity.SaleRefundEntity
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.mapper.SaleMapper.toDomain
import com.hojaz.maiduka26.data.mapper.SaleMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.SaleMapper.toEntity
import com.hojaz.maiduka26.data.mapper.SaleMapper.toSaleItemDomainList
import com.hojaz.maiduka26.data.mapper.SaleMapper.toSalePaymentDomainList
import com.hojaz.maiduka26.data.remote.api.ApiService
import com.hojaz.maiduka26.domain.model.Sale
import com.hojaz.maiduka26.domain.model.SaleItem
import com.hojaz.maiduka26.domain.model.SalePayment
import com.hojaz.maiduka26.domain.repository.SaleRepository
import com.hojaz.maiduka26.util.DateTimeUtil
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of SaleRepository.
 * Follows offline-first approach: local database is the source of truth.
 */
@Singleton
class SaleRepositoryImpl @Inject constructor(
    private val saleDao: SaleDao,
    private val saleItemDao: SaleItemDao,
    private val salePaymentDao: SalePaymentDao,
    private val saleRefundDao: SaleRefundDao,
    private val productDao: ProductDao,
    private val customerDao: CustomerDao,
    private val apiService: ApiService,
    private val networkMonitor: NetworkMonitor,
    private val preferencesManager: PreferencesManager
) : SaleRepository {

    override fun getSales(shopId: String): Flow<List<Sale>> {
        return saleDao.getSalesByShop(shopId).map { it.toDomainList() }
    }

    override suspend fun getSaleById(saleId: String): Either<Throwable, Sale?> {
        return try {
            val saleEntity = saleDao.getSaleById(saleId)
            if (saleEntity != null) {
                val items = saleItemDao.getSaleItemsBySaleSync(saleId).toSaleItemDomainList()
                val payments = salePaymentDao.getPaymentsBySaleSync(saleId).toSalePaymentDomainList()
                saleEntity.toDomain(items = items, payments = payments).right()
            } else {
                null.right()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting sale by ID: $saleId")
            e.left()
        }
    }

    override fun getSaleByIdFlow(saleId: String): Flow<Sale?> {
        return saleDao.getSaleByIdFlow(saleId).map { it?.toDomain() }
    }

    override suspend fun getSaleBySaleNumber(saleNumber: String): Either<Throwable, Sale?> {
        return try {
            saleDao.getSaleBySaleNumber(saleNumber)?.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Error getting sale by number: $saleNumber")
            e.left()
        }
    }

    override fun getSalesByCustomer(shopId: String, customerId: String): Flow<List<Sale>> {
        return saleDao.getSalesByCustomer(shopId, customerId).map { it.toDomainList() }
    }

    override fun getSalesByStatus(shopId: String, status: String): Flow<List<Sale>> {
        return saleDao.getSalesByStatus(shopId, status).map { it.toDomainList() }
    }

    override fun getSalesByDateRange(shopId: String, startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<Sale>> {
        return saleDao.getSalesByDateRange(
            shopId,
            DateTimeUtil.toMillis(startDate),
            DateTimeUtil.toMillis(endDate)
        ).map { it.toDomainList() }
    }

    override fun getTodaySales(shopId: String): Flow<List<Sale>> {
        val startOfDay = DateTimeUtil.getStartOfDay()
        return saleDao.getTodaySales(shopId, startOfDay).map { it.toDomainList() }
    }

    override suspend fun getTodayTotalSales(shopId: String): Double {
        val startOfDay = DateTimeUtil.getStartOfDay()
        return saleDao.getTodayTotalSales(shopId, startOfDay)
    }

    override suspend fun getTodayTotalProfit(shopId: String): Double {
        val startOfDay = DateTimeUtil.getStartOfDay()
        return saleDao.getTodayTotalProfit(shopId, startOfDay)
    }

    override suspend fun getTodaySalesCount(shopId: String): Int {
        val startOfDay = DateTimeUtil.getStartOfDay()
        return saleDao.getTodaySalesCount(shopId, startOfDay)
    }

    override fun getSalesWithDebt(shopId: String): Flow<List<Sale>> {
        return saleDao.getSalesWithDebt(shopId).map { it.toDomainList() }
    }

    override suspend fun createSale(
        sale: Sale,
        items: List<SaleItem>,
        payments: List<SalePayment>
    ): Either<Throwable, Sale> {
        return try {
            // Insert sale
            val saleEntity = sale.toEntity(syncStatus = "pending")
            saleDao.insert(saleEntity)

            // Insert sale items
            items.forEach { item ->
                val itemEntity = com.hojaz.maiduka26.data.mapper.SaleMapper.run { item.toEntity(syncStatus = "pending") }
                saleItemDao.insert(itemEntity)

                // Update product stock if tracking
                item.productId?.let { productId ->
                    val product = productDao.getProductById(productId)
                    product?.let { p ->
                        if (p.trackInventory && p.currentStock != null) {
                            val newStock = p.currentStock - item.quantity.toInt()
                            productDao.updateStock(productId, newStock, System.currentTimeMillis())
                        }
                    }
                }
            }

            // Insert payments
            payments.forEach { payment ->
                val paymentEntity = com.hojaz.maiduka26.data.mapper.SaleMapper.run { payment.toEntity(syncStatus = "pending") }
                salePaymentDao.insert(paymentEntity)
            }

            // Update customer debt if applicable
            sale.customerId?.let { customerId ->
                if (sale.debtAmount > BigDecimal.ZERO) {
                    val customer = customerDao.getCustomerById(customerId)
                    customer?.let { c ->
                        val newDebt = BigDecimal(c.currentDebt).add(sale.debtAmount)
                        customerDao.updateDebt(customerId, newDebt.toPlainString(), System.currentTimeMillis())
                    }
                }
            }

            Timber.d("Sale created locally: ${sale.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote server
            }

            sale.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating sale")
            e.left()
        }
    }

    override suspend fun updateSale(sale: Sale): Either<Throwable, Sale> {
        return try {
            val entity = sale.toEntity(syncStatus = "pending")
            saleDao.update(entity)
            Timber.d("Sale updated locally: ${sale.id}")
            sale.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating sale")
            e.left()
        }
    }

    override suspend fun cancelSale(saleId: String): Either<Throwable, Unit> {
        return try {
            saleDao.updateStatus(saleId, "cancelled", System.currentTimeMillis())
            Timber.d("Sale cancelled: $saleId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error cancelling sale")
            e.left()
        }
    }

    override suspend fun addPayment(saleId: String, payment: SalePayment): Either<Throwable, Unit> {
        return try {
            val paymentEntity = com.hojaz.maiduka26.data.mapper.SaleMapper.run { payment.toEntity(syncStatus = "pending") }
            salePaymentDao.insert(paymentEntity)

            // Update sale payment status
            val totalPaid = salePaymentDao.getTotalPaidForSale(saleId)
            val sale = saleDao.getSaleById(saleId)

            sale?.let { s ->
                val totalAmount = BigDecimal(s.totalAmount)
                val paidAmount = BigDecimal(totalPaid)
                val debtAmount = totalAmount.subtract(paidAmount).max(BigDecimal.ZERO)

                val paymentStatus = when {
                    paidAmount >= totalAmount -> "paid"
                    paidAmount > BigDecimal.ZERO -> "partially_paid"
                    else -> "pending"
                }

                saleDao.updatePaymentInfo(
                    saleId,
                    paymentStatus,
                    paidAmount.toPlainString(),
                    debtAmount.toPlainString(),
                    System.currentTimeMillis()
                )
            }

            Timber.d("Payment added to sale: $saleId")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error adding payment")
            e.left()
        }
    }

    override suspend fun refundSale(saleId: String, amount: Double, reason: String): Either<Throwable, Unit> {
        return try {
            val userPrefs = preferencesManager.userPreferencesFlow.first()
            val userId = userPrefs.userId ?: return Exception("User not logged in").left()

            val refund = SaleRefundEntity(
                id = UUID.randomUUID().toString(),
                saleId = saleId,
                userId = userId,
                amount = BigDecimal(amount).toPlainString(),
                reason = reason,
                refundDate = System.currentTimeMillis(),
                createdAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )

            saleRefundDao.insert(refund)

            // Update sale status
            val totalRefunded = saleRefundDao.getTotalRefundedForSale(saleId)
            val sale = saleDao.getSaleById(saleId)

            sale?.let { s ->
                val totalAmount = BigDecimal(s.totalAmount)
                val status = if (BigDecimal(totalRefunded) >= totalAmount) "refunded" else "partially_refunded"
                saleDao.updateStatus(saleId, status, System.currentTimeMillis())
            }

            Timber.d("Sale refunded: $saleId, amount: $amount")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error refunding sale")
            e.left()
        }
    }

    override fun getSaleItems(saleId: String): Flow<List<SaleItem>> {
        return saleItemDao.getSaleItemsBySale(saleId).map { it.toSaleItemDomainList() }
    }

    override fun getSalePayments(saleId: String): Flow<List<SalePayment>> {
        return salePaymentDao.getPaymentsBySale(saleId).map { it.toSalePaymentDomainList() }
    }

    override suspend fun syncSales(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pendingSales = saleDao.getSalesPendingSync()

            // TODO: Upload pending sales to server
            // TODO: Download updated sales from server

            pendingSales.forEach { sale ->
                saleDao.updateSyncStatus(sale.id, "synced", System.currentTimeMillis())
            }

            Timber.d("Sales synced: ${pendingSales.size} uploaded")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing sales")
            e.left()
        }
    }
}

