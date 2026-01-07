package com.hojaz.maiduka26.presentantion.screens.subscription

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hojaz.maiduka26.presentantion.components.AppButton
import com.hojaz.maiduka26.presentantion.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

/**
 * Subscription Payment screen for renewing expired subscriptions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionPaymentScreen(
    navController: NavController,
    viewModel: SubscriptionPaymentViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SubscriptionPaymentEffect.NavigateToDashboard -> {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SubscriptionPaymentEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            // Warning Icon
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Subscription Expired",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.error
            )

            Text(
                text = "Your subscription has expired. Please renew to continue using MaiDuka.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp, bottom = 32.dp)
            )

            // Subscription Plans
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Monthly Plan",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "TZS 12,000/month",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Unlimited products\n• Unlimited sales\n• Full reporting\n• Multi-user support",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Methods
            Text(
                text = "Select Payment Method",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // M-Pesa Option
            OutlinedCard(
                onClick = { viewModel.onEvent(SubscriptionPaymentEvent.SelectPaymentMethod("mpesa")) },
                modifier = Modifier.fillMaxWidth(),
                border = CardDefaults.outlinedCardBorder().copy(
                    width = if (state.selectedPaymentMethod == "mpesa") 2.dp else 1.dp
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("M-Pesa", fontWeight = FontWeight.SemiBold)
                        Text("Pay via mobile money", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (state.selectedPaymentMethod == "mpesa") {
                        RadioButton(selected = true, onClick = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Tigo Pesa Option
            OutlinedCard(
                onClick = { viewModel.onEvent(SubscriptionPaymentEvent.SelectPaymentMethod("tigopesa")) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CreditCard,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Tigo Pesa", fontWeight = FontWeight.SemiBold)
                        Text("Pay via mobile money", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    if (state.selectedPaymentMethod == "tigopesa") {
                        RadioButton(selected = true, onClick = null)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Pay Button
            AppButton(
                text = if (state.isLoading) "Processing..." else "Pay TZS 12,000",
                onClick = { viewModel.onEvent(SubscriptionPaymentEvent.ProcessPayment) },
                enabled = state.selectedPaymentMethod.isNotBlank() && !state.isLoading,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            // Error message
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

