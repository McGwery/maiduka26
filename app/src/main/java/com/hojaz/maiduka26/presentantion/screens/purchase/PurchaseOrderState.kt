package com.hojaz.maiduka26.presentantion.screens.purchase

import com.hojaz.maiduka26.domain.model.Product
import com.hojaz.maiduka26.domain.model.PurchaseOrder
import com.hojaz.maiduka26.domain.model.PurchaseOrderItem
import com.hojaz.maiduka26.domain.model.PurchaseOrderStatus
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.presentantion.base.ViewState
import java.math.BigDecimal

/**
 * State for the Purchase Order List screen.
 */
data class PurchaseOrderListState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeShop: Shop? = null,
    val purchaseOrdersAsBuyer: List<PurchaseOrder> = emptyList(),
    val purchaseOrdersAsSeller: List<PurchaseOrder> = emptyList(),
    val selectedTab: PurchaseTab = PurchaseTab.BUYING,
    val filterStatus: PurchaseOrderStatus? = null
) : ViewState

enum class PurchaseTab {
    BUYING, SELLING
}

/**
 * Events for the Purchase Order List screen.
 */
sealed class PurchaseOrderListEvent {
    data object LoadOrders : PurchaseOrderListEvent()
    data object Refresh : PurchaseOrderListEvent()
    data class SelectTab(val tab: PurchaseTab) : PurchaseOrderListEvent()
    data class FilterByStatus(val status: PurchaseOrderStatus?) : PurchaseOrderListEvent()
    data object NavigateToCreateOrder : PurchaseOrderListEvent()
    data class NavigateToOrderDetail(val orderId: String) : PurchaseOrderListEvent()
    data object NavigateBack : PurchaseOrderListEvent()
}

/**
 * Side effects for the Purchase Order List screen.
 */
sealed class PurchaseOrderListEffect {
    data object NavigateToCreateOrder : PurchaseOrderListEffect()
    data class NavigateToOrderDetail(val orderId: String) : PurchaseOrderListEffect()
    data object NavigateBack : PurchaseOrderListEffect()
    data class ShowSnackbar(val message: String) : PurchaseOrderListEffect()
}

// ==================== Create Purchase Order ====================

/**
 * State for the Create Purchase Order screen.
 */
data class CreatePurchaseOrderState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val buyerShop: Shop? = null,

    // Supplier selection
    val suppliers: List<Shop> = emptyList(),
    val selectedSupplier: Shop? = null,
    val supplierProducts: List<Product> = emptyList(),
    val searchQuery: String = "",

    // Order items
    val orderItems: List<PurchaseOrderItemDraft> = emptyList(),
    val notes: String = "",

    // Dialogs
    val showSupplierDialog: Boolean = false,
    val showProductDialog: Boolean = false,

    val isCreating: Boolean = false
) : ViewState {

    val filteredProducts: List<Product>
        get() = if (searchQuery.isBlank()) {
            supplierProducts
        } else {
            supplierProducts.filter {
                it.productName.contains(searchQuery, ignoreCase = true) ||
                it.sku?.contains(searchQuery, ignoreCase = true) == true
            }
        }

    val subtotal: BigDecimal
        get() = orderItems.fold(BigDecimal.ZERO) { acc, item ->
            acc.add(item.unitPrice.multiply(BigDecimal(item.quantity)))
        }

    val itemCount: Int
        get() = orderItems.sumOf { it.quantity }
}

/**
 * Draft item for purchase order creation.
 */
data class PurchaseOrderItemDraft(
    val product: Product,
    val quantity: Int = 1,
    val unitPrice: BigDecimal
) {
    val totalPrice: BigDecimal get() = unitPrice.multiply(BigDecimal(quantity))
}

/**
 * Events for the Create Purchase Order screen.
 */
sealed class CreatePurchaseOrderEvent {
    data object LoadData : CreatePurchaseOrderEvent()
    data class SearchQueryChanged(val query: String) : CreatePurchaseOrderEvent()
    data object ShowSupplierDialog : CreatePurchaseOrderEvent()
    data object HideSupplierDialog : CreatePurchaseOrderEvent()
    data class SelectSupplier(val supplier: Shop) : CreatePurchaseOrderEvent()
    data object ShowProductDialog : CreatePurchaseOrderEvent()
    data object HideProductDialog : CreatePurchaseOrderEvent()
    data class AddProduct(val product: Product, val quantity: Int, val price: BigDecimal) : CreatePurchaseOrderEvent()
    data class UpdateItemQuantity(val productId: String, val quantity: Int) : CreatePurchaseOrderEvent()
    data class RemoveItem(val productId: String) : CreatePurchaseOrderEvent()
    data class UpdateNotes(val notes: String) : CreatePurchaseOrderEvent()
    data object CreateOrder : CreatePurchaseOrderEvent()
    data object NavigateBack : CreatePurchaseOrderEvent()
}

/**
 * Side effects for the Create Purchase Order screen.
 */
sealed class CreatePurchaseOrderEffect {
    data object NavigateBack : CreatePurchaseOrderEffect()
    data class NavigateToOrderDetail(val orderId: String) : CreatePurchaseOrderEffect()
    data class ShowSnackbar(val message: String) : CreatePurchaseOrderEffect()
}

// ==================== Purchase Order Detail ====================

/**
 * State for the Purchase Order Detail screen.
 */
data class PurchaseOrderDetailState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val order: PurchaseOrder? = null,
    val items: List<PurchaseOrderItem> = emptyList(),
    val isCurrentShopBuyer: Boolean = true,

    // Actions
    val showApproveDialog: Boolean = false,
    val showRejectDialog: Boolean = false,
    val showPaymentDialog: Boolean = false,
    val rejectReason: String = "",
    val paymentAmount: String = "",
    val paymentMethod: String = "cash",

    val isProcessing: Boolean = false
) : ViewState

/**
 * Events for the Purchase Order Detail screen.
 */
sealed class PurchaseOrderDetailEvent {
    data class LoadOrder(val orderId: String) : PurchaseOrderDetailEvent()
    data object Refresh : PurchaseOrderDetailEvent()

    // Actions
    data object ShowApproveDialog : PurchaseOrderDetailEvent()
    data object HideApproveDialog : PurchaseOrderDetailEvent()
    data object ApproveOrder : PurchaseOrderDetailEvent()

    data object ShowRejectDialog : PurchaseOrderDetailEvent()
    data object HideRejectDialog : PurchaseOrderDetailEvent()
    data class UpdateRejectReason(val reason: String) : PurchaseOrderDetailEvent()
    data object RejectOrder : PurchaseOrderDetailEvent()

    data object ShowPaymentDialog : PurchaseOrderDetailEvent()
    data object HidePaymentDialog : PurchaseOrderDetailEvent()
    data class UpdatePaymentAmount(val amount: String) : PurchaseOrderDetailEvent()
    data class UpdatePaymentMethod(val method: String) : PurchaseOrderDetailEvent()
    data object AddPayment : PurchaseOrderDetailEvent()

    data object NavigateBack : PurchaseOrderDetailEvent()
}

/**
 * Side effects for the Purchase Order Detail screen.
 */
sealed class PurchaseOrderDetailEffect {
    data object NavigateBack : PurchaseOrderDetailEffect()
    data class ShowSnackbar(val message: String) : PurchaseOrderDetailEffect()
}

