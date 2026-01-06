package com.hojaz.maiduka26.presentantion.screens.reports

import com.hojaz.maiduka26.domain.model.InventoryReport
import com.hojaz.maiduka26.domain.model.SalesReport
import com.hojaz.maiduka26.domain.model.Shop
import com.hojaz.maiduka26.presentantion.base.ViewState
import java.math.BigDecimal
import java.time.LocalDate

/**
 * State for the Reports screen.
 */
data class ReportsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val activeShop: Shop? = null,

    // Date range
    val startDate: LocalDate = LocalDate.now().minusDays(30),
    val endDate: LocalDate = LocalDate.now(),
    val selectedPeriod: ReportPeriod = ReportPeriod.LAST_30_DAYS,

    // Sales data
    val totalSales: BigDecimal = BigDecimal.ZERO,
    val totalProfit: BigDecimal = BigDecimal.ZERO,
    val totalExpenses: BigDecimal = BigDecimal.ZERO,
    val netProfit: BigDecimal = BigDecimal.ZERO,
    val salesCount: Int = 0,
    val averageOrderValue: BigDecimal = BigDecimal.ZERO,

    // Daily sales for chart
    val dailySales: List<DailySalesData> = emptyList(),

    // Top products
    val topProducts: List<TopProduct> = emptyList(),

    // Payment methods breakdown
    val paymentMethodBreakdown: List<PaymentMethodData> = emptyList(),

    // Inventory
    val inventoryValue: BigDecimal = BigDecimal.ZERO,
    val lowStockCount: Int = 0,
    val outOfStockCount: Int = 0,
    val totalProducts: Int = 0,

    // Customers
    val totalCustomers: Int = 0,
    val totalDebt: BigDecimal = BigDecimal.ZERO,
    val newCustomers: Int = 0,

    val selectedReportType: ReportType = ReportType.OVERVIEW
) : ViewState

enum class ReportPeriod(val displayName: String, val days: Int) {
    TODAY("Today", 0),
    YESTERDAY("Yesterday", 1),
    LAST_7_DAYS("Last 7 Days", 7),
    LAST_30_DAYS("Last 30 Days", 30),
    THIS_MONTH("This Month", -1),
    LAST_MONTH("Last Month", -2),
    CUSTOM("Custom", -3)
}

enum class ReportType(val displayName: String) {
    OVERVIEW("Overview"),
    SALES("Sales"),
    PROFIT("Profit & Loss"),
    INVENTORY("Inventory"),
    CUSTOMERS("Customers"),
    EXPENSES("Expenses")
}

data class DailySalesData(
    val date: LocalDate,
    val sales: BigDecimal,
    val profit: BigDecimal,
    val count: Int
)

data class TopProduct(
    val productId: String,
    val productName: String,
    val quantitySold: Int,
    val revenue: BigDecimal,
    val profit: BigDecimal
)

data class PaymentMethodData(
    val method: String,
    val amount: BigDecimal,
    val count: Int,
    val percentage: Float
)

/**
 * Events for the Reports screen.
 */
sealed class ReportsEvent {
    data object LoadReports : ReportsEvent()
    data object Refresh : ReportsEvent()
    data class SelectPeriod(val period: ReportPeriod) : ReportsEvent()
    data class SelectDateRange(val startDate: LocalDate, val endDate: LocalDate) : ReportsEvent()
    data class SelectReportType(val type: ReportType) : ReportsEvent()
    data object ExportReport : ReportsEvent()
    data object NavigateBack : ReportsEvent()
    data object NavigateToSalesReport : ReportsEvent()
    data object NavigateToProfitReport : ReportsEvent()
    data object NavigateToInventoryReport : ReportsEvent()
    data object NavigateToDebtReport : ReportsEvent()
    data object NavigateToExpenseReport : ReportsEvent()
}

/**
 * Side effects for the Reports screen.
 */
sealed class ReportsEffect {
    data object NavigateBack : ReportsEffect()
    data object NavigateToSalesReport : ReportsEffect()
    data object NavigateToProfitReport : ReportsEffect()
    data object NavigateToInventoryReport : ReportsEffect()
    data object NavigateToDebtReport : ReportsEffect()
    data object NavigateToExpenseReport : ReportsEffect()
    data class ShowSnackbar(val message: String) : ReportsEffect()
    data class ShareReport(val filePath: String) : ReportsEffect()
}

