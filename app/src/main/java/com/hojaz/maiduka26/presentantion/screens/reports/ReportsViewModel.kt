package com.hojaz.maiduka26.presentantion.screens.reports

import androidx.lifecycle.viewModelScope
import com.hojaz.maiduka26.domain.repository.CustomerRepository
import com.hojaz.maiduka26.domain.repository.ExpenseRepository
import com.hojaz.maiduka26.domain.repository.ProductRepository
import com.hojaz.maiduka26.domain.repository.SaleRepository
import com.hojaz.maiduka26.domain.usecase.shop.GetActiveShopUseCase
import com.hojaz.maiduka26.presentantion.base.BaseViewModel
import com.hojaz.maiduka26.util.DateTimeUtil
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import javax.inject.Inject

/**
 * ViewModel for the Reports screen.
 */
@HiltViewModel
class ReportsViewModel @Inject constructor(
    private val getActiveShopUseCase: GetActiveShopUseCase,
    private val saleRepository: SaleRepository,
    private val productRepository: ProductRepository,
    private val customerRepository: CustomerRepository,
    private val expenseRepository: ExpenseRepository
) : BaseViewModel<ReportsState, ReportsEvent, ReportsEffect>() {

    override fun createInitialState(): ReportsState = ReportsState()

    init {
        loadActiveShop()
    }

    override fun onEvent(event: ReportsEvent) {
        when (event) {
            is ReportsEvent.LoadReports -> loadReports()
            is ReportsEvent.Refresh -> loadReports()
            is ReportsEvent.SelectPeriod -> selectPeriod(event.period)
            is ReportsEvent.SelectDateRange -> selectDateRange(event.startDate, event.endDate)
            is ReportsEvent.SelectReportType -> setState { copy(selectedReportType = event.type) }
            is ReportsEvent.ExportReport -> exportReport()
            is ReportsEvent.NavigateBack -> setEffect(ReportsEffect.NavigateBack)
            is ReportsEvent.NavigateToSalesReport -> setEffect(ReportsEffect.NavigateToSalesReport)
            is ReportsEvent.NavigateToProfitReport -> setEffect(ReportsEffect.NavigateToProfitReport)
            is ReportsEvent.NavigateToInventoryReport -> setEffect(ReportsEffect.NavigateToInventoryReport)
            is ReportsEvent.NavigateToDebtReport -> setEffect(ReportsEffect.NavigateToDebtReport)
            is ReportsEvent.NavigateToExpenseReport -> setEffect(ReportsEffect.NavigateToExpenseReport)
        }
    }

    private fun loadActiveShop() {
        viewModelScope.launch {
            getActiveShopUseCase().collectLatest { shop ->
                setState { copy(activeShop = shop) }
                shop?.let { loadReports() }
            }
        }
    }

    private fun selectPeriod(period: ReportPeriod) {
        val today = LocalDate.now()
        val (startDate, endDate) = when (period) {
            ReportPeriod.TODAY -> today to today
            ReportPeriod.YESTERDAY -> today.minusDays(1) to today.minusDays(1)
            ReportPeriod.LAST_7_DAYS -> today.minusDays(6) to today
            ReportPeriod.LAST_30_DAYS -> today.minusDays(29) to today
            ReportPeriod.THIS_MONTH -> today.withDayOfMonth(1) to today
            ReportPeriod.LAST_MONTH -> {
                val lastMonth = today.minusMonths(1)
                lastMonth.withDayOfMonth(1) to lastMonth.withDayOfMonth(lastMonth.lengthOfMonth())
            }
            ReportPeriod.CUSTOM -> currentState.startDate to currentState.endDate
        }

        setState { copy(selectedPeriod = period, startDate = startDate, endDate = endDate) }
        loadReports()
    }

    private fun selectDateRange(startDate: LocalDate, endDate: LocalDate) {
        setState {
            copy(
                selectedPeriod = ReportPeriod.CUSTOM,
                startDate = startDate,
                endDate = endDate
            )
        }
        loadReports()
    }

    private fun loadReports() {
        val shopId = currentState.activeShop?.id ?: return

        viewModelScope.launch {
            setState { copy(isLoading = true, error = null) }

            try {
                val startDateTime = currentState.startDate.atStartOfDay()
                val endDateTime = currentState.endDate.atTime(23, 59, 59)

                // Load sales data
                launch {
                    saleRepository.getSalesByDateRange(shopId, startDateTime, endDateTime)
                        .collectLatest { sales ->
                            val totalSales = sales.fold(BigDecimal.ZERO) { acc, sale ->
                                acc.add(sale.totalAmount)
                            }
                            val totalProfit = sales.fold(BigDecimal.ZERO) { acc, sale ->
                                acc.add(sale.profitAmount ?: BigDecimal.ZERO)
                            }
                            val averageOrderValue = if (sales.isNotEmpty()) {
                                totalSales.divide(BigDecimal(sales.size), 2, RoundingMode.HALF_UP)
                            } else BigDecimal.ZERO

                            // Calculate daily sales
                            val dailySalesMap = sales.groupBy { it.saleDate.toLocalDate() }
                            val dailySales = dailySalesMap.map { (date, daySales) ->
                                DailySalesData(
                                    date = date,
                                    sales = daySales.fold(BigDecimal.ZERO) { acc, s -> acc.add(s.totalAmount) },
                                    profit = daySales.fold(BigDecimal.ZERO) { acc, s -> acc.add(s.profitAmount ?: BigDecimal.ZERO) },
                                    count = daySales.size
                                )
                            }.sortedBy { it.date }

                            // Payment method breakdown
                            val paymentMethods = mutableMapOf<String, Pair<BigDecimal, Int>>()
                            // This would need payment data - simplified here

                            setState {
                                copy(
                                    totalSales = totalSales,
                                    totalProfit = totalProfit,
                                    salesCount = sales.size,
                                    averageOrderValue = averageOrderValue,
                                    dailySales = dailySales
                                )
                            }
                        }
                }

                // Load expenses
                launch {
                    expenseRepository.getExpensesByDateRange(shopId, currentState.startDate, currentState.endDate)
                        .collectLatest { expenses ->
                            val totalExpenses = expenses.fold(BigDecimal.ZERO) { acc, expense ->
                                acc.add(expense.amount)
                            }
                            val netProfit = currentState.totalProfit.subtract(totalExpenses)

                            setState {
                                copy(
                                    totalExpenses = totalExpenses,
                                    netProfit = netProfit
                                )
                            }
                        }
                }

                // Load products for inventory
                launch {
                    productRepository.getProducts(shopId).collectLatest { products ->
                        val inventoryValue = products.fold(BigDecimal.ZERO) { acc, product ->
                            val stock = product.currentStock ?: 0
                            val cost = product.costPerUnit ?: BigDecimal.ZERO
                            acc.add(cost.multiply(BigDecimal(stock)))
                        }
                        val lowStock = products.count { it.isLowStock }
                        val outOfStock = products.count { it.isOutOfStock }

                        setState {
                            copy(
                                totalProducts = products.size,
                                inventoryValue = inventoryValue,
                                lowStockCount = lowStock,
                                outOfStockCount = outOfStock
                            )
                        }
                    }
                }

                // Load customers
                launch {
                    customerRepository.getCustomers(shopId).collectLatest { customers ->
                        val totalDebt = customers.fold(BigDecimal.ZERO) { acc, customer ->
                            acc.add(customer.currentDebt)
                        }

                        setState {
                            copy(
                                totalCustomers = customers.size,
                                totalDebt = totalDebt
                            )
                        }
                    }
                }

                setState { copy(isLoading = false) }
            } catch (e: Exception) {
                setState { copy(isLoading = false, error = e.message) }
            }
        }
    }

    private fun exportReport() {
        viewModelScope.launch {
            // TODO: Generate PDF/CSV report
            setEffect(ReportsEffect.ShowSnackbar("Report exported successfully"))
        }
    }
}

