package com.hojaz.maiduka26.presentantion.screens.shop.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
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
import com.hojaz.maiduka26.domain.model.MemberRole
import com.hojaz.maiduka26.domain.model.ShopMember
import com.hojaz.maiduka26.domain.model.Subscription
import com.hojaz.maiduka26.domain.model.SubscriptionStatus
import com.hojaz.maiduka26.presentantion.components.*
import com.hojaz.maiduka26.presentantion.navigation.Screen
import com.hojaz.maiduka26.util.CurrencyFormatter
import kotlinx.coroutines.flow.collectLatest

/**
 * Shop Settings screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShopSettingsScreen(
    navController: NavController,
    viewModel: ShopSettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is ShopSettingsEffect.NavigateBack -> navController.popBackStack()
                is ShopSettingsEffect.NavigateToSubscription -> {
                    navController.navigate(Screen.Settings.route + "/subscription")
                }
                is ShopSettingsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is ShopSettingsEffect.SettingsSaved -> { /* handled by snackbar */ }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Settings") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(ShopSettingsEvent.NavigateBack) }) {
                        @Suppress("DEPRECATION")
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (state.isSaving) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        IconButton(onClick = { viewModel.onEvent(ShopSettingsEvent.SaveSettings) }) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
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
                onRetry = { viewModel.onEvent(ShopSettingsEvent.LoadSettings) }
            )
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    // Tab Row
                    ScrollableTabRow(
                        selectedTabIndex = state.selectedTab.ordinal,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        SettingsTab.entries.forEach { tab ->
                            Tab(
                                selected = state.selectedTab == tab,
                                onClick = { viewModel.onEvent(ShopSettingsEvent.SelectTab(tab)) },
                                text = { Text(tab.name.replace("_", " ")) }
                            )
                        }
                    }

                    // Tab Content
                    when (state.selectedTab) {
                        SettingsTab.GENERAL -> GeneralSettingsTab(state, viewModel::onEvent)
                        SettingsTab.NOTIFICATIONS -> NotificationSettingsTab(state, viewModel::onEvent)
                        SettingsTab.SALES -> SalesSettingsTab(state, viewModel::onEvent)
                        SettingsTab.INVENTORY -> InventorySettingsTab(state, viewModel::onEvent)
                        SettingsTab.RECEIPT -> ReceiptSettingsTab(state, viewModel::onEvent)
                        SettingsTab.MEMBERS -> MembersTab(state, viewModel::onEvent)
                        SettingsTab.SUBSCRIPTION -> SubscriptionTab(state, viewModel::onEvent)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GeneralSettingsTab(
    state: ShopSettingsState,
    onEvent: (ShopSettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        AppTextField(
            value = state.shopName,
            onValueChange = { onEvent(ShopSettingsEvent.UpdateShopName(it)) },
            label = "Shop Name"
        )

        AppTextField(
            value = state.businessType,
            onValueChange = { onEvent(ShopSettingsEvent.UpdateBusinessType(it)) },
            label = "Business Type"
        )

        AppTextField(
            value = state.phoneNumber,
            onValueChange = { onEvent(ShopSettingsEvent.UpdatePhoneNumber(it)) },
            label = "Phone Number"
        )

        AppTextField(
            value = state.address,
            onValueChange = { onEvent(ShopSettingsEvent.UpdateAddress(it)) },
            label = "Address",
            singleLine = false,
            maxLines = 3
        )

        // Currency selector
        var currencyExpanded by remember { mutableStateOf(false) }
        val currencies = listOf("TZS", "USD", "KES", "UGX", "RWF", "EUR")

        ExposedDropdownMenuBox(
            expanded = currencyExpanded,
            onExpandedChange = { currencyExpanded = it }
        ) {
            OutlinedTextField(
                value = state.currency,
                onValueChange = {},
                readOnly = true,
                label = { Text("Currency") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
            )
            ExposedDropdownMenu(
                expanded = currencyExpanded,
                onDismissRequest = { currencyExpanded = false }
            ) {
                currencies.forEach { currency ->
                    DropdownMenuItem(
                        text = { Text(currency) },
                        onClick = {
                            onEvent(ShopSettingsEvent.UpdateCurrency(currency))
                            currencyExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                    )
                }
            }
        }
    }
}

@Composable
private fun NotificationSettingsTab(
    state: ShopSettingsState,
    onEvent: (ShopSettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Notification Preferences",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SettingsSwitch(
            title = "SMS Notifications",
            description = "Receive important updates via SMS",
            checked = state.enableSmsNotifications,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateSmsNotifications(it)) }
        )

        SettingsSwitch(
            title = "Email Notifications",
            description = "Receive reports and updates via email",
            checked = state.enableEmailNotifications,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateEmailNotifications(it)) }
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Alerts",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SettingsSwitch(
            title = "Low Stock Alerts",
            description = "Get notified when products are running low",
            checked = state.notifyLowStock,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateNotifyLowStock(it)) }
        )

        if (state.notifyLowStock) {
            OutlinedTextField(
                value = state.lowStockThreshold.toString(),
                onValueChange = {
                    it.toIntOrNull()?.let { threshold ->
                        onEvent(ShopSettingsEvent.UpdateLowStockThreshold(threshold))
                    }
                },
                label = { Text("Low Stock Threshold") },
                modifier = Modifier.fillMaxWidth()
            )
        }

        SettingsSwitch(
            title = "Daily Sales Summary",
            description = "Receive daily sales summary at end of day",
            checked = state.notifyDailySalesSummary,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateDailySummary(it)) }
        )
    }
}

@Composable
private fun SalesSettingsTab(
    state: ShopSettingsState,
    onEvent: (ShopSettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Sales Configuration",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SettingsSwitch(
            title = "Allow Credit Sales",
            description = "Allow customers to buy on credit",
            checked = state.allowCreditSales,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateAllowCreditSales(it)) }
        )

        if (state.allowCreditSales) {
            SettingsSwitch(
                title = "Require Customer for Credit",
                description = "Customer must be selected for credit sales",
                checked = state.requireCustomerForCredit,
                onCheckedChange = { onEvent(ShopSettingsEvent.UpdateRequireCustomerForCredit(it)) }
            )
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        SettingsSwitch(
            title = "Allow Discounts",
            description = "Enable discounts on sales",
            checked = state.allowDiscounts,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateAllowDiscounts(it)) }
        )

        if (state.allowDiscounts) {
            OutlinedTextField(
                value = state.maxDiscountPercentage.toPlainString(),
                onValueChange = {
                    it.toBigDecimalOrNull()?.let { percentage ->
                        onEvent(ShopSettingsEvent.UpdateMaxDiscount(percentage))
                    }
                },
                label = { Text("Maximum Discount %") },
                suffix = { Text("%") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InventorySettingsTab(
    state: ShopSettingsState,
    onEvent: (ShopSettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = "Inventory Management",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SettingsSwitch(
            title = "Track Stock",
            description = "Enable inventory tracking for products",
            checked = state.trackStock,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateTrackStock(it)) }
        )

        if (state.trackStock) {
            SettingsSwitch(
                title = "Allow Negative Stock",
                description = "Allow selling when stock is zero",
                checked = state.allowNegativeStock,
                onCheckedChange = { onEvent(ShopSettingsEvent.UpdateAllowNegativeStock(it)) }
            )

            SettingsSwitch(
                title = "Auto-Deduct Stock",
                description = "Automatically reduce stock on sale",
                checked = state.autoDeductStockOnSale,
                onCheckedChange = { onEvent(ShopSettingsEvent.UpdateAutoDeductStock(it)) }
            )
        }
    }
}

@Composable
private fun ReceiptSettingsTab(
    state: ShopSettingsState,
    onEvent: (ShopSettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Receipt Customization",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SettingsSwitch(
            title = "Show Shop Logo",
            description = "Display logo on printed receipts",
            checked = state.showShopLogoOnReceipt,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateShowLogo(it)) }
        )

        AppTextField(
            value = state.receiptHeader,
            onValueChange = { onEvent(ShopSettingsEvent.UpdateReceiptHeader(it)) },
            label = "Receipt Header",
            placeholder = "Thank you for shopping with us!",
            singleLine = false,
            maxLines = 3
        )

        AppTextField(
            value = state.receiptFooter,
            onValueChange = { onEvent(ShopSettingsEvent.UpdateReceiptFooter(it)) },
            label = "Receipt Footer",
            placeholder = "Please visit again!",
            singleLine = false,
            maxLines = 3
        )

        HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

        Text(
            text = "Tax Settings",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SettingsSwitch(
            title = "Show Tax on Receipt",
            description = "Display tax breakdown on receipts",
            checked = state.showTaxOnReceipt,
            onCheckedChange = { onEvent(ShopSettingsEvent.UpdateShowTax(it)) }
        )

        if (state.showTaxOnReceipt) {
            OutlinedTextField(
                value = state.taxPercentage.toPlainString(),
                onValueChange = {
                    it.toBigDecimalOrNull()?.let { percentage ->
                        onEvent(ShopSettingsEvent.UpdateTaxPercentage(percentage))
                    }
                },
                label = { Text("Tax Percentage") },
                suffix = { Text("%") },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun MembersTab(
    state: ShopSettingsState,
    onEvent: (ShopSettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header with add button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Team Members (${state.memberCount})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            FilledTonalButton(
                onClick = { onEvent(ShopSettingsEvent.ShowAddMemberDialog) }
            ) {
                Icon(Icons.Default.PersonAdd, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Add Member")
            }
        }

        if (state.members.isEmpty()) {
            EmptyView(
                title = "No Team Members",
                message = "Add team members to help manage your shop"
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(state.members) { member ->
                    MemberCard(
                        member = member,
                        onEdit = { onEvent(ShopSettingsEvent.ShowEditMemberDialog(member)) },
                        onToggleActive = { active ->
                            onEvent(ShopSettingsEvent.ToggleMemberActive(member.id, active))
                        },
                        onRemove = { onEvent(ShopSettingsEvent.RemoveMember(member.id)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemberCard(
    member: ShopMember,
    onEdit: () -> Unit,
    onToggleActive: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = member.user?.name ?: "Unknown User",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = member.role.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
                if (!member.isActive) {
                    Text(
                        text = "Inactive",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            Row {
                Switch(
                    checked = member.isActive,
                    onCheckedChange = onToggleActive
                )

                IconButton(onClick = onEdit) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }

                if (member.role != MemberRole.OWNER) {
                    IconButton(onClick = onRemove) {
                        Icon(
                            Icons.Default.Delete,
                            contentDescription = "Remove",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SubscriptionTab(
    state: ShopSettingsState,
    onEvent: (ShopSettingsEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Current Subscription Card
        state.subscription?.let { subscription ->
            SubscriptionStatusCard(
                subscription = subscription,
                daysRemaining = state.daysRemaining
            )
        } ?: run {
            NoSubscriptionCard(
                onSubscribe = { onEvent(ShopSettingsEvent.NavigateToSubscription) }
            )
        }

        HorizontalDivider()

        // Upgrade Options
        Text(
            text = "Available Plans",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        SubscriptionPlanCard(
            planName = "Basic",
            price = "TSh 12,000",
            period = "per month",
            features = listOf(
                "Up to 500 products",
                "Up to 3 users",
                "Basic reports",
                "Email support"
            ),
            isCurrentPlan = state.subscription?.plan?.name == "BASIC",
            onSelect = { onEvent(ShopSettingsEvent.NavigateToSubscription) }
        )

        SubscriptionPlanCard(
            planName = "Premium",
            price = "TSh 25,000",
            period = "per month",
            features = listOf(
                "Unlimited products",
                "Up to 10 users",
                "Advanced reports",
                "Priority support",
                "Multi-shop management"
            ),
            isCurrentPlan = state.subscription?.plan?.name == "PREMIUM",
            isRecommended = true,
            onSelect = { onEvent(ShopSettingsEvent.NavigateToSubscription) }
        )

        SubscriptionPlanCard(
            planName = "Enterprise",
            price = "TSh 50,000",
            period = "per month",
            features = listOf(
                "Unlimited everything",
                "24/7 phone support",
                "Custom integrations",
                "White-label option"
            ),
            isCurrentPlan = state.subscription?.plan?.name == "ENTERPRISE",
            onSelect = { onEvent(ShopSettingsEvent.NavigateToSubscription) }
        )
    }
}

@Composable
private fun SubscriptionStatusCard(
    subscription: Subscription,
    daysRemaining: Int
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Current Plan",
                    style = MaterialTheme.typography.labelMedium
                )
                AssistChip(
                    onClick = {},
                    label = {
                        Text(
                            if (subscription.status == SubscriptionStatus.ACTIVE) "Active" else subscription.status.name
                        )
                    },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (subscription.status == SubscriptionStatus.ACTIVE)
                            Color(0xFF4CAF50) else Color(0xFFFF9800)
                    )
                )
            }

            Text(
                text = subscription.plan.name,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = CurrencyFormatter.format(subscription.price.toDouble(), subscription.currency) + "/month"
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (daysRemaining > 0) {
                LinearProgressIndicator(
                    progress = { (daysRemaining / 30f).coerceIn(0f, 1f) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$daysRemaining days remaining",
                    style = MaterialTheme.typography.bodySmall
                )
            } else {
                Text(
                    text = "Subscription expired",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun NoSubscriptionCard(
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "No Active Subscription",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "Subscribe to unlock all features",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onSubscribe) {
                Text("Subscribe Now - TSh 12,000/month")
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SubscriptionPlanCard(
    planName: String,
    price: String,
    period: String,
    features: List<String>,
    isCurrentPlan: Boolean = false,
    isRecommended: Boolean = false,
    onSelect: () -> Unit
) {
    Card(
        onClick = onSelect,
        modifier = Modifier.fillMaxWidth(),
        border = if (isRecommended) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
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
                    if (isRecommended) {
                        Text(
                            text = "RECOMMENDED",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    Text(
                        text = planName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isCurrentPlan) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Current") }
                    )
                }
            }

            Text(
                text = "$price $period",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(12.dp))

            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Color(0xFF4CAF50)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (!isCurrentPlan) {
                OutlinedButton(
                    onClick = onSelect,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Select Plan")
                }
            }
        }
    }
}

@Composable
private fun SettingsSwitch(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

