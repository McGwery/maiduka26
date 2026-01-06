package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.PurchaseOrderDao
import com.hojaz.maiduka26.data.local.dao.PurchaseOrderItemDao
import com.hojaz.maiduka26.data.local.dao.PurchasePaymentDao
import com.hojaz.maiduka26.data.local.dao.ProductDao
import com.hojaz.maiduka26.data.mapper.PurchaseOrderMapper.toDomain
import com.hojaz.maiduka26.data.mapper.PurchaseOrderMapper.toDomainList
import com.hojaz.maiduka26.data.mapper.PurchaseOrderMapper.toEntity
import com.hojaz.maiduka26.data.mapper.PurchaseOrderMapper.toItemDomainList
import com.hojaz.maiduka26.data.mapper.PurchaseOrderMapper.toPaymentDomainList
import com.hojaz.maiduka26.domain.model.PurchaseOrder
import com.hojaz.maiduka26.domain.model.PurchaseOrderItem
import com.hojaz.maiduka26.domain.model.PurchasePayment
import com.hojaz.maiduka26.domain.repository.PurchaseOrderRepository
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.math.BigDecimal
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of PurchaseOrderRepository.
 */
@Singleton
class PurchaseOrderRepositoryImpl @Inject constructor(
    private val purchaseOrderDao: PurchaseOrderDao,
    private val purchaseOrderItemDao: PurchaseOrderItemDao,
    private val purchasePaymentDao: PurchasePaymentDao,
    private val productDao: ProductDao,
    private val networkMonitor: NetworkMonitor
) : PurchaseOrderRepository {

    override fun getPurchaseOrdersAsBuyer(shopId: String): Flow<List<PurchaseOrder>> {
        return purchaseOrderDao.getPurchaseOrdersAsBuyer(shopId).map { it.toDomainList() }
    }

    override fun getPurchaseOrdersAsSeller(shopId: String): Flow<List<PurchaseOrder>> {
        return purchaseOrderDao.getPurchaseOrdersAsSeller(shopId).map { it.toDomainList() }
    }

    override suspend fun getPurchaseOrderById(orderId: String): Either<Throwable, PurchaseOrder?> {
        return try {
            val order = purchaseOrderDao.getPurchaseOrderById(orderId)
            if (order != null) {
                val items = purchaseOrderItemDao.getItemsForOrderSync(orderId).toItemDomainList()
                val payments = purchasePaymentDao.getPaymentsForOrderSync(orderId).toPaymentDomainList()
                order.toDomain(items = items, payments = payments).right()
            } else {
                null.right()
            }
        } catch (e: Exception) {
            Timber.e(e, "Error getting purchase order: $orderId")
            e.left()
        }
    }

    override fun getPurchaseOrdersByStatus(shopId: String, status: String): Flow<List<PurchaseOrder>> {
        return purchaseOrderDao.getPurchaseOrdersByStatus(shopId, status).map { it.toDomainList() }
    }

    override suspend fun createPurchaseOrder(
        order: PurchaseOrder,
        items: List<PurchaseOrderItem>
    ): Either<Throwable, PurchaseOrder> {
        return try {
            val orderEntity = order.toEntity(syncStatus = "pending")
            purchaseOrderDao.insert(orderEntity)

            items.forEach { item ->
                val itemEntity = com.hojaz.maiduka26.data.mapper.PurchaseOrderMapper.run {
                    item.toEntity(syncStatus = "pending")
                }
                purchaseOrderItemDao.insert(itemEntity)
            }

            Timber.d("Purchase order created: ${order.id}")

            if (networkMonitor.isOnline) {
                // TODO: Sync with remote
            }

            order.right()
        } catch (e: Exception) {
            Timber.e(e, "Error creating purchase order")
            e.left()
        }
    }

    override suspend fun updateStatus(orderId: String, status: String): Either<Throwable, Unit> {
        return try {
            purchaseOrderDao.updateStatus(orderId, status, System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error updating status")
            e.left()
        }
    }

    override suspend fun approvePurchaseOrder(orderId: String, approvedBy: String): Either<Throwable, Unit> {
        return try {
            purchaseOrderDao.approvePurchaseOrder(orderId, approvedBy, System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error approving order")
            e.left()
        }
    }

    override suspend fun rejectPurchaseOrder(orderId: String, reason: String): Either<Throwable, Unit> {
        return try {
            purchaseOrderDao.updateStatus(orderId, "rejected", System.currentTimeMillis())
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error rejecting order")
            e.left()
        }
    }

    override suspend fun addPayment(orderId: String, payment: PurchasePayment): Either<Throwable, Unit> {
        return try {
            val entity = com.hojaz.maiduka26.data.mapper.PurchaseOrderMapper.run {
                payment.toEntity(syncStatus = "pending")
            }
            purchasePaymentDao.insert(entity)

            // Update order total paid
            val totalPaid = purchasePaymentDao.getTotalPaidForOrder(orderId)
            val order = purchaseOrderDao.getPurchaseOrderById(orderId)

            order?.let {
                val totalAmount = BigDecimal(it.totalAmount)
                val newStatus = if (BigDecimal(totalPaid) >= totalAmount) "completed" else it.status
                purchaseOrderDao.updateStatus(orderId, newStatus, System.currentTimeMillis())
            }

            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error adding payment")
            e.left()
        }
    }

    override fun getOrderItems(orderId: String): Flow<List<PurchaseOrderItem>> {
        return purchaseOrderItemDao.getItemsForOrder(orderId).map { it.toItemDomainList() }
    }

    override fun getOrderPayments(orderId: String): Flow<List<PurchasePayment>> {
        return purchasePaymentDao.getPaymentsForOrder(orderId).map { it.toPaymentDomainList() }
    }

    override suspend fun syncPurchaseOrders(shopId: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("Device is offline").left()
            }

            val pending = purchaseOrderDao.getPurchaseOrdersPendingSync()
            pending.forEach {
                purchaseOrderDao.updateSyncStatus(it.id, "synced", System.currentTimeMillis())
            }

            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Error syncing purchase orders")
            e.left()
        }
    }
}

