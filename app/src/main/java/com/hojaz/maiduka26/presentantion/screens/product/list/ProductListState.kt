package com.hojaz.maiduka26.presentantion.screens.product.list

import com.hojaz.maiduka26.domain.model.Category
import com.hojaz.maiduka26.domain.model.Product
import com.hojaz.maiduka26.presentantion.base.ViewState

/**
 * State for the Product List screen.
 */
data class ProductListState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val products: List<Product> = emptyList(),
    val categories: List<Category> = emptyList(),
    val selectedCategoryId: String? = null,
    val searchQuery: String = "",
    val showLowStockOnly: Boolean = false,
    val sortBy: ProductSortBy = ProductSortBy.NAME_ASC
) : ViewState {
    val filteredProducts: List<Product>
        get() {
            var filtered = products

            // Filter by category
            if (selectedCategoryId != null) {
                filtered = filtered.filter { it.categoryId == selectedCategoryId }
            }

            // Filter by search query
            if (searchQuery.isNotBlank()) {
                val query = searchQuery.lowercase()
                filtered = filtered.filter {
                    it.productName.lowercase().contains(query) ||
                    it.sku?.lowercase()?.contains(query) == true ||
                    it.barcode?.lowercase()?.contains(query) == true
                }
            }

            // Filter low stock only
            if (showLowStockOnly) {
                filtered = filtered.filter { it.isLowStock || it.isOutOfStock }
            }

            // Sort
            filtered = when (sortBy) {
                ProductSortBy.NAME_ASC -> filtered.sortedBy { it.productName }
                ProductSortBy.NAME_DESC -> filtered.sortedByDescending { it.productName }
                ProductSortBy.PRICE_ASC -> filtered.sortedBy { it.pricePerUnit }
                ProductSortBy.PRICE_DESC -> filtered.sortedByDescending { it.pricePerUnit }
                ProductSortBy.STOCK_ASC -> filtered.sortedBy { it.currentStock ?: 0 }
                ProductSortBy.STOCK_DESC -> filtered.sortedByDescending { it.currentStock ?: 0 }
            }

            return filtered
        }
}

enum class ProductSortBy {
    NAME_ASC, NAME_DESC, PRICE_ASC, PRICE_DESC, STOCK_ASC, STOCK_DESC
}

/**
 * Events for the Product List screen.
 */
sealed class ProductListEvent {
    data object LoadProducts : ProductListEvent()
    data object Refresh : ProductListEvent()
    data class SearchQueryChanged(val query: String) : ProductListEvent()
    data class CategorySelected(val categoryId: String?) : ProductListEvent()
    data class SortByChanged(val sortBy: ProductSortBy) : ProductListEvent()
    data object ToggleLowStockFilter : ProductListEvent()
    data object NavigateToCreateProduct : ProductListEvent()
    data class NavigateToProductDetail(val productId: String) : ProductListEvent()
    data object NavigateBack : ProductListEvent()
}

/**
 * Side effects for the Product List screen.
 */
sealed class ProductListEffect {
    data object NavigateToCreateProduct : ProductListEffect()
    data class NavigateToProductDetail(val productId: String) : ProductListEffect()
    data object NavigateBack : ProductListEffect()
    data class ShowSnackbar(val message: String) : ProductListEffect()
}

