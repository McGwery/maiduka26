package com.hojaz.maiduka26.presentantion.screens.dashboard

import com.hojaz.maiduka26.domain.model.DashboardSummary
import com.hojaz.maiduka26.domain.model.Sale
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.presentantion.base.ViewState

/**
 * State for the Dashboard screen.
 */
data class DashboardState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeShop: Shop? = null,
    val summary: DashboardSummary = DashboardSummary(),
    val recentSales: List<Sale> = emptyList(),
    val isOnline: Boolean = true,
    val lastSyncTime: String? = null
) : ViewState

/**
 * Events for the Dashboard screen.
 */
sealed class DashboardEvent {
    data object LoadDashboard : DashboardEvent()
    data object Refresh : DashboardEvent()
    data object NavigateToPOS : DashboardEvent()
    data object NavigateToProducts : DashboardEvent()
    data object NavigateToSales : DashboardEvent()
    data object NavigateToCustomers : DashboardEvent()
    data object NavigateToExpenses : DashboardEvent()
    data object NavigateToReports : DashboardEvent()
    data object NavigateToSettings : DashboardEvent()
    data object NavigateToShopSelection : DashboardEvent()
    data object SyncData : DashboardEvent()
    data class NavigateToSaleDetail(val saleId: String) : DashboardEvent()
}

/**
 * Side effects for the Dashboard screen.
 */
sealed class DashboardEffect {
    data object NavigateToPOS : DashboardEffect()
    data object NavigateToProducts : DashboardEffect()
    data object NavigateToSales : DashboardEffect()
    data object NavigateToCustomers : DashboardEffect()
    data object NavigateToExpenses : DashboardEffect()
    data object NavigateToReports : DashboardEffect()
    data object NavigateToSettings : DashboardEffect()
    data object NavigateToShopSelection : DashboardEffect()
    data class NavigateToSaleDetail(val saleId: String) : DashboardEffect()
    data class ShowSnackbar(val message: String) : DashboardEffect()
}

