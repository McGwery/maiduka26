package com.hojaz.maiduka26.presentantion.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.hojaz.maiduka26.presentantion.screens.auth.login.LoginScreen
import com.hojaz.maiduka26.presentantion.screens.auth.register.RegisterScreen
import com.hojaz.maiduka26.presentantion.screens.dashboard.DashboardScreen
import com.hojaz.maiduka26.presentantion.screens.product.list.ProductListScreen
import com.hojaz.maiduka26.presentantion.screens.purchase.PurchaseOrderDetailScreen
import com.hojaz.maiduka26.presentantion.screens.purchase.PurchaseOrderListScreen
import com.hojaz.maiduka26.presentantion.screens.reports.ReportsScreen
import com.hojaz.maiduka26.presentantion.screens.settings.SettingsScreen
import com.hojaz.maiduka26.presentantion.screens.shop.settings.ShopSettingsScreen
import com.hojaz.maiduka26.presentantion.screens.subscription.SubscriptionScreen

/**
 * Main navigation graph for the application.
 */
@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Login.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Auth screens
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController)
        }

        composable(route = Screen.Register.route) {
            RegisterScreen(navController = navController)
        }

        composable(route = Screen.ForgotPassword.route) {
            // TODO: ForgotPasswordScreen(navController)
        }

        composable(
            route = Screen.OtpVerification.route,
            arguments = listOf(navArgument("phone") { type = NavType.StringType })
        ) { backStackEntry ->
            val phone = backStackEntry.arguments?.getString("phone") ?: ""
            // TODO: OtpVerificationScreen(navController, phone)
        }

        // Main screens
        composable(route = Screen.Dashboard.route) {
            DashboardScreen(navController = navController)
        }

        // Shop screens
        composable(route = Screen.ShopList.route) {
            // TODO: ShopListScreen(navController)
        }

        composable(route = Screen.CreateShop.route) {
            // TODO: CreateShopScreen(navController)
        }

        composable(
            route = Screen.ShopSettings.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            // TODO: ShopSettingsScreen(navController, shopId)
        }

        // Product screens
        composable(route = Screen.ProductList.route) {
            ProductListScreen(navController = navController)
        }

        composable(
            route = Screen.ProductDetail.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            // TODO: ProductDetailScreen(navController, productId)
        }

        composable(route = Screen.CreateProduct.route) {
            // TODO: CreateProductScreen(navController)
        }

        composable(
            route = Screen.EditProduct.route,
            arguments = listOf(navArgument("productId") { type = NavType.StringType })
        ) { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId") ?: ""
            // TODO: EditProductScreen(navController, productId)
        }

        // Sale screens
        composable(route = Screen.POS.route) {
            // TODO: POSScreen(navController)
        }

        composable(route = Screen.SaleList.route) {
            // TODO: SaleListScreen(navController)
        }

        composable(
            route = Screen.SaleDetail.route,
            arguments = listOf(navArgument("saleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getString("saleId") ?: ""
            // TODO: SaleDetailScreen(navController, saleId)
        }

        composable(
            route = Screen.AddPayment.route,
            arguments = listOf(navArgument("saleId") { type = NavType.StringType })
        ) { backStackEntry ->
            val saleId = backStackEntry.arguments?.getString("saleId") ?: ""
            // TODO: AddPaymentScreen(navController, saleId)
        }

        // Customer screens
        composable(route = Screen.CustomerList.route) {
            // TODO: CustomerListScreen(navController)
        }

        composable(
            route = Screen.CustomerDetail.route,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            // TODO: CustomerDetailScreen(navController, customerId)
        }

        composable(route = Screen.CreateCustomer.route) {
            // TODO: CreateCustomerScreen(navController)
        }

        composable(
            route = Screen.EditCustomer.route,
            arguments = listOf(navArgument("customerId") { type = NavType.StringType })
        ) { backStackEntry ->
            val customerId = backStackEntry.arguments?.getString("customerId") ?: ""
            // TODO: EditCustomerScreen(navController, customerId)
        }

        // Expense screens
        composable(route = Screen.ExpenseList.route) {
            // TODO: ExpenseListScreen(navController)
        }

        composable(route = Screen.CreateExpense.route) {
            // TODO: CreateExpenseScreen(navController)
        }

        // Purchase Orders
        composable(route = Screen.PurchaseOrderList.route) {
            PurchaseOrderListScreen(navController = navController)
        }

        composable(route = Screen.CreatePurchaseOrder.route) {
            // TODO: CreatePurchaseOrderScreen(navController)
        }

        composable(
            route = Screen.PurchaseOrderDetail.route,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            PurchaseOrderDetailScreen(orderId = orderId, navController = navController)
        }

        // Savings
        composable(route = Screen.SavingsGoals.route) {
            // TODO: SavingsGoalsScreen(navController)
        }

        composable(route = Screen.CreateSavingsGoal.route) {
            // TODO: CreateSavingsGoalScreen(navController)
        }

        composable(
            route = Screen.SavingsGoalDetail.route,
            arguments = listOf(navArgument("goalId") { type = NavType.StringType })
        ) { backStackEntry ->
            val goalId = backStackEntry.arguments?.getString("goalId") ?: ""
            // TODO: SavingsGoalDetailScreen(navController, goalId)
        }

        // Messaging
        composable(route = Screen.ConversationList.route) {
            // TODO: ConversationListScreen(navController)
        }

        composable(
            route = Screen.ChatScreen.route,
            arguments = listOf(navArgument("conversationId") { type = NavType.StringType })
        ) { backStackEntry ->
            val conversationId = backStackEntry.arguments?.getString("conversationId") ?: ""
            // TODO: ChatScreen(navController, conversationId)
        }

        // Subscription
        composable(route = Screen.Subscription.route) {
            SubscriptionScreen(navController = navController)
        }

        // Shop Members
        composable(
            route = Screen.ShopMembers.route,
            arguments = listOf(navArgument("shopId") { type = NavType.StringType })
        ) { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            // TODO: ShopMembersScreen(navController, shopId)
        }

        // Settings
        composable(route = Screen.Settings.route) {
            SettingsScreen(navController = navController)
        }

        composable(route = Screen.Profile.route) {
            // TODO: ProfileScreen(navController)
        }

        // Reports
        composable(route = Screen.Reports.route) {
            ReportsScreen(navController = navController)
        }

        composable(route = Screen.SalesReport.route) {
            // TODO: SalesReportScreen(navController)
        }

        composable(route = Screen.InventoryReport.route) {
            // TODO: InventoryReportScreen(navController)
        }

        composable(route = Screen.DebtReport.route) {
            // TODO: DebtReportScreen(navController)
        }
    }
}
