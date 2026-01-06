package com.hojaz.maiduka26.presentantion.screens.auth.register

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hojaz.maiduka26.presentantion.components.*
import com.hojaz.maiduka26.presentantion.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

/**
 * Register screen composable with multi-step registration flow.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: RegisterViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is RegisterEffect.NavigateToLogin -> {
                    navController.popBackStack()
                }
                is RegisterEffect.NavigateToDashboard -> {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                is RegisterEffect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(effect.message)
                }
                is RegisterEffect.StartOtpCountdown -> {
                    // Countdown is handled in ViewModel
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = when (state.currentStep) {
                            RegisterStep.ACCOUNT_INFO -> "Create Account"
                            RegisterStep.OTP_VERIFICATION -> "Verify Phone"
                            RegisterStep.SHOP_SETUP -> "Setup Your Shop"
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(RegisterEvent.GoToPreviousStep) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Progress indicator
            LinearProgressIndicator(
                progress = { state.progressPercentage },
                modifier = Modifier.fillMaxWidth()
            )

            // Step indicator
            StepIndicator(currentStep = state.currentStep)

            // Content based on current step
            AnimatedContent(
                targetState = state.currentStep,
                transitionSpec = {
                    if (targetState.ordinal > initialState.ordinal) {
                        slideInHorizontally { width -> width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> -width } + fadeOut()
                    } else {
                        slideInHorizontally { width -> -width } + fadeIn() togetherWith
                                slideOutHorizontally { width -> width } + fadeOut()
                    }
                },
                label = "step_animation"
            ) { step ->
                when (step) {
                    RegisterStep.ACCOUNT_INFO -> AccountInfoStep(state, viewModel::onEvent)
                    RegisterStep.OTP_VERIFICATION -> OtpVerificationStep(state, viewModel::onEvent)
                    RegisterStep.SHOP_SETUP -> ShopSetupStep(state, viewModel::onEvent)
                }
            }
        }
    }
}

@Composable
private fun StepIndicator(currentStep: RegisterStep) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        RegisterStep.entries.forEachIndexed { index, step ->
            val isActive = step == currentStep
            val isCompleted = step.ordinal < currentStep.ordinal

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    shape = MaterialTheme.shapes.extraLarge,
                    color = when {
                        isActive -> MaterialTheme.colorScheme.primary
                        isCompleted -> MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (isCompleted) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                        } else {
                            Text(
                                text = "${index + 1}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = if (isActive) MaterialTheme.colorScheme.onPrimary
                                       else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = when (step) {
                        RegisterStep.ACCOUNT_INFO -> "Account"
                        RegisterStep.OTP_VERIFICATION -> "Verify"
                        RegisterStep.SHOP_SETUP -> "Shop"
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isActive) MaterialTheme.colorScheme.primary
                           else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun AccountInfoStep(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Welcome text
        Text(
            text = "Join MaiDuka",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Create your account to get started",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Full Name
        AppTextField(
            value = state.fullName,
            onValueChange = { onEvent(RegisterEvent.UpdateFullName(it)) },
            label = "Full Name",
            placeholder = "Enter your full name",
            isError = state.fullNameError != null,
            errorMessage = state.fullNameError,
            leadingIcon = {
                Icon(Icons.Default.Person, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Email
        AppTextField(
            value = state.email,
            onValueChange = { onEvent(RegisterEvent.UpdateEmail(it)) },
            label = "Email (Optional)",
            placeholder = "Enter your email",
            isError = state.emailError != null,
            errorMessage = state.emailError,
            leadingIcon = {
                Icon(Icons.Default.Email, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Phone Number
        AppTextField(
            value = state.phoneNumber,
            onValueChange = { onEvent(RegisterEvent.UpdatePhoneNumber(it)) },
            label = "Phone Number",
            placeholder = "0712 345 678",
            isError = state.phoneError != null,
            errorMessage = state.phoneError,
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Password
        AppPasswordField(
            value = state.password,
            onValueChange = { onEvent(RegisterEvent.UpdatePassword(it)) },
            label = "Password",
            placeholder = "Create a password",
            isError = state.passwordError != null,
            errorMessage = state.passwordError,
            imeAction = ImeAction.Next
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Confirm Password
        AppPasswordField(
            value = state.confirmPassword,
            onValueChange = { onEvent(RegisterEvent.UpdateConfirmPassword(it)) },
            label = "Confirm Password",
            placeholder = "Confirm your password",
            isError = state.confirmPasswordError != null,
            errorMessage = state.confirmPasswordError,
            imeAction = ImeAction.Done,
            onImeAction = { onEvent(RegisterEvent.SubmitAccountInfo) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error message
        state.error?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue button
        AppButton(
            text = "Continue",
            onClick = { onEvent(RegisterEvent.SubmitAccountInfo) },
            isLoading = state.isLoading || state.isSendingOtp,
            enabled = !state.isLoading && !state.isSendingOtp
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Login link
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account?",
                style = MaterialTheme.typography.bodyMedium
            )
            AppTextButton(
                text = "Login",
                onClick = { onEvent(RegisterEvent.NavigateToLogin) }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun OtpVerificationStep(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(32.dp))

        // Icon
        Surface(
            modifier = Modifier.size(80.dp),
            shape = MaterialTheme.shapes.extraLarge,
            color = MaterialTheme.colorScheme.primaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = Icons.Default.Sms,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Verify Your Phone",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We sent a 6-digit code to",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = state.phoneNumber.ifBlank { state.email },
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(32.dp))

        // OTP Input
        OtpInputField(
            otpCode = state.otpCode,
            onOtpChange = { onEvent(RegisterEvent.UpdateOtpCode(it)) },
            isError = state.otpError != null,
            errorMessage = state.otpError
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Resend OTP
        if (state.canResendOtp) {
            AppTextButton(
                text = "Resend Code",
                onClick = { onEvent(RegisterEvent.ResendOtp) },
                enabled = !state.isSendingOtp
            )
        } else {
            Text(
                text = "Resend code in ${state.otpResendCountdown}s",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Verify button
        AppButton(
            text = "Verify",
            onClick = { onEvent(RegisterEvent.VerifyOtp) },
            isLoading = state.isVerifyingOtp,
            enabled = state.otpCode.length == 6 && !state.isVerifyingOtp
        )

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ShopSetupStep(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit
) {
    var showBusinessTypeDialog by remember { mutableStateOf(false) }
    var showCurrencyDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Welcome text
        Text(
            text = "Setup Your Shop",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Tell us about your business",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Shop Name
        AppTextField(
            value = state.shopName,
            onValueChange = { onEvent(RegisterEvent.UpdateShopName(it)) },
            label = "Shop Name *",
            placeholder = "e.g., Mama Shida's Shop",
            isError = state.shopNameError != null,
            errorMessage = state.shopNameError,
            leadingIcon = {
                Icon(Icons.Default.Store, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Business Type (Dropdown)
        OutlinedTextField(
            value = state.businessType,
            onValueChange = {},
            readOnly = true,
            label = { Text("Business Type *") },
            placeholder = { Text("Select your business type") },
            isError = state.businessTypeError != null,
            supportingText = state.businessTypeError?.let { { Text(it) } },
            leadingIcon = {
                Icon(Icons.Default.Business, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = { showBusinessTypeDialog = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shop Address
        AppTextField(
            value = state.shopAddress,
            onValueChange = { onEvent(RegisterEvent.UpdateShopAddress(it)) },
            label = "Shop Address (Optional)",
            placeholder = "e.g., Kariakoo Market, Dar es Salaam",
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            singleLine = false,
            maxLines = 2
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Shop Phone Number
        AppTextField(
            value = state.shopPhoneNumber,
            onValueChange = { onEvent(RegisterEvent.UpdateShopPhoneNumber(it)) },
            label = "Shop Phone (Optional)",
            placeholder = "Business contact number",
            leadingIcon = {
                Icon(Icons.Default.Phone, contentDescription = null)
            },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Currency (Dropdown)
        OutlinedTextField(
            value = state.shopCurrency,
            onValueChange = {},
            readOnly = true,
            label = { Text("Currency") },
            leadingIcon = {
                Icon(Icons.Default.AttachMoney, contentDescription = null)
            },
            trailingIcon = {
                IconButton(onClick = { showCurrencyDialog = true }) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = "Select")
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Error message
        state.error?.let { error ->
            Text(
                text = error,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Create Shop button
        AppButton(
            text = "Create Shop & Get Started",
            onClick = { onEvent(RegisterEvent.CreateShop) },
            isLoading = state.isCreatingShop,
            enabled = !state.isCreatingShop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Info text
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            )
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.Top
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "You can add more shops and customize settings later from the app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Business Type Dialog
    if (showBusinessTypeDialog) {
        AlertDialog(
            onDismissRequest = { showBusinessTypeDialog = false },
            title = { Text("Select Business Type") },
            text = {
                Column {
                    BUSINESS_TYPES.forEach { type ->
                        Surface(
                            onClick = {
                                onEvent(RegisterEvent.UpdateBusinessType(type))
                                showBusinessTypeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.businessType == type,
                                    onClick = {
                                        onEvent(RegisterEvent.UpdateBusinessType(type))
                                        showBusinessTypeDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(text = type)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showBusinessTypeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Currency Dialog
    if (showCurrencyDialog) {
        AlertDialog(
            onDismissRequest = { showCurrencyDialog = false },
            title = { Text("Select Currency") },
            text = {
                Column {
                    CURRENCIES.forEach { (code, name) ->
                        Surface(
                            onClick = {
                                onEvent(RegisterEvent.UpdateShopCurrency(code))
                                showCurrencyDialog = false
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 12.dp, horizontal = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = state.shopCurrency == code,
                                    onClick = {
                                        onEvent(RegisterEvent.UpdateShopCurrency(code))
                                        showCurrencyDialog = false
                                    }
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(text = code, fontWeight = FontWeight.Medium)
                                    Text(
                                        text = name,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showCurrencyDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
private fun OtpInputField(
    otpCode: String,
    onOtpChange: (String) -> Unit,
    isError: Boolean,
    errorMessage: String?
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        OutlinedTextField(
            value = otpCode,
            onValueChange = { if (it.length <= 6 && it.all { c -> c.isDigit() }) onOtpChange(it) },
            label = { Text("Enter OTP Code") },
            isError = isError,
            supportingText = errorMessage?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done
            ),
            singleLine = true,
            textStyle = MaterialTheme.typography.headlineSmall.copy(
                textAlign = TextAlign.Center,
                letterSpacing = 8.sp
            ),
            modifier = Modifier.width(200.dp)
        )
    }
}

private val Int.sp get() = androidx.compose.ui.unit.TextUnit(this.toFloat(), androidx.compose.ui.unit.TextUnitType.Sp)

