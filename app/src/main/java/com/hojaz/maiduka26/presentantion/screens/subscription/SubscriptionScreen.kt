package com.hojaz.maiduka26.presentantion.screens.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hojaz.maiduka26.domain.model.SubscriptionPlan
import com.hojaz.maiduka26.domain.model.SubscriptionStatus
import com.hojaz.maiduka26.domain.usecase.subscription.SubscriptionPricing
import com.hojaz.maiduka26.presentantion.components.*
import com.hojaz.maiduka26.util.CurrencyFormatter
import kotlinx.coroutines.flow.collectLatest

/**
 * Subscription screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    navController: NavController,
    viewModel: SubscriptionViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SubscriptionEffect.NavigateBack -> navController.popBackStack()
                is SubscriptionEffect.NavigateToDashboard -> {
                    navController.popBackStack()
                }
                is SubscriptionEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
                is SubscriptionEffect.OpenUssdPrompt -> {
                    // In production, this would trigger USSD prompt
                }
                is SubscriptionEffect.OpenPaymentUrl -> {
                    // Open payment URL in browser
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(SubscriptionEvent.NavigateBack) }) {
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
                onRetry = { viewModel.onEvent(SubscriptionEvent.LoadSubscription) }
            )
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Current subscription status
                    state.currentSubscription?.let { subscription ->
                        CurrentSubscriptionBanner(
                            subscription = subscription,
                            daysRemaining = state.daysRemaining,
                            onCancel = { viewModel.onEvent(SubscriptionEvent.ShowCancelDialog) }
                        )
                    }

                    // Billing cycle toggle
                    BillingCycleToggle(
                        isAnnual = state.isAnnual,
                        onToggle = { viewModel.onEvent(SubscriptionEvent.ToggleBillingCycle(it)) }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Plan cards
                    PlanSelectionSection(
                        selectedPlan = state.selectedPlan,
                        isAnnual = state.isAnnual,
                        currentPlan = state.currentSubscription?.plan,
                        onSelectPlan = { viewModel.onEvent(SubscriptionEvent.SelectPlan(it)) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Payment section
                    if (state.selectedPlan != SubscriptionPlan.FREE) {
                        PaymentMethodSection(
                            selectedMethod = state.selectedPaymentMethod,
                            phoneNumber = state.phoneNumber,
                            calculatedPrice = state.calculatedPrice.toDouble(),
                            onSelectMethod = { viewModel.onEvent(SubscriptionEvent.SelectPaymentMethod(it)) },
                            onPhoneChange = { viewModel.onEvent(SubscriptionEvent.UpdatePhoneNumber(it)) },
                            onPay = { viewModel.onEvent(SubscriptionEvent.InitiatePayment) },
                            isProcessing = state.isProcessingPayment
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

        // Payment confirmation dialog
        if (state.showPaymentDialog) {
            PaymentConfirmationDialog(
                paymentInitiation = state.paymentInitiation,
                isProcessing = state.isProcessingPayment,
                onConfirm = { viewModel.onEvent(SubscriptionEvent.ConfirmPayment) },
                onDismiss = { viewModel.onEvent(SubscriptionEvent.HidePaymentDialog) }
            )
        }

        // Success dialog
        if (state.showSuccessDialog) {
            SuccessDialog(
                onDismiss = {
                    viewModel.onEvent(SubscriptionEvent.HideSuccessDialog)
                    viewModel.onEvent(SubscriptionEvent.NavigateBack)
                }
            )
        }

        // Cancel confirmation dialog
        if (state.showConfirmCancelDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(SubscriptionEvent.HideCancelDialog) },
                title = { Text("Cancel Subscription?") },
                text = {
                    Text("Are you sure you want to cancel your subscription? You will lose access to premium features at the end of your billing period.")
                },
                confirmButton = {
                    TextButton(
                        onClick = { viewModel.onEvent(SubscriptionEvent.CancelSubscription) },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Cancel Subscription")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(SubscriptionEvent.HideCancelDialog) }) {
                        Text("Keep Subscription")
                    }
                }
            )
        }
    }
}

@Composable
private fun CurrentSubscriptionBanner(
    subscription: com.hojaz.maiduka26.domain.model.Subscription,
    daysRemaining: Int,
    onCancel: () -> Unit
) {
    val isActive = subscription.status == SubscriptionStatus.ACTIVE
    val backgroundColor = if (isActive) Color(0xFF4CAF50) else Color(0xFFFF9800)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isActive) Icons.Default.CheckCircle else Icons.Default.Warning,
                        contentDescription = null,
                        tint = backgroundColor
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${subscription.plan.name} Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    text = if (isActive) "$daysRemaining days remaining" else "Expired",
                    style = MaterialTheme.typography.bodySmall,
                    color = backgroundColor
                )
            }

            if (isActive) {
                TextButton(
                    onClick = onCancel,
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Cancel")
                }
            }
        }
    }
}

@Composable
private fun BillingCycleToggle(
    isAnnual: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            FilterChip(
                selected = !isAnnual,
                onClick = { onToggle(false) },
                label = { Text("Monthly") },
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            FilterChip(
                selected = isAnnual,
                onClick = { onToggle(true) },
                label = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("Annual")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Save 17%",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50)
                        )
                    }
                },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun PlanSelectionSection(
    selectedPlan: SubscriptionPlan,
    isAnnual: Boolean,
    currentPlan: SubscriptionPlan?,
    onSelectPlan: (SubscriptionPlan) -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SubscriptionPlan.entries.filter { it != SubscriptionPlan.FREE }.forEach { plan ->
            val isSelected = selectedPlan == plan
            val isCurrent = currentPlan == plan
            val price = SubscriptionPricing.getPrice(plan, isAnnual)
            val monthlyEquivalent = if (isAnnual) {
                price.divide(java.math.BigDecimal(12), 0, java.math.RoundingMode.HALF_UP)
            } else price
            val features = SubscriptionPricing.getFeatures(plan)

            PlanCard(
                planName = plan.name,
                monthlyPrice = monthlyEquivalent.toDouble(),
                totalPrice = price.toDouble(),
                isAnnual = isAnnual,
                features = features,
                isSelected = isSelected,
                isCurrent = isCurrent,
                isRecommended = plan == SubscriptionPlan.BASIC,
                onClick = { onSelectPlan(plan) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PlanCard(
    planName: String,
    monthlyPrice: Double,
    totalPrice: Double,
    isAnnual: Boolean,
    features: List<String>,
    isSelected: Boolean,
    isCurrent: Boolean,
    isRecommended: Boolean,
    onClick: () -> Unit
) {
    val borderColor = when {
        isSelected -> MaterialTheme.colorScheme.primary
        isRecommended -> Color(0xFF4CAF50)
        else -> MaterialTheme.colorScheme.outline
    }

    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            else
                MaterialTheme.colorScheme.surface
        )
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
                            text = "MOST POPULAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = planName,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (isCurrent) {
                    AssistChip(
                        onClick = {},
                        label = { Text("Current") },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.2f)
                        )
                    )
                }

                if (isSelected && !isCurrent) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = CurrencyFormatter.format(monthlyPrice, "TZS", false),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "/month",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            if (isAnnual) {
                Text(
                    text = "Billed ${CurrencyFormatter.format(totalPrice, "TZS", false)} annually",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            features.take(4).forEach { feature ->
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

            if (features.size > 4) {
                Text(
                    text = "+${features.size - 4} more features",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}

@Composable
private fun PaymentMethodSection(
    selectedMethod: PaymentMethod,
    phoneNumber: String,
    calculatedPrice: Double,
    onSelectMethod: (PaymentMethod) -> Unit,
    onPhoneChange: (String) -> Unit,
    onPay: () -> Unit,
    isProcessing: Boolean
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        Text(
            text = "Payment Method",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Payment method chips
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(PaymentMethod.entries) { method ->
                FilterChip(
                    selected = selectedMethod == method,
                    onClick = { onSelectMethod(method) },
                    label = { Text(method.displayName) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Phone number input for mobile money
        if (selectedMethod != PaymentMethod.CARD) {
            AppTextField(
                value = phoneNumber,
                onValueChange = onPhoneChange,
                label = "Phone Number",
                placeholder = "0712 345 678",
                leadingIcon = {
                    Icon(Icons.Default.Phone, contentDescription = null)
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Total and pay button
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
                        text = "Total",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = CurrencyFormatter.format(calculatedPrice, "TZS"),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = onPay,
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isProcessing
                ) {
                    if (isProcessing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = MaterialTheme.colorScheme.onPrimary,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.Payment, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Pay with ${selectedMethod.displayName}")
                    }
                }
            }
        }
    }
}

@Composable
private fun PaymentConfirmationDialog(
    paymentInitiation: com.hojaz.maiduka26.domain.usecase.subscription.PaymentInitiation?,
    isProcessing: Boolean,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Complete Payment") },
        text = {
            Column {
                paymentInitiation?.let {
                    Text("Reference: ${it.referenceNumber}")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Amount: ${CurrencyFormatter.format(it.amount.toDouble(), it.currency)}")
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Please complete the payment on your phone and tap 'Confirm' when done.",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                enabled = !isProcessing
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Confirm Payment")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun SuccessDialog(
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.CheckCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = Color(0xFF4CAF50)
            )
        },
        title = {
            Text(
                text = "Subscription Activated!",
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = "Thank you for subscribing to MaiDuka. Enjoy all the premium features!",
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Continue")
            }
        }
    )
}

