package com.hojaz.maiduka26.presentantion.screens.sale.pos

import com.hojaz.maiduka26.domain.model.Category
import com.hojaz.maiduka26.domain.model.Customer
import com.hojaz.maiduka26.domain.model.PaymentMethod
import com.hojaz.maiduka26.domain.model.Product
import com.hojaz.maiduka26.presentantion.base.ViewState
import java.math.BigDecimal

/**
 * State for the POS screen.
 */
data class POSState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val customers: List<Customer> = emptyList(),
    val cartItems: List<CartItem> = emptyList(),
    val selectedCustomer: Customer? = null,
    val searchQuery: String = "",
    val selectedCategoryId: String? = null,
    val discountAmount: BigDecimal = BigDecimal.ZERO,
    val discountPercentage: BigDecimal = BigDecimal.ZERO,
    val notes: String = "",
    val showCustomerDialog: Boolean = false,
    val showPaymentDialog: Boolean = false,
    val showDiscountDialog: Boolean = false,
    val currency: String = "TZS"
) : ViewState {

    val subtotal: BigDecimal
        get() = cartItems.fold(BigDecimal.ZERO) { acc, item -> acc.add(item.total) }

    val totalDiscount: BigDecimal
        get() {
            val percentageDiscount = subtotal.multiply(discountPercentage).divide(BigDecimal(100))
            return discountAmount.add(percentageDiscount)
        }

    val total: BigDecimal
        get() = subtotal.subtract(totalDiscount).max(BigDecimal.ZERO)

    val itemCount: Int
        get() = cartItems.sumOf { it.quantity.toInt() }

    val filteredProducts: List<Product>
        get() {
            var filtered = products.filter { !it.isOutOfStock || !it.trackInventory }

            if (selectedCategoryId != null) {
                filtered = filtered.filter { it.categoryId == selectedCategoryId }
            }

            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                filtered = filtered.filter {
                    it.productName.lowercase().contains(query) ||
                    it.sku?.lowercase()?.contains(query) == true ||
                    it.barcode?.lowercase()?.contains(query) == true
                }
            }

            return filtered
        }
}

/**
 * Cart item for POS.
 */
data class CartItem(
    val product: Product,
    val quantity: BigDecimal = BigDecimal.ONE,
    val unitPrice: BigDecimal,
    val discount: BigDecimal = BigDecimal.ZERO
) {
    val subtotal: BigDecimal get() = unitPrice.multiply(quantity)
    val total: BigDecimal get() = subtotal.subtract(discount).max(BigDecimal.ZERO)
}

/**
 * Events for the POS screen.
 */
sealed class POSEvent {
    data object LoadData : POSEvent()
    data class SearchQueryChanged(val query: String) : POSEvent()
    data class CategorySelected(val categoryId: String?) : POSEvent()
    data class AddToCart(val product: Product) : POSEvent()
    data class UpdateCartItemQuantity(val productId: String, val quantity: BigDecimal) : POSEvent()
    data class RemoveFromCart(val productId: String) : POSEvent()
    data object ClearCart : POSEvent()
    data class SelectCustomer(val customer: Customer?) : POSEvent()
    data class SetDiscount(val amount: BigDecimal, val percentage: BigDecimal) : POSEvent()
    data class SetNotes(val notes: String) : POSEvent()
    data object ShowCustomerDialog : POSEvent()
    data object HideCustomerDialog : POSEvent()
    data object ShowPaymentDialog : POSEvent()
    data object HidePaymentDialog : POSEvent()
    data object ShowDiscountDialog : POSEvent()
    data object HideDiscountDialog : POSEvent()
    data class ProcessPayment(val payments: List<PaymentEntry>) : POSEvent()
    data class ScanBarcode(val barcode: String) : POSEvent()
    data object NavigateBack : POSEvent()
}

/**
 * Payment entry for checkout.
 */
data class PaymentEntry(
    val method: PaymentMethod,
    val amount: BigDecimal,
    val reference: String? = null
)

/**
 * Side effects for the POS screen.
 */
sealed class POSEffect {
    data object NavigateBack : POSEffect()
    data class NavigateToSaleDetail(val saleId: String) : POSEffect()
    data class ShowSnackbar(val message: String) : POSEffect()
    data object PrintReceipt : POSEffect()
    data object ClearCartAndReset : POSEffect()
}

