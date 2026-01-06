package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.PurchaseOrder
import com.hojaz.maiduka26.domain.model.PurchaseOrderItem
import com.hojaz.maiduka26.domain.model.PurchasePayment
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for purchase order operations.
 */
interface PurchaseOrderRepository {

    /**
     * Get purchase orders where shop is buyer.
     */
    fun getPurchaseOrdersAsBuyer(shopId: String): Flow<List<PurchaseOrder>>

    /**
     * Get purchase orders where shop is seller.
     */
    fun getPurchaseOrdersAsSeller(shopId: String): Flow<List<PurchaseOrder>>

    /**
     * Get a purchase order by ID.
     */
    suspend fun getPurchaseOrderById(orderId: String): Either<Throwable, PurchaseOrder?>

    /**
     * Get purchase orders by status.
     */
    fun getPurchaseOrdersByStatus(shopId: String, status: String): Flow<List<PurchaseOrder>>

    /**
     * Create a new purchase order.
     */
    suspend fun createPurchaseOrder(
        order: PurchaseOrder,
        items: List<PurchaseOrderItem>
    ): Either<Throwable, PurchaseOrder>

    /**
     * Update purchase order status.
     */
    suspend fun updateStatus(orderId: String, status: String): Either<Throwable, Unit>

    /**
     * Approve a purchase order.
     */
    suspend fun approvePurchaseOrder(orderId: String, approvedBy: String): Either<Throwable, Unit>

    /**
     * Reject a purchase order.
     */
    suspend fun rejectPurchaseOrder(orderId: String, reason: String): Either<Throwable, Unit>

    /**
     * Add payment to purchase order.
     */
    suspend fun addPayment(orderId: String, payment: PurchasePayment): Either<Throwable, Unit>

    /**
     * Get items for a purchase order.
     */
    fun getOrderItems(orderId: String): Flow<List<PurchaseOrderItem>>

    /**
     * Get payments for a purchase order.
     */
    fun getOrderPayments(orderId: String): Flow<List<PurchasePayment>>

    /**
     * Sync purchase orders with remote server.
     */
    suspend fun syncPurchaseOrders(shopId: String): Either<Throwable, Unit>
}

