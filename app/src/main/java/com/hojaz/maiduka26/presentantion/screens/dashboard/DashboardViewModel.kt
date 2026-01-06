package com.hojaz.maiduka26.presentantion.screens.dashboard

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.model.DashboardSummary
import com.hojaz.maiduka26.domain.usecase.customer.GetCustomersUseCase
import com.hojaz.maiduka26.domain.usecase.customer.GetTotalDebtUseCase
import com.hojaz.maiduka26.domain.usecase.product.GetLowStockProductsUseCase
import com.hojaz.maiduka26.domain.usecase.product.GetProductsUseCase
import com.hojaz.maiduka26.domain.usecase.sale.GetTodaySalesUseCase
import com.hojaz.maiduka26.domain.usecase.sale.GetTodaySalesSummaryUseCase
import com.hojaz.maiduka26.domain.usecase.shop.GetActiveShopUseCase
import com.hojaz.maiduka26.data.sync.SyncManager
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import com.hojaz.maiduka26.util.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import javax.inject.Inject

/**
 * ViewModel for the Dashboard screen.
 */
@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val getTodaySalesUseCase: GetTodaySalesUseCase,
    private val getTodaySalesSummaryUseCase: GetTodaySalesSummaryUseCase,
    private val getProductsUseCase: GetProductsUseCase,
    private val getLowStockProductsUseCase: GetLowStockProductsUseCase,
    private val getCustomersUseCase: GetCustomersUseCase,
    private val getTotalDebtUseCase: GetTotalDebtUseCase,
    private val syncManager: SyncManager,
    private val networkMonitor: NetworkMonitor
) : BaseViewModel<DashboardState, DashboardEvent, DashboardEffect>() {

    override fun createInitialState(): DashboardState = DashboardState()

    init {
        observeActiveShop()
        observeNetworkStatus()
    }

    override fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.LoadDashboard -> loadDashboard()
            is DashboardEvent.Refresh -> loadDashboard()
            is DashboardEvent.NavigateToPOS -> setEffect(DashboardEffect.NavigateToPOS)
            is DashboardEvent.NavigateToProducts -> setEffect(DashboardEffect.NavigateToProducts)
            is DashboardEvent.NavigateToSales -> setEffect(DashboardEffect.NavigateToSales)
            is DashboardEvent.NavigateToCustomers -> setEffect(DashboardEffect.NavigateToCustomers)
            is DashboardEvent.NavigateToExpenses -> setEffect(DashboardEffect.NavigateToExpenses)
            is DashboardEvent.NavigateToReports -> setEffect(DashboardEffect.NavigateToReports)
            is DashboardEvent.NavigateToSettings -> setEffect(DashboardEffect.NavigateToSettings)
            is DashboardEvent.NavigateToShopSelection -> setEffect(DashboardEffect.NavigateToShopSelection)
            is DashboardEvent.NavigateToSaleDetail -> setEffect(DashboardEffect.NavigateToSaleDetail(event.saleId))
            is DashboardEvent.SyncData -> triggerSync()
        }
    }

    private fun observeActiveShop() {
        viewModelScope.launch {
            getActiveShopUseCase().collectLatest { shop ->
                setState { copy(activeShop = shop) }
                shop?.let { loadDashboard() }
            }
        }
    }

    private fun observeNetworkStatus() {
        viewModelScope.launch {
            networkMonitor.isOnlineFlow.collectLatest { isOnline ->
                setState { copy(isOnline = isOnline) }
            }
        }
    }

    private fun loadDashboard() {
        val shopId = currentState.activeShop?.id ?: return

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                // Load sales summary
                val salesSummary = getTodaySalesSummaryUseCase(shopId)

                // Load recent sales
                val recentSales = getTodaySalesUseCase(shopId).first().take(5)

                // Load low stock products count
                val lowStockProducts = getLowStockProductsUseCase(shopId).first()
                val lowStockCount = lowStockProducts.size
                val outOfStockCount = lowStockProducts.count { (it.currentStock ?: 0) <= 0 }

                // Load customer count
                val customers = getCustomersUseCase(shopId).first()
                val customerCount = customers.size

                // Load total debt
                val totalDebt = getTotalDebtUseCase(shopId)

                // Load product count
                val products = getProductsUseCase(shopId).first()
                val productCount = products.size

                val summary = DashboardSummary(
                    todaySales = BigDecimal(salesSummary.totalSales),
                    todayProfit = BigDecimal(salesSummary.totalProfit),
                    todaySalesCount = salesSummary.salesCount,
                    totalDebt = BigDecimal(totalDebt),
                    lowStockCount = lowStockCount,
                    outOfStockCount = outOfStockCount,
                    customerCount = customerCount,
                    productCount = productCount
                )

                setState {
                    copy(
                        isLoading = false,
                        summary = summary,
                        recentSales = recentSales
                    )
                }
            } catch (e: Exception) {
                setState {
                    copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load dashboard"
                    )
                }
            }
        }
    }

    private fun triggerSync() {
        if (!currentState.isOnline) {
            setEffect(DashboardEffect.ShowSnackbar("You are offline. Data will sync when connection is restored."))
            return
        }

        syncManager.triggerImmediateSync()
        setEffect(DashboardEffect.ShowSnackbar("Syncing data..."))
    }
}

