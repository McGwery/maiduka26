package com.hojaz.maiduka26.presentantion.screens.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
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
import com.hojaz.maiduka26.domain.model.Sale
import com.hojaz.maiduka26.presentantion.components.ErrorView
import com.hojaz.maiduka26.presentantion.components.LoadingIndicator
import com.hojaz.maiduka26.presentantion.navigation.Screen
import com.hojaz.maiduka26.util.CurrencyFormatter
import com.hojaz.maiduka26.util.DateTimeUtil
import kotlinx.coroutines.flow.collectLatest

/**
 * Dashboard screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavController,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is DashboardEffect.NavigateToPOS -> navController.navigate(Screen.POS.route)
                is DashboardEffect.NavigateToProducts -> navController.navigate(Screen.ProductList.route)
                is DashboardEffect.NavigateToSales -> navController.navigate(Screen.SaleList.route)
                is DashboardEffect.NavigateToCustomers -> navController.navigate(Screen.CustomerList.route)
                is DashboardEffect.NavigateToExpenses -> navController.navigate(Screen.ExpenseList.route)
                is DashboardEffect.NavigateToReports -> navController.navigate(Screen.Reports.route)
                is DashboardEffect.NavigateToSettings -> navController.navigate(Screen.Settings.route)
                is DashboardEffect.NavigateToShopSelection -> navController.navigate(Screen.ShopList.route)
                is DashboardEffect.NavigateToSaleDetail -> navController.navigate(Screen.SaleDetail.createRoute(effect.saleId))
                is DashboardEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.activeShop?.name ?: "MaiDuka",
                            style = MaterialTheme.typography.titleMedium
                        )
                        if (state.activeShop != null) {
                            Text(
                                text = if (state.isOnline) "Online" else "Offline",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (state.isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800)
                            )
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.onEvent(DashboardEvent.SyncData) }) {
                        Icon(Icons.Default.Sync, contentDescription = "Sync")
                    }
                    IconButton(onClick = { viewModel.onEvent(DashboardEvent.NavigateToSettings) }) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(DashboardEvent.NavigateToShopSelection) }) {
                        Icon(Icons.Default.Store, contentDescription = "Shops")
                    }
                }
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { viewModel.onEvent(DashboardEvent.NavigateToPOS) },
                icon = { Icon(Icons.Default.PointOfSale, contentDescription = null) },
                text = { Text("New Sale") }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        when {
            state.isLoading -> LoadingIndicator()
            state.error != null -> ErrorView(
                message = state.error!!,
                onRetry = { viewModel.onEvent(DashboardEvent.Refresh) }
            )
            state.activeShop == null -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("No shop selected")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.onEvent(DashboardEvent.NavigateToShopSelection) }) {
                        Text("Select Shop")
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Cards
                    item {
                        SummarySection(state = state)
                    }

                    // Quick Actions
                    item {
                        QuickActionsSection(
                            onProductsClick = { viewModel.onEvent(DashboardEvent.NavigateToProducts) },
                            onSalesClick = { viewModel.onEvent(DashboardEvent.NavigateToSales) },
                            onCustomersClick = { viewModel.onEvent(DashboardEvent.NavigateToCustomers) },
                            onExpensesClick = { viewModel.onEvent(DashboardEvent.NavigateToExpenses) },
                            onReportsClick = { viewModel.onEvent(DashboardEvent.NavigateToReports) }
                        )
                    }

                    // Recent Sales
                    item {
                        Text(
                            text = "Recent Sales",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (state.recentSales.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                                )
                            ) {
                                Text(
                                    text = "No sales today",
                                    modifier = Modifier.padding(24.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(state.recentSales) { sale ->
                            RecentSaleItem(
                                sale = sale,
                                currency = state.activeShop?.currency ?: "TZS",
                                onClick = { viewModel.onEvent(DashboardEvent.NavigateToSaleDetail(sale.id)) }
                            )
                        }
                    }

                    item { Spacer(modifier = Modifier.height(80.dp)) }
                }
            }
        }
    }
}

@Composable
private fun SummarySection(state: DashboardState) {
    val currency = state.activeShop?.currency ?: "TZS"

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Today's Sales",
                value = CurrencyFormatter.format(state.summary.todaySales.toDouble(), currency),
                icon = Icons.Default.ShoppingCart,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Profit",
                value = CurrencyFormatter.format(state.summary.todayProfit.toDouble(), currency),
                icon = Icons.Default.TrendingUp,
                color = Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SummaryCard(
                title = "Low Stock",
                value = state.summary.lowStockCount.toString(),
                icon = Icons.Default.Warning,
                color = if (state.summary.lowStockCount > 0) Color(0xFFFF9800) else Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
            SummaryCard(
                title = "Total Debt",
                value = CurrencyFormatter.format(state.summary.totalDebt.toDouble(), currency),
                icon = Icons.Default.AccountBalance,
                color = if (state.summary.totalDebt.toDouble() > 0) Color(0xFFF44336) else Color(0xFF4CAF50),
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun SummaryCard(
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
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = color
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun QuickActionsSection(
    onProductsClick: () -> Unit,
    onSalesClick: () -> Unit,
    onCustomersClick: () -> Unit,
    onExpensesClick: () -> Unit,
    onReportsClick: () -> Unit
) {
    Column {
        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            QuickActionButton(
                icon = Icons.Outlined.Inventory2,
                label = "Products",
                onClick = onProductsClick
            )
            QuickActionButton(
                icon = Icons.Outlined.Receipt,
                label = "Sales",
                onClick = onSalesClick
            )
            QuickActionButton(
                icon = Icons.Outlined.People,
                label = "Customers",
                onClick = onCustomersClick
            )
            QuickActionButton(
                icon = Icons.Outlined.Money,
                label = "Expenses",
                onClick = onExpensesClick
            )
            QuickActionButton(
                icon = Icons.Outlined.BarChart,
                label = "Reports",
                onClick = onReportsClick
            )
        }
    }
}

@Composable
private fun QuickActionButton(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        FilledIconButton(
            onClick = onClick,
            modifier = Modifier.size(56.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(imageVector = icon, contentDescription = label)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecentSaleItem(
    sale: Sale,
    currency: String,
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
            Column {
                Text(
                    text = sale.saleNumber,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = sale.customer?.name ?: "Walk-in Customer",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = CurrencyFormatter.format(sale.totalAmount.toDouble(), currency),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = DateTimeUtil.formatDisplayTime(sale.saleDate),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

