package com.hojaz.maiduka26.presentantion.screens.purchase

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.model.PurchaseOrder
import com.hojaz.maiduka26.domain.model.PurchaseOrderItem
import com.hojaz.maiduka26.domain.model.PurchaseOrderStatus
import com.hojaz.maiduka26.domain.model.PurchasePayment
import com.hojaz.maiduka26.domain.repository.PurchaseOrderRepository
import com.hojaz.maiduka26.domain.usecase.shop.GetActiveShopUseCase
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the Purchase Order List screen.
 */
@HiltViewModel
class PurchaseOrderListViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val purchaseOrderRepository: PurchaseOrderRepository
) : BaseViewModel<PurchaseOrderListState, PurchaseOrderListEvent, PurchaseOrderListEffect>() {

    override fun createInitialState(): PurchaseOrderListState = PurchaseOrderListState()

    init {
        loadOrders()
    }

    override fun onEvent(event: PurchaseOrderListEvent) {
        when (event) {
            is PurchaseOrderListEvent.LoadOrders -> loadOrders()
            is PurchaseOrderListEvent.Refresh -> loadOrders()
            is PurchaseOrderListEvent.SelectTab -> setState { copy(selectedTab = event.tab) }
            is PurchaseOrderListEvent.FilterByStatus -> setState { copy(filterStatus = event.status) }
            is PurchaseOrderListEvent.NavigateToCreateOrder -> setEffect(PurchaseOrderListEffect.NavigateToCreateOrder)
            is PurchaseOrderListEvent.NavigateToOrderDetail -> setEffect(PurchaseOrderListEffect.NavigateToOrderDetail(event.orderId))
            is PurchaseOrderListEvent.NavigateBack -> setEffect(PurchaseOrderListEffect.NavigateBack)
        }
    }

    private fun loadOrders() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val shop = getActiveShopUseCase().first()
                if (shop == null) {
                    setState { copy(isLoading = false, error = "No shop selected") }
                    return@launch
                }

                setState { copy(activeShop = shop) }

                // Load orders as buyer
                launch {
                    purchaseOrderRepository.getPurchaseOrdersAsBuyer(shop.id).collectLatest { orders ->
                        setState { copy(purchaseOrdersAsBuyer = orders) }
                    }
                }

                // Load orders as seller
                launch {
                    purchaseOrderRepository.getPurchaseOrdersAsSeller(shop.id).collectLatest { orders ->
                        setState { copy(purchaseOrdersAsSeller = orders) }
                    }
                }

                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }
}

/**
 * ViewModel for the Create Purchase Order screen.
 */
@HiltViewModel
class CreatePurchaseOrderViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val purchaseOrderRepository: PurchaseOrderRepository
) : BaseViewModel<CreatePurchaseOrderState, CreatePurchaseOrderEvent, CreatePurchaseOrderEffect>() {

    override fun createInitialState(): CreatePurchaseOrderState = CreatePurchaseOrderState()

    init {
        loadData()
    }

    override fun onEvent(event: CreatePurchaseOrderEvent) {
        when (event) {
            is CreatePurchaseOrderEvent.LoadData -> loadData()
            is CreatePurchaseOrderEvent.SearchQueryChanged -> setState { copy(searchQuery = event.query) }
            is CreatePurchaseOrderEvent.ShowSupplierDialog -> setState { copy(showSupplierDialog = true) }
            is CreatePurchaseOrderEvent.HideSupplierDialog -> setState { copy(showSupplierDialog = false) }
            is CreatePurchaseOrderEvent.SelectSupplier -> selectSupplier(event.supplier)
            is CreatePurchaseOrderEvent.ShowProductDialog -> setState { copy(showProductDialog = true) }
            is CreatePurchaseOrderEvent.HideProductDialog -> setState { copy(showProductDialog = false) }
            is CreatePurchaseOrderEvent.AddProduct -> addProduct(event.product, event.quantity, event.price)
            is CreatePurchaseOrderEvent.UpdateItemQuantity -> updateItemQuantity(event.productId, event.quantity)
            is CreatePurchaseOrderEvent.RemoveItem -> removeItem(event.productId)
            is CreatePurchaseOrderEvent.UpdateNotes -> setState { copy(notes = event.notes) }
            is CreatePurchaseOrderEvent.CreateOrder -> createOrder()
            is CreatePurchaseOrderEvent.NavigateBack -> setEffect(CreatePurchaseOrderEffect.NavigateBack)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            try {
                val shop = getActiveShopUseCase().first()
                setState { copy(buyerShop = shop, isLoading = false) }

                // TODO: Load suppliers from ShopSupplierRepository
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun selectSupplier(supplier: com.hojaz.maiduka26.domain.model.Shop) {
        setState {
            copy(
                selectedSupplier = supplier,
                showSupplierDialog = false,
                orderItems = emptyList() // Clear items when changing supplier
            )
        }
        // TODO: Load supplier's products
    }

    private fun addProduct(product: com.hojaz.maiduka26.domain.model.Product, quantity: Int, price: BigDecimal) {
        val existingItem = currentState.orderItems.find { it.product.id == product.id }

        if (existingItem != null) {
            val updatedItems = currentState.orderItems.map {
                if (it.product.id == product.id) {
                    it.copy(quantity = it.quantity + quantity)
                } else it
            }
            setState { copy(orderItems = updatedItems, showProductDialog = false) }
        } else {
            val newItem = PurchaseOrderItemDraft(
                product = product,
                quantity = quantity,
                unitPrice = price
            )
            setState { copy(orderItems = orderItems + newItem, showProductDialog = false) }
        }
    }

    private fun updateItemQuantity(productId: String, quantity: Int) {
        if (quantity <= 0) {
            removeItem(productId)
            return
        }

        val updatedItems = currentState.orderItems.map {
            if (it.product.id == productId) it.copy(quantity = quantity) else it
        }
        setState { copy(orderItems = updatedItems) }
    }

    private fun removeItem(productId: String) {
        val updatedItems = currentState.orderItems.filter { it.product.id != productId }
        setState { copy(orderItems = updatedItems) }
    }

    private fun createOrder() {
        val supplier = currentState.selectedSupplier
        val buyer = currentState.buyerShop

        if (supplier == null) {
            setEffect(CreatePurchaseOrderEffect.ShowSnackbar("Please select a supplier"))
            return
        }

        if (buyer == null) {
            setEffect(CreatePurchaseOrderEffect.ShowSnackbar("No shop selected"))
            return
        }

        if (currentState.orderItems.isEmpty()) {
            setEffect(CreatePurchaseOrderEffect.ShowSnackbar("Please add items to the order"))
            return
        }

        viewModelScope.launch {
            setState { copy(isCreating = true) }

            try {
                val orderId = UUID.randomUUID().toString()
                val referenceNumber = "PO${System.currentTimeMillis()}"

                val order = PurchaseOrder(
                    id = orderId,
                    buyerShopId = buyer.id,
                    sellerShopId = supplier.id,
                    isInternal = false,
                    referenceNumber = referenceNumber,
                    status = PurchaseOrderStatus.PENDING,
                    totalAmount = currentState.subtotal,
                    totalPaid = BigDecimal.ZERO,
                    notes = currentState.notes.takeIf { it.isNotBlank() },
                    createdAt = LocalDateTime.now()
                )

                val items = currentState.orderItems.map { draft ->
                    PurchaseOrderItem(
                        id = UUID.randomUUID().toString(),
                        purchaseOrderId = orderId,
                        productId = draft.product.id,
                        quantity = draft.quantity,
                        unitPrice = draft.unitPrice,
                        totalPrice = draft.totalPrice
                    )
                }

                purchaseOrderRepository.createPurchaseOrder(order, items).fold(
                    ifLeft = { error ->
                        setState { copy(isCreating = false) }
                        setEffect(CreatePurchaseOrderEffect.ShowSnackbar(error.message ?: "Failed to create order"))
                    },
                    ifRight = { createdOrder ->
                        setState { copy(isCreating = false) }
                        setEffect(CreatePurchaseOrderEffect.ShowSnackbar("Order created successfully"))
                        setEffect(CreatePurchaseOrderEffect.NavigateToOrderDetail(createdOrder.id))
                    }
                )
            } catch (e: Exception) {
                setState { copy(isCreating = false) }
                setEffect(CreatePurchaseOrderEffect.ShowSnackbar(e.message ?: "Failed to create order"))
            }
        }
    }
}

/**
 * ViewModel for the Purchase Order Detail screen.
 */
@HiltViewModel
class PurchaseOrderDetailViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val purchaseOrderRepository: PurchaseOrderRepository,
    private val preferencesManager: PreferencesManager
) : BaseViewModel<PurchaseOrderDetailState, PurchaseOrderDetailEvent, PurchaseOrderDetailEffect>() {

    override fun createInitialState(): PurchaseOrderDetailState = PurchaseOrderDetailState()

    override fun onEvent(event: PurchaseOrderDetailEvent) {
        when (event) {
            is PurchaseOrderDetailEvent.LoadOrder -> loadOrder(event.orderId)
            is PurchaseOrderDetailEvent.Refresh -> currentState.order?.let { loadOrder(it.id) }

            is PurchaseOrderDetailEvent.ShowApproveDialog -> setState { copy(showApproveDialog = true) }
            is PurchaseOrderDetailEvent.HideApproveDialog -> setState { copy(showApproveDialog = false) }
            is PurchaseOrderDetailEvent.ApproveOrder -> approveOrder()

            is PurchaseOrderDetailEvent.ShowRejectDialog -> setState { copy(showRejectDialog = true) }
            is PurchaseOrderDetailEvent.HideRejectDialog -> setState { copy(showRejectDialog = false, rejectReason = "") }
            is PurchaseOrderDetailEvent.UpdateRejectReason -> setState { copy(rejectReason = event.reason) }
            is PurchaseOrderDetailEvent.RejectOrder -> rejectOrder()

            is PurchaseOrderDetailEvent.ShowPaymentDialog -> setState { copy(showPaymentDialog = true) }
            is PurchaseOrderDetailEvent.HidePaymentDialog -> setState { copy(showPaymentDialog = false, paymentAmount = "") }
            is PurchaseOrderDetailEvent.UpdatePaymentAmount -> setState { copy(paymentAmount = event.amount) }
            is PurchaseOrderDetailEvent.UpdatePaymentMethod -> setState { copy(paymentMethod = event.method) }
            is PurchaseOrderDetailEvent.AddPayment -> addPayment()

            is PurchaseOrderDetailEvent.NavigateBack -> setEffect(PurchaseOrderDetailEffect.NavigateBack)
        }
    }

    private fun loadOrder(orderId: String) {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val shop = getActiveShopUseCase().first()

                purchaseOrderRepository.getPurchaseOrderById(orderId).fold(
                    ifLeft = { error ->
                        setState { copy(isLoading = false, error = error.message) }
                    },
                    ifRight = { order ->
                        val isCurrentShopBuyer = order?.buyerShopId == shop?.id
                        setState {
                            copy(
                                isLoading = false,
                                order = order,
                                items = order?.items ?: emptyList(),
                                isCurrentShopBuyer = isCurrentShopBuyer
                            )
                        }
                    }
                )
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun approveOrder() {
        val order = currentState.order ?: return

        viewModelScope.launch {
            setState { copy(isProcessing = true, showApproveDialog = false) }

            try {
                val prefs = preferencesManager.userPreferencesFlow.first()
                val userId = prefs.userId ?: ""

                purchaseOrderRepository.approvePurchaseOrder(order.id, userId).fold(
                    ifLeft = { error ->
                        setState { copy(isProcessing = false) }
                        setEffect(PurchaseOrderDetailEffect.ShowSnackbar(error.message ?: "Failed to approve"))
                    },
                    ifRight = {
                        setState { copy(isProcessing = false) }
                        setEffect(PurchaseOrderDetailEffect.ShowSnackbar("Order approved"))
                        loadOrder(order.id)
                    }
                )
            } catch (e: Exception) {
                setState { copy(isProcessing = false) }
                setEffect(PurchaseOrderDetailEffect.ShowSnackbar(e.message ?: "Failed to approve"))
            }
        }
    }

    private fun rejectOrder() {
        val order = currentState.order ?: return

        if (currentState.rejectReason.isBlank()) {
            setEffect(PurchaseOrderDetailEffect.ShowSnackbar("Please provide a reason"))
            return
        }

        viewModelScope.launch {
            setState { copy(isProcessing = true, showRejectDialog = false) }

            purchaseOrderRepository.rejectPurchaseOrder(order.id, currentState.rejectReason).fold(
                ifLeft = { error ->
                    setState { copy(isProcessing = false) }
                    setEffect(PurchaseOrderDetailEffect.ShowSnackbar(error.message ?: "Failed to reject"))
                },
                ifRight = {
                    setState { copy(isProcessing = false, rejectReason = "") }
                    setEffect(PurchaseOrderDetailEffect.ShowSnackbar("Order rejected"))
                    loadOrder(order.id)
                }
            )
        }
    }

    private fun addPayment() {
        val order = currentState.order ?: return
        val amount = currentState.paymentAmount.toBigDecimalOrNull()

        if (amount == null || amount <= BigDecimal.ZERO) {
            setEffect(PurchaseOrderDetailEffect.ShowSnackbar("Please enter a valid amount"))
            return
        }

        viewModelScope.launch {
            setState { copy(isProcessing = true, showPaymentDialog = false) }

            try {
                val prefs = preferencesManager.userPreferencesFlow.first()
                val userId = prefs.userId ?: ""

                val payment = PurchasePayment(
                    id = UUID.randomUUID().toString(),
                    purchaseOrderId = order.id,
                    amount = amount,
                    paymentMethod = currentState.paymentMethod,
                    recordedBy = userId,
                    createdAt = LocalDateTime.now()
                )

                purchaseOrderRepository.addPayment(order.id, payment).fold(
                    ifLeft = { error ->
                        setState { copy(isProcessing = false) }
                        setEffect(PurchaseOrderDetailEffect.ShowSnackbar(error.message ?: "Failed to add payment"))
                    },
                    ifRight = {
                        setState { copy(isProcessing = false, paymentAmount = "") }
                        setEffect(PurchaseOrderDetailEffect.ShowSnackbar("Payment added"))
                        loadOrder(order.id)
                    }
                )
            } catch (e: Exception) {
                setState { copy(isProcessing = false) }
                setEffect(PurchaseOrderDetailEffect.ShowSnackbar(e.message ?: "Failed to add payment"))
            }
        }
    }
}

