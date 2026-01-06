package com.hojaz.maiduka26.presentantion.screens.auth.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hojaz.maiduka26.presentantion.components.AppButton
import com.hojaz.maiduka26.presentantion.components.AppPasswordField
import com.hojaz.maiduka26.presentantion.components.AppTextField
import com.hojaz.maiduka26.presentantion.components.AppTextButton
import com.hojaz.maiduka26.presentantion.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

/**
 * Login screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle side effects
    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is LoginEffect.NavigateToHome -> {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
                is LoginEffect.NavigateToRegister -> {
                    navController.navigate(Screen.Register.route)
                }
                is LoginEffect.NavigateToForgotPassword -> {
                    navController.navigate(Screen.ForgotPassword.route)
                }
                is LoginEffect.ShowSnackbar -> {
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
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(80.dp))

            // Logo/Title
            Text(
                text = "MaiDuka",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "POS System",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(48.dp))

            // Login method toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                FilterChip(
                    selected = state.isEmailLogin,
                    onClick = {
                        if (!state.isEmailLogin) viewModel.onEvent(LoginEvent.ToggleLoginMethod)
                    },
                    label = { Text("Email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )

                Spacer(modifier = Modifier.width(12.dp))

                FilterChip(
                    selected = !state.isEmailLogin,
                    onClick = {
                        if (state.isEmailLogin) viewModel.onEvent(LoginEvent.ToggleLoginMethod)
                    },
                    label = { Text("Phone") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Email or Phone field
            if (state.isEmailLogin) {
                AppTextField(
                    value = state.email,
                    onValueChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
                    label = "Email",
                    placeholder = "Enter your email",
                    isError = state.emailError != null,
                    errorMessage = state.emailError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Email,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = null
                        )
                    }
                )
            } else {
                AppTextField(
                    value = state.phone,
                    onValueChange = { viewModel.onEvent(LoginEvent.PhoneChanged(it)) },
                    label = "Phone Number",
                    placeholder = "Enter your phone number",
                    isError = state.phoneError != null,
                    errorMessage = state.phoneError,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Phone,
                        imeAction = ImeAction.Next
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Phone,
                            contentDescription = null
                        )
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Password field
            AppPasswordField(
                value = state.password,
                onValueChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
                label = "Password",
                placeholder = "Enter your password",
                isError = state.passwordError != null,
                errorMessage = state.passwordError,
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.onEvent(LoginEvent.Login) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Forgot password
            Box(modifier = Modifier.fillMaxWidth()) {
                AppTextButton(
                    text = "Forgot Password?",
                    onClick = { viewModel.onEvent(LoginEvent.NavigateToForgotPassword) },
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Error message
            state.error?.let { error ->
                Text(
                    text = error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Login button
            AppButton(
                text = "Login",
                onClick = { viewModel.onEvent(LoginEvent.Login) },
                isLoading = state.isLoading,
                enabled = !state.isLoading
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Register link
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Don't have an account?",
                    style = MaterialTheme.typography.bodyMedium
                )
                AppTextButton(
                    text = "Register",
                    onClick = { viewModel.onEvent(LoginEvent.NavigateToRegister) }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

