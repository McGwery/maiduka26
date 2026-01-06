package com.hojaz.maiduka26.presentantion.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.hojaz.maiduka26.domain.model.SubscriptionStatus
import com.hojaz.maiduka26.presentantion.components.LoadingIndicator
import com.hojaz.maiduka26.presentantion.navigation.Screen
import kotlinx.coroutines.flow.collectLatest

/**
 * Settings screen composable.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(key1 = true) {
        viewModel.effect.collectLatest { effect ->
            when (effect) {
                is SettingsEffect.NavigateBack -> navController.popBackStack()
                is SettingsEffect.NavigateToProfile -> navController.navigate(Screen.Profile.route)
                is SettingsEffect.NavigateToShopSettings -> {
                    state.activeShop?.let {
                        navController.navigate(Screen.ShopSettings.createRoute(it.id))
                    }
                }
                is SettingsEffect.NavigateToSubscription -> navController.navigate(Screen.Subscription.route)
                is SettingsEffect.NavigateToShopMembers -> {
                    state.activeShop?.let {
                        navController.navigate(Screen.ShopMembers.createRoute(it.id))
                    }
                }
                is SettingsEffect.NavigateToLogin -> {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(0) { inclusive = true }
                    }
                }
                is SettingsEffect.ShowSnackbar -> snackbarHostState.showSnackbar(effect.message)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = { viewModel.onEvent(SettingsEvent.NavigateBack) }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        if (state.isLoading) {
            LoadingIndicator()
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
            ) {
                // User profile section
                UserProfileSection(
                    userName = state.user?.name ?: "User",
                    userEmail = state.user?.email ?: "",
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToProfile) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Shop section
                SettingsSectionTitle("Shop")

                SettingsItem(
                    icon = Icons.Outlined.Store,
                    title = "Shop Settings",
                    subtitle = state.activeShop?.name ?: "No shop selected",
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToShopSettings) }
                )

                SettingsItem(
                    icon = Icons.Outlined.People,
                    title = "Team Members",
                    subtitle = "Manage staff and permissions",
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToShopMembers) }
                )

                // Subscription
                SettingsItem(
                    icon = Icons.Outlined.CreditCard,
                    title = "Subscription",
                    subtitle = if (state.subscription?.status == SubscriptionStatus.ACTIVE) {
                        "${state.subscription?.plan?.name} - ${state.daysRemaining} days left"
                    } else {
                        "No active subscription"
                    },
                    badge = if (state.daysRemaining <= 7 && state.daysRemaining > 0) {
                        "Expiring Soon"
                    } else null,
                    badgeColor = Color(0xFFFF9800),
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToSubscription) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Sync section
                SettingsSectionTitle("Data & Sync")

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (state.isOnline) Icons.Default.Cloud else Icons.Default.CloudOff,
                            contentDescription = null,
                            tint = if (state.isOnline) Color(0xFF4CAF50) else Color(0xFFFF9800)
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (state.isOnline) "Online" else "Offline",
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (state.pendingSyncCount > 0) {
                                Text(
                                    text = "${state.pendingSyncCount} items pending sync",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    FilledTonalButton(
                        onClick = { viewModel.onEvent(SettingsEvent.SyncNow) },
                        enabled = state.isOnline
                    ) {
                        Icon(Icons.Default.Sync, contentDescription = null)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Now")
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // App settings
                SettingsSectionTitle("App Settings")

                SettingsSwitch(
                    icon = Icons.Outlined.DarkMode,
                    title = "Dark Mode",
                    subtitle = "Use dark theme",
                    checked = state.isDarkMode,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleDarkMode(it)) }
                )

                SettingsSwitch(
                    icon = Icons.Outlined.Notifications,
                    title = "Notifications",
                    subtitle = "Receive push notifications",
                    checked = state.notifications,
                    onCheckedChange = { viewModel.onEvent(SettingsEvent.ToggleNotifications(it)) }
                )

                SettingsItem(
                    icon = Icons.Outlined.Language,
                    title = "Language",
                    subtitle = state.language,
                    onClick = { /* Show language picker */ }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Support
                SettingsSectionTitle("Support")

                SettingsItem(
                    icon = Icons.Outlined.Help,
                    title = "Help & FAQ",
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToHelp) }
                )

                SettingsItem(
                    icon = Icons.Outlined.Info,
                    title = "About",
                    subtitle = "Version 1.0.0",
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToAbout) }
                )

                SettingsItem(
                    icon = Icons.Outlined.Policy,
                    title = "Privacy Policy",
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToPrivacyPolicy) }
                )

                SettingsItem(
                    icon = Icons.Outlined.Description,
                    title = "Terms of Service",
                    onClick = { viewModel.onEvent(SettingsEvent.NavigateToTerms) }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                // Account
                SettingsSectionTitle("Account")

                SettingsItem(
                    icon = Icons.Outlined.Logout,
                    title = "Logout",
                    titleColor = MaterialTheme.colorScheme.error,
                    onClick = { viewModel.onEvent(SettingsEvent.ShowLogoutDialog) }
                )

                SettingsItem(
                    icon = Icons.Outlined.DeleteForever,
                    title = "Delete Account",
                    titleColor = MaterialTheme.colorScheme.error,
                    subtitle = "Permanently delete your account and data",
                    onClick = { viewModel.onEvent(SettingsEvent.ShowDeleteAccountDialog) }
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }

        // Logout dialog
        if (state.showLogoutDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(SettingsEvent.HideLogoutDialog) },
                icon = { Icon(Icons.Default.Logout, contentDescription = null) },
                title = { Text("Logout") },
                text = { Text("Are you sure you want to logout?") },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(SettingsEvent.Logout) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Logout")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(SettingsEvent.HideLogoutDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }

        // Delete account dialog
        if (state.showDeleteAccountDialog) {
            AlertDialog(
                onDismissRequest = { viewModel.onEvent(SettingsEvent.HideDeleteAccountDialog) },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = { Text("Delete Account") },
                text = {
                    Text("This action cannot be undone. All your data will be permanently deleted.")
                },
                confirmButton = {
                    Button(
                        onClick = { viewModel.onEvent(SettingsEvent.DeleteAccount) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        )
                    ) {
                        Text("Delete Account")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { viewModel.onEvent(SettingsEvent.HideDeleteAccountDialog) }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun UserProfileSection(
    userName: String,
    userEmail: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape),
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = userName.take(2).uppercase(),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = userName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = userEmail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null
            )
        }
    }
}

@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsItem(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    badge: String? = null,
    badgeColor: Color = MaterialTheme.colorScheme.error,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.bodyLarge,
                        color = titleColor
                    )

                    if (badge != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = badgeColor.copy(alpha = 0.2f),
                            shape = MaterialTheme.shapes.small
                        ) {
                            Text(
                                text = badge,
                                style = MaterialTheme.typography.labelSmall,
                                color = badgeColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }

                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsSwitch(
    icon: ImageVector,
    title: String,
    subtitle: String? = null,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyLarge
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

