package com.hojaz.maiduka26.presentantion.screens.reports

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hojaz.maiduka26.presentantion.components.ErrorView
import com.hojaz.maiduka26.presentantion.components.LoadingIndicator
import com.hojaz.maiduka26.presentantion.navigation.Screen
import com.hojaz.maiduka26.util.CurrencyFormatter
import kotlinx.coroutines.flow.collectLatest
import java.math.BigDecimal

/**
 * Reports screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReportsScreen(
    navController: NavController,
    viewModel: ReportsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ReportsEffect.NavigateBack -> navController.popBackStack()
                is ReportsEffect.NavigateToSalesReport -> navController.navigate(Screen.SalesReport.route)
                is ReportsEffect.NavigateToProfitReport -> navController.navigate(Screen.ProfitReport.route)
                is ReportsEffect.NavigateToInventoryReport -> navController.navigate(Screen.InventoryReport.route)
                is ReportsEffect.NavigateToDebtReport -> navController.navigate(Screen.DebtReport.route)
                is ReportsEffect.NavigateToExpenseReport -> navController.navigate(Screen.ExpenseReport.route)
                is ReportsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is ReportsEffect.ShareReport -> { /* Handle share */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reports & Analytics") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(ReportsEvent.NavigateBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(ReportsEvent.ExportReport) }) {
                        Icon(Icons.Default.Share, contentDescription = "Export")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorView(
                message = state.error!!,
                onRetry = { viewModel.onEvent(ReportsEvent.Refresh) }
            )
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Period selector
                    item {
                        PeriodSelector(
                            selectedPeriod = state.selectedPeriod,
                            onSelectPeriod = { viewModel.onEvent(ReportsEvent.SelectPeriod(it)) }
                        )
                    }

                    // Key metrics
                    item {
                        KeyMetricsSection(state = state)
                    }

                    // Report cards
                    item {
                        Text(
                            text = "Detailed Reports",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    item {
                        ReportCard(
                            title = "Sales Report",
                            description = "View detailed sales analysis",
                            icon = Icons.Outlined.ShoppingCart,
                            value = "${state.salesCount} sales",
                            onClick = { viewModel.onEvent(ReportsEvent.NavigateToSalesReport) }
                        )
                    }

                    item {
                        ReportCard(
                            title = "Profit & Loss",
                            description = "Revenue, costs, and net profit",
                            icon = Icons.Outlined.TrendingUp,
                            value = CurrencyFormatter.format(state.netProfit.toDouble(), state.activeShop?.currency ?: "TZS"),
                            valueColor = if (state.netProfit >= BigDecimal.ZERO) Color(0xFF4CAF50) else Color(0xFFF44336),
                            onClick = { viewModel.onEvent(ReportsEvent.NavigateToProfitReport) }
                        )
                    }

                    item {
                        ReportCard(
                            title = "Inventory Report",
                            description = "Stock levels and valuation",
                            icon = Icons.Outlined.Inventory2,
                            value = "${state.totalProducts} products",
                            onClick = { viewModel.onEvent(ReportsEvent.NavigateToInventoryReport) }
                        )
                    }

                    item {
                        ReportCard(
                            title = "Customer Debts",
                            description = "Outstanding credit balances",
                            icon = Icons.Outlined.AccountBalance,
                            value = CurrencyFormatter.format(state.totalDebt.toDouble(), state.activeShop?.currency ?: "TZS"),
                            valueColor = if (state.totalDebt > BigDecimal.ZERO) Color(0xFFF44336) else Color(0xFF4CAF50),
                            onClick = { viewModel.onEvent(ReportsEvent.NavigateToDebtReport) }
                        )
                    }

                    item {
                        ReportCard(
                            title = "Expense Report",
                            description = "Business expenses breakdown",
                            icon = Icons.Outlined.Money,
                            value = CurrencyFormatter.format(state.totalExpenses.toDouble(), state.activeShop?.currency ?: "TZS"),
                            onClick = { viewModel.onEvent(ReportsEvent.NavigateToExpenseReport) }
                        )
                    }

                    // Daily sales chart placeholder
                    if (state.dailySales.isNotEmpty()) {
                        item {
                            DailySalesChart(
                                dailySales = state.dailySales,
                                currency = state.activeShop?.currency ?: "TZS"
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(16.dp)) }
                }
            }
        }
    }
}

@Composable
private fun PeriodSelector(
    selectedPeriod: ReportPeriod,
    onSelectPeriod: (ReportPeriod) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(ReportPeriod.entries.filter { it != ReportPeriod.CUSTOM }) { period ->
            FilterChip(
                selected = selectedPeriod == period,
                onClick = { onSelectPeriod(period) },
                label = { Text(period.displayName) }
            )
        }
    }
}

@Composable
private fun KeyMetricsSection(state: ReportsState) {
    val currency = state.activeShop?.currency ?: "TZS"

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Total Sales",
                value = CurrencyFormatter.format(state.totalSales.toDouble(), currency),
                icon = Icons.Default.ShoppingCart,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Gross Profit",
                value = CurrencyFormatter.format(state.totalProfit.toDouble(), currency),
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Expenses",
                value = CurrencyFormatter.format(state.totalExpenses.toDouble(), currency),
                icon = Icons.Default.Remove,
                color = Color(0xFFF44336),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Net Profit",
                value = CurrencyFormatter.format(state.netProfit.toDouble(), currency),
                icon = Icons.Default.AccountBalance,
                color = if (state.netProfit >= BigDecimal.ZERO) Color(0xFF4CAF50) else Color(0xFFF44336),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            MetricCard(
                title = "Transactions",
                value = state.salesCount.toString(),
                icon = Icons.Default.Receipt,
                color = Color(0xFF2196F3),
                modifier = Modifier.weight(1f)
            )
            MetricCard(
                title = "Avg Order",
                value = CurrencyFormatter.format(state.averageOrderValue.toDouble(), currency),
                icon = Icons.Default.Assessment,
                color = Color(0xFF9C27B0),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.1f)
        )
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ReportCard(
    title: String,
    description: String,
    icon: ImageVector,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.primary,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = valueColor
                )
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun DailySalesChart(
    dailySales: List<DailySalesData>,
    currency: String
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Daily Sales Trend",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Simple bar chart representation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val maxSales = dailySales.maxOfOrNull { it.sales.toDouble() } ?: 1.0

                dailySales.takeLast(7).forEach { data ->
                    val height = if (maxSales > 0) {
                        (data.sales.toDouble() / maxSales * 100).toFloat().coerceIn(10f, 100f)
                    } else 10f

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Surface(
                            modifier = Modifier
                                .width(24.dp)
                                .height(height.dp),
                            color = MaterialTheme.colorScheme.primary,
                            shape = MaterialTheme.shapes.small
                        ) {}

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = data.date.dayOfMonth.toString(),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val totalSales = dailySales.sumOf { it.sales.toDouble() }
                val totalCount = dailySales.sumOf { it.count }

                Text(
                    text = "Total: ${CurrencyFormatter.format(totalSales, currency)}",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$totalCount transactions",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

