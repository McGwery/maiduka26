package com.hojaz.maiduka26.presentantion.screens.sale.pos

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.model.Sale
import com.hojaz.maiduka26.domain.model.SaleItem
import com.hojaz.maiduka26.domain.model.SalePayment
import com.hojaz.maiduka26.domain.model.SaleStatus
import com.hojaz.maiduka26.domain.model.PaymentStatus
import com.hojaz.maiduka26.domain.usecase.category.GetCategoriesUseCase
import com.hojaz.maiduka26.domain.usecase.customer.GetCustomersUseCase
import com.hojaz.maiduka26.domain.usecase.product.GetProductByBarcodeUseCase
import com.hojaz.maiduka26.domain.usecase.product.GetProductsUseCase
import com.hojaz.maiduka26.domain.usecase.sale.CreateSaleUseCase
import com.hojaz.maiduka26.domain.usecase.shop.GetActiveShopUseCase
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import com.hojaz.maiduka26.util.DateTimeUtil
import java.math.BigDecimal
import java.util.UUID
import javax.inject.Inject

/**
 * ViewModel for the POS screen.
 */
@HiltViewModel
class POSViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getProductByBarcodeUseCase: GetProductByBarcodeUseCase,
    private val createSaleUseCase: CreateSaleUseCase,
    private val preferencesManager: PreferencesManager
) : BaseViewModel<POSState, POSEvent, POSEffect>() {

    override fun createInitialState(): POSState = POSState()

    init {
        loadData()
    }

    override fun onEvent(event: POSEvent) {
        when (event) {
            is POSEvent.LoadData -> loadData()
            is POSEvent.SearchQueryChanged -> setState { copy(searchQuery = event.query) }
            is POSEvent.CategorySelected -> setState { copy(selectedCategoryId = event.categoryId) }
            is POSEvent.AddToCart -> addToCart(event.product)
            is POSEvent.UpdateCartItemQuantity -> updateCartItemQuantity(event.productId, event.quantity)
            is POSEvent.RemoveFromCart -> removeFromCart(event.productId)
            is POSEvent.ClearCart -> clearCart()
            is POSEvent.SelectCustomer -> setState { copy(selectedCustomer = event.customer, showCustomerDialog = false) }
            is POSEvent.SetDiscount -> setState { copy(discountAmount = event.amount, discountPercentage = event.percentage, showDiscountDialog = false) }
            is POSEvent.SetNotes -> setState { copy(notes = event.notes) }
            is POSEvent.ShowCustomerDialog -> setState { copy(showCustomerDialog = true) }
            is POSEvent.HideCustomerDialog -> setState { copy(showCustomerDialog = false) }
            is POSEvent.ShowPaymentDialog -> setState { copy(showPaymentDialog = true) }
            is POSEvent.HidePaymentDialog -> setState { copy(showPaymentDialog = false) }
            is POSEvent.ShowDiscountDialog -> setState { copy(showDiscountDialog = true) }
            is POSEvent.HideDiscountDialog -> setState { copy(showDiscountDialog = false) }
            is POSEvent.ProcessPayment -> processPayment(event.payments)
            is POSEvent.ScanBarcode -> handleBarcodeScan(event.barcode)
            is POSEvent.NavigateBack -> setEffect(POSEffect.NavigateBack)
        }
    }

    private fun loadData() {
        viewModelScope.launch {
            setState { copy(isLoading = true) }

            try {
                val shop = getActiveShopUseCase().first()
                if (shop == null) {
                    setState { copy(isLoading = false, error = "No shop selected") }
                    return@launch
                }

                setState { copy(currency = shop.currency) }

                // Load products
                launch {
                    getProductsUseCase(shop.id).collectLatest { products ->
                        setState { copy(products = products) }
                    }
                }

                // Load categories
                launch {
                    getCategoriesUseCase().collectLatest { categories ->
                        setState { copy(categories = categories) }
                    }
                }

                // Load customers
                launch {
                    getCustomersUseCase(shop.id).collectLatest { customers ->
                        setState { copy(customers = customers) }
                    }
                }

                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun addToCart(product: com.hojaz.maiduka26.domain.model.Product) {
        val existingItem = currentState.cartItems.find { it.product.id == product.id }

        if (existingItem != null) {
            // Increase quantity
            val newQuantity = existingItem.quantity.add(BigDecimal.ONE)
            updateCartItemQuantity(product.id, newQuantity)
        } else {
            // Add new item
            val cartItem = CartItem(
                product = product,
                quantity = BigDecimal.ONE,
                unitPrice = product.pricePerUnit ?: BigDecimal.ZERO
            )
            setState { copy(cartItems = cartItems + cartItem) }
        }
    }

    private fun updateCartItemQuantity(productId: String, quantity: BigDecimal) {
        if (quantity <= BigDecimal.ZERO) {
            removeFromCart(productId)
            return
        }

        val updatedItems = currentState.cartItems.map { item ->
            if (item.product.id == productId) {
                item.copy(quantity = quantity)
            } else {
                item
            }
        }
        setState { copy(cartItems = updatedItems) }
    }

    private fun removeFromCart(productId: String) {
        val updatedItems = currentState.cartItems.filter { it.product.id != productId }
        setState { copy(cartItems = updatedItems) }
    }

    private fun clearCart() {
        setState {
            copy(
                cartItems = emptyList(),
                selectedCustomer = null,
                discountAmount = BigDecimal.ZERO,
                discountPercentage = BigDecimal.ZERO,
                notes = ""
            )
        }
    }

    private fun handleBarcodeScan(barcode: String) {
        viewModelScope.launch {
            getProductByBarcodeUseCase(barcode).fold(
                ifLeft = { _ ->
                    setEffect(POSEffect.ShowSnackbar("Product not found"))
                },
                ifRight = { product ->
                    if (product != null) {
                        addToCart(product)
                    } else {
                        setEffect(POSEffect.ShowSnackbar("Product not found"))
                    }
                }
            )
        }
    }

    private fun processPayment(payments: List<PaymentEntry>) {
        if (currentState.cartItems.isEmpty()) {
            setEffect(POSEffect.ShowSnackbar("Cart is empty"))
            return
        }

        viewModelScope.launch {
            setState { copy(isLoading = true, showPaymentDialog = false) }

            try {
                val shop = getActiveShopUseCase().first()
                    ?: throw Exception("No shop selected")

                val prefs = preferencesManager.userPreferencesFlow.first()
                val userId = prefs.userId ?: throw Exception("User not logged in")

                val totalPaid = payments.fold(BigDecimal.ZERO) { acc, p -> acc.add(p.amount) }
                val total = currentState.total
                val change = totalPaid.subtract(total).max(BigDecimal.ZERO)
                val debt = total.subtract(totalPaid).max(BigDecimal.ZERO)

                val paymentStatus = when {
                    totalPaid >= total -> PaymentStatus.PAID
                    totalPaid > BigDecimal.ZERO -> PaymentStatus.PARTIALLY_PAID
                    else -> PaymentStatus.PENDING
                }

                // Generate sale number
                val saleNumber = "S${System.currentTimeMillis()}"

                // Calculate profit
                val totalCost = currentState.cartItems.fold(BigDecimal.ZERO) { acc, item ->
                    val costPrice = item.product.costPerUnit ?: BigDecimal.ZERO
                    acc.add(costPrice.multiply(item.quantity))
                }
                val profit = total.subtract(totalCost)

                // Create sale
                val sale = Sale(
                    id = UUID.randomUUID().toString(),
                    shopId = shop.id,
                    customerId = currentState.selectedCustomer?.id,
                    userId = userId,
                    saleNumber = saleNumber,
                    subtotal = currentState.subtotal,
                    discountAmount = currentState.discountAmount,
                    discountPercentage = currentState.discountPercentage,
                    totalAmount = total,
                    amountPaid = totalPaid,
                    changeAmount = change,
                    debtAmount = debt,
                    profitAmount = profit,
                    status = SaleStatus.COMPLETED,
                    paymentStatus = paymentStatus,
                    notes = currentState.notes.takeIf { it.isNotBlank() },
                    saleDate = DateTimeUtil.now()
                )

                // Create sale items
                val saleItems = currentState.cartItems.map { cartItem ->
                    SaleItem(
                        id = UUID.randomUUID().toString(),
                        saleId = sale.id,
                        productId = cartItem.product.id,
                        productName = cartItem.product.productName,
                        productSku = cartItem.product.sku,
                        quantity = cartItem.quantity,
                        originalPrice = cartItem.product.pricePerUnit ?: BigDecimal.ZERO,
                        sellingPrice = cartItem.unitPrice,
                        costPrice = cartItem.product.costPerUnit ?: BigDecimal.ZERO,
                        discountAmount = cartItem.discount,
                        subtotal = cartItem.subtotal,
                        total = cartItem.total,
                        profit = cartItem.total.subtract(
                            (cartItem.product.costPerUnit ?: BigDecimal.ZERO).multiply(cartItem.quantity)
                        )
                    )
                }

                // Create sale payments
                val salePayments = payments.map { payment ->
                    SalePayment(
                        id = UUID.randomUUID().toString(),
                        saleId = sale.id,
                        userId = userId,
                        paymentMethod = payment.method,
                        amount = payment.amount,
                        referenceNumber = payment.reference,
                        paymentDate = DateTimeUtil.now()
                    )
                }

                createSaleUseCase(sale, saleItems, salePayments).fold(
                    ifLeft = { error ->
                        setState { copy(isLoading = false) }
                        setEffect(POSEffect.ShowSnackbar(error.message ?: "Failed to create sale"))
                    },
                    ifRight = { createdSale ->
                        setState { copy(isLoading = false) }
                        setEffect(POSEffect.ShowSnackbar("Sale completed successfully"))
                        clearCart()
                        setEffect(POSEffect.NavigateToSaleDetail(createdSale.id))
                    }
                )
            } catch (e: Exception) {
                setState { copy(isLoading = false) }
                setEffect(POSEffect.ShowSnackbar(e.message ?: "Failed to process payment"))
            }
        }
    }
}

