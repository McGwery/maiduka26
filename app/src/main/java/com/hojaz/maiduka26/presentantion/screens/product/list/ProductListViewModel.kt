package com.hojaz.maiduka26.presentantion.screens.product.list

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.usecase.category.GetCategoriesUseCase
import com.hojaz.maiduka26.domain.usecase.product.GetProductsUseCase
import com.hojaz.maiduka26.domain.usecase.shop.GetActiveShopUseCase
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Product List screen.
 */
@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getCategoriesUseCase: GetCategoriesUseCase
) : BaseViewModel<ProductListState, ProductListEvent, ProductListEffect>() {

    override fun createInitialState(): ProductListState = ProductListState()

    init {
        loadProducts()
        loadCategories()
    }

    override fun onEvent(event: ProductListEvent) {
        when (event) {
            is ProductListEvent.LoadProducts -> loadProducts()
            is ProductListEvent.Refresh -> loadProducts()
            is ProductListEvent.SearchQueryChanged -> {
                setState { copy(searchQuery = event.query) }
            }
            is ProductListEvent.CategorySelected -> {
                setState { copy(selectedCategoryId = event.categoryId) }
            }
            is ProductListEvent.SortByChanged -> {
                setState { copy(sortBy = event.sortBy) }
            }
            is ProductListEvent.ToggleLowStockFilter -> {
                setState { copy(showLowStockOnly = !showLowStockOnly) }
            }
            is ProductListEvent.NavigateToCreateProduct -> {
                setEffect(ProductListEffect.NavigateToCreateProduct)
            }
            is ProductListEvent.NavigateToProductDetail -> {
                setEffect(ProductListEffect.NavigateToProductDetail(event.productId))
            }
            is ProductListEvent.NavigateBack -> {
                setEffect(ProductListEffect.NavigateBack)
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val shop = getActiveShopUseCase().first()
                if (shop == null) {
                    setState { copy(isLoading = false, error = "No shop selected") }
                    return@launch
                }

                getProductsUseCase(shop.id).collectLatest { products ->
                    setState { copy(isLoading = false, products = products) }
                }
            } catch (e: Exception) {
                setState {
                    copy(isLoading = false, error = e.message ?: "Failed to load products")
                }
            }
        }
    }

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                getCategoriesUseCase().collectLatest { categories ->
                    setState { copy(categories = categories) }
                }
            } catch (e: Exception) {
                // Categories are optional, just log the error
            }
        }
    }
}

