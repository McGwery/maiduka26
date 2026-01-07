package com.hojaz.maiduka26.presentantion.screens.shop.create

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Store
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
import com.hojaz.maiduka26.presentantion.components.AppTextField
import com.hojaz.maiduka26.presentantion.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

/**
 * Create Shop screen composable.
 * Used when a logged-in user has no active shop.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateShopScreen(
    navController: NavController,
    viewModel: CreateShopViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is CreateShopEffect.NavigateToDashboard -> {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is CreateShopEffect.NavigateBack -> {
                    navController.popBackStack()
                }
                is CreateShopEffect.ShowSnackbar -> {
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

            // Header Icon
            Icon(
                imageVector = Icons.Default.Store,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Title
            Text(
                text = "Create Your Shop",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Let's set up your first shop to get started",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 8.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Shop Name
            AppTextField(
                value = state.shopName,
                onValueChange = { viewModel.onEvent(CreateShopEvent.UpdateShopName(it)) },
                label = "Shop Name *",
                placeholder = "e.g., My Retail Store",
                isError = state.shopNameError != null,
                errorMessage = state.shopNameError,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Business Type Dropdown
            var businessTypeExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = businessTypeExpanded,
                onExpandedChange = { if (!state.isLoading) businessTypeExpanded = it }
            ) {
                OutlinedTextField(
                    value = BUSINESS_TYPES.find { it.first == state.businessType }?.second ?: "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Business Type *") },
                    placeholder = { Text("Select business type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = businessTypeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    isError = state.businessTypeError != null,
                    supportingText = state.businessTypeError?.let { { Text(it, color = MaterialTheme.colorScheme.error) } },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = !state.isLoading),
                    enabled = !state.isLoading
                )
                ExposedDropdownMenu(
                    expanded = businessTypeExpanded,
                    onDismissRequest = { businessTypeExpanded = false }
                ) {
                    BUSINESS_TYPES.forEach { (value, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.onEvent(CreateShopEvent.UpdateBusinessType(value))
                                businessTypeExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Phone Number
            AppTextField(
                value = state.phoneNumber,
                onValueChange = { viewModel.onEvent(CreateShopEvent.UpdatePhoneNumber(it)) },
                label = "Shop Phone Number",
                placeholder = "+255 XXX XXX XXX",
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Address
            AppTextField(
                value = state.address,
                onValueChange = { viewModel.onEvent(CreateShopEvent.UpdateAddress(it)) },
                label = "Address",
                placeholder = "Enter shop address",
                singleLine = false,
                maxLines = 2,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Currency Dropdown
            var currencyExpanded by remember { mutableStateOf(false) }

            ExposedDropdownMenuBox(
                expanded = currencyExpanded,
                onExpandedChange = { if (!state.isLoading) currencyExpanded = it }
            ) {
                OutlinedTextField(
                    value = CURRENCIES.find { it.first == state.currency }?.second ?: state.currency,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Currency") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = currencyExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = !state.isLoading),
                    enabled = !state.isLoading
                )
                ExposedDropdownMenu(
                    expanded = currencyExpanded,
                    onDismissRequest = { currencyExpanded = false }
                ) {
                    CURRENCIES.forEach { (code, label) ->
                        DropdownMenuItem(
                            text = { Text(label) },
                            onClick = {
                                viewModel.onEvent(CreateShopEvent.UpdateCurrency(code))
                                currencyExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding
                        )
                    }
                }
            }

            // Error message
            state.error?.let { error ->
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Create Shop Button
            AppButton(
                text = if (state.isLoading) "Creating Shop..." else "Create Shop & Continue",
                onClick = { viewModel.onEvent(CreateShopEvent.CreateShop) },
                enabled = state.isValid && !state.isLoading,
                isLoading = state.isLoading,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info text
            Text(
                text = "You'll get a 14-day free trial of all premium features!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

