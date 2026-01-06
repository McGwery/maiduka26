package com.hojaz.maiduka26.presentantion.screens.purchase

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hojaz.maiduka26.domain.model.PurchaseOrder
import com.hojaz.maiduka26.domain.model.PurchaseOrderStatus
import com.hojaz.maiduka26.presentantion.components.*
import com.hojaz.maiduka26.presentantion.navigation.Screen
import com.hojaz.maiduka26.util.CurrencyFormatter
import com.hojaz.maiduka26.util.DateTimeUtil
import kotlinx.coroutines.flow.collectLatest

/**
 * Purchase Order List screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseOrderListScreen(
    navController: NavController,
    viewModel: PurchaseOrderListViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PurchaseOrderListEffect.NavigateToCreateOrder -> {
                    navController.navigate(Screen.CreatePurchaseOrder.route)
                }
                is PurchaseOrderListEffect.NavigateToOrderDetail -> {
                    navController.navigate(Screen.PurchaseOrderDetail.createRoute(effect.orderId))
                }
                is PurchaseOrderListEffect.NavigateBack -> navController.popBackStack()
                is PurchaseOrderListEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Purchase Orders") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(PurchaseOrderListEvent.NavigateBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(PurchaseOrderListEvent.NavigateToCreateOrder) }
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Order")
            }
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab row
            TabRow(selectedTabIndex = state.selectedTab.ordinal) {
                PurchaseTab.entries.forEach { tab ->
                    Tab(
                        selected = state.selectedTab == tab,
                        onClick = { viewModel.onEvent(PurchaseOrderListEvent.SelectTab(tab)) },
                        text = {
                            Text(
                                text = when (tab) {
                                    PurchaseTab.BUYING -> "Orders I Made"
                                    PurchaseTab.SELLING -> "Orders Received"
                                }
                            )
                        }
                    )
                }
            }

            // Status filter chips
            LazyRow(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    FilterChip(
                        selected = state.filterStatus == null,
                        onClick = { viewModel.onEvent(PurchaseOrderListEvent.FilterByStatus(null)) },
                        label = { Text("All") }
                    )
                }
                items(PurchaseOrderStatus.entries) { status ->
                    FilterChip(
                        selected = state.filterStatus == status,
                        onClick = { viewModel.onEvent(PurchaseOrderListEvent.FilterByStatus(status)) },
                        label = { Text(status.name) }
                    )
                }
            }

            // Order list
            val orders = when (state.selectedTab) {
                PurchaseTab.BUYING -> state.purchaseOrdersAsBuyer
                PurchaseTab.SELLING -> state.purchaseOrdersAsSeller
            }.let { list ->
                if (state.filterStatus != null) {
                    list.filter { it.status == state.filterStatus }
                } else list
            }

            when {
                state.isLoading -> LoadingIndicator()
                state.error != null -> ErrorView(
                    message = state.error!!,
                    onRetry = { viewModel.onEvent(PurchaseOrderListEvent.Refresh) }
                )
                orders.isEmpty() -> {
                    EmptyView(
                        title = "No Orders Yet",
                        message = if (state.selectedTab == PurchaseTab.BUYING)
                            "Create your first purchase order"
                        else
                            "You haven't received any orders yet",
                        icon = {
                            Icon(
                                imageVector = Icons.Default.ShoppingBag,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(orders, key = { it.id }) { order ->
                            PurchaseOrderCard(
                                order = order,
                                currency = state.activeShop?.currency ?: "TZS",
                                isBuyer = state.selectedTab == PurchaseTab.BUYING,
                                onClick = {
                                    viewModel.onEvent(PurchaseOrderListEvent.NavigateToOrderDetail(order.id))
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PurchaseOrderCard(
    order: PurchaseOrder,
    currency: String,
    isBuyer: Boolean,
    onClick: () -> Unit
) {
    val statusColor = when (order.status) {
        PurchaseOrderStatus.PENDING -> Color(0xFFFF9800)
        PurchaseOrderStatus.APPROVED -> Color(0xFF2196F3)
        PurchaseOrderStatus.REJECTED -> Color(0xFFF44336)
        PurchaseOrderStatus.COMPLETED -> Color(0xFF4CAF50)
        PurchaseOrderStatus.CANCELLED -> Color(0xFF9E9E9E)
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.referenceNumber,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    order.createdAt?.let {
                        Text(
                            text = DateTimeUtil.formatDisplayDate(it),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                AssistChip(
                    onClick = {},
                    label = { Text(order.status.name) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = statusColor.copy(alpha = 0.2f),
                        labelColor = statusColor
                    )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Items",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${order.itemCount} items",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Total",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = CurrencyFormatter.format(order.totalAmount.toDouble(), currency),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Payment progress
            if (order.status == PurchaseOrderStatus.APPROVED || order.status == PurchaseOrderStatus.COMPLETED) {
                Spacer(modifier = Modifier.height(8.dp))

                val progress = if (order.totalAmount > java.math.BigDecimal.ZERO) {
                    (order.totalPaid.toFloat() / order.totalAmount.toFloat()).coerceIn(0f, 1f)
                } else 0f

                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Paid: ${CurrencyFormatter.format(order.totalPaid.toDouble(), currency)}",
                            style = MaterialTheme.typography.labelSmall
                        )
                        Text(
                            text = "${(progress * 100).toInt()}%",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth(),
                        color = if (progress >= 1f) Color(0xFF4CAF50) else MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

/**
 * Purchase Order Detail screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PurchaseOrderDetailScreen(
    orderId: String,
    navController: NavController,
    viewModel: PurchaseOrderDetailViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(orderId) {
        viewModel.onEvent(PurchaseOrderDetailEvent.LoadOrder(orderId))
    }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is PurchaseOrderDetailEffect.NavigateBack -> navController.popBackStack()
                is PurchaseOrderDetailEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.order?.referenceNumber ?: "Order Details") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.NavigateBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                onRetry = { viewModel.onEvent(PurchaseOrderDetailEvent.Refresh) }
            )
            state.order != null -> {
                val order = state.order!!

                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Order status card
                    item {
                        OrderStatusCard(order = order)
                    }

                    // Order summary
                    item {
                        OrderSummaryCard(
                            order = order,
                            currency = "TZS"
                        )
                    }

                    // Items
                    item {
                        Text(
                            text = "Order Items",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    items(state.items) { item ->
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Product #${item.productId.take(8)}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = "${item.quantity} x ${CurrencyFormatter.format(item.unitPrice.toDouble(), "TZS")}",
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                                Text(
                                    text = CurrencyFormatter.format(item.totalPrice.toDouble(), "TZS"),
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    // Actions
                    item {
                        OrderActionsCard(
                            order = order,
                            isCurrentShopBuyer = state.isCurrentShopBuyer,
                            isProcessing = state.isProcessing,
                            onApprove = { viewModel.onEvent(PurchaseOrderDetailEvent.ShowApproveDialog) },
                            onReject = { viewModel.onEvent(PurchaseOrderDetailEvent.ShowRejectDialog) },
                            onAddPayment = { viewModel.onEvent(PurchaseOrderDetailEvent.ShowPaymentDialog) }
                        )
                    }
                }
            }
        }

        // Approve dialog
        if (state.showApproveDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(PurchaseOrderDetailEvent.HideApproveDialog) },
                title = { Text("Approve Order?") },
                text = { Text("Are you sure you want to approve this purchase order?") },
                confirmButton = {
                    Button(onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.ApproveOrder) }) {
                        Text("Approve")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.HideApproveDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Reject dialog
        if (state.showRejectDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(PurchaseOrderDetailEvent.HideRejectDialog) },
                title = { Text("Reject Order") },
                text = {
                    Column {
                        Text("Please provide a reason for rejection:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = state.rejectReason,
                            onValueChange = { viewModel.onEvent(PurchaseOrderDetailEvent.UpdateRejectReason(it)) },
                            placeholder = { Text("Reason...") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.RejectOrder) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Reject")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.HideRejectDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Payment dialog
        if (state.showPaymentDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(PurchaseOrderDetailEvent.HidePaymentDialog) },
                title = { Text("Add Payment") },
                text = {
                    Column {
                        OutlinedTextField(
                            value = state.paymentAmount,
                            onValueChange = { viewModel.onEvent(PurchaseOrderDetailEvent.UpdatePaymentAmount(it)) },
                            label = { Text("Amount") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text("Payment Method", style = MaterialTheme.typography.labelMedium)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = state.paymentMethod == "cash",
                                onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.UpdatePaymentMethod("cash")) },
                                label = { Text("Cash") }
                            )
                            FilterChip(
                                selected = state.paymentMethod == "mobile",
                                onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.UpdatePaymentMethod("mobile")) },
                                label = { Text("Mobile") }
                            )
                            FilterChip(
                                selected = state.paymentMethod == "bank",
                                onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.UpdatePaymentMethod("bank")) },
                                label = { Text("Bank") }
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.AddPayment) }) {
                        Text("Add Payment")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(PurchaseOrderDetailEvent.HidePaymentDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@Composable
private fun OrderStatusCard(order: PurchaseOrder) {
    val statusColor = when (order.status) {
        PurchaseOrderStatus.PENDING -> Color(0xFFFF9800)
        PurchaseOrderStatus.APPROVED -> Color(0xFF2196F3)
        PurchaseOrderStatus.REJECTED -> Color(0xFFF44336)
        PurchaseOrderStatus.COMPLETED -> Color(0xFF4CAF50)
        PurchaseOrderStatus.CANCELLED -> Color(0xFF9E9E9E)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = statusColor.copy(alpha = 0.1f)
        )
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
                    text = "Status",
                    style = MaterialTheme.typography.labelSmall
                )
                Text(
                    text = order.status.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = statusColor
                )
            }

            Icon(
                imageVector = when (order.status) {
                    PurchaseOrderStatus.PENDING -> Icons.Default.Schedule
                    PurchaseOrderStatus.APPROVED -> Icons.Default.CheckCircle
                    PurchaseOrderStatus.REJECTED -> Icons.Default.Cancel
                    PurchaseOrderStatus.COMPLETED -> Icons.Default.Done
                    PurchaseOrderStatus.CANCELLED -> Icons.Default.Block
                },
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = statusColor
            )
        }
    }
}

@Composable
private fun OrderSummaryCard(order: PurchaseOrder, currency: String) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Order Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            SummaryRow("Items", "${order.itemCount} items")
            SummaryRow("Total Amount", CurrencyFormatter.format(order.totalAmount.toDouble(), currency))
            SummaryRow("Paid", CurrencyFormatter.format(order.totalPaid.toDouble(), currency))
            SummaryRow(
                "Outstanding",
                CurrencyFormatter.format(order.outstandingAmount.toDouble(), currency),
                valueColor = if (order.outstandingAmount > java.math.BigDecimal.ZERO)
                    Color(0xFFF44336) else Color(0xFF4CAF50)
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: Color = MaterialTheme.colorScheme.onSurface
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Medium,
            color = valueColor
        )
    }
}

@Composable
private fun OrderActionsCard(
    order: PurchaseOrder,
    isCurrentShopBuyer: Boolean,
    isProcessing: Boolean,
    onApprove: () -> Unit,
    onReject: () -> Unit,
    onAddPayment: () -> Unit
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            // Seller can approve/reject pending orders
            if (!isCurrentShopBuyer && order.status == PurchaseOrderStatus.PENDING) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onApprove,
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Approve")
                    }

                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        enabled = !isProcessing,
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject")
                    }
                }
            }

            // Buyer can add payment for approved orders
            if (isCurrentShopBuyer &&
                order.status == PurchaseOrderStatus.APPROVED &&
                !order.isPaid) {
                Button(
                    onClick = onAddPayment,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Payment")
                }
            }
        }
    }
}

