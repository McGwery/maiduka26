package com.hojaz.maiduka26.presentantion.navigation

/**
 * Sealed class representing all navigation routes in the app.
 */
sealed class Screen(val route: String) {
    // Auth screens
    data object Splash : Screen("splash")
    data object Login : Screen("login")
    data object Register : Screen("register")
    data object ForgotPassword : Screen("forgot_password")
    data object OtpVerification : Screen("otp_verification/{phone}") {
        fun createRoute(phone: String) = "otp_verification/$phone"
    }

    // Main screens
    data object Dashboard : Screen("dashboard")
    data object ShopList : Screen("shops")
    data object CreateShop : Screen("shops/create")
    data object ShopSettings : Screen("shops/{shopId}/settings") {
        fun createRoute(shopId: String) = "shops/$shopId/settings"
    }

    // Product screens
    data object ProductList : Screen("products")
    data object ProductDetail : Screen("products/{productId}") {
        fun createRoute(productId: String) = "products/$productId"
    }
    data object CreateProduct : Screen("products/create")
    data object EditProduct : Screen("products/{productId}/edit") {
        fun createRoute(productId: String) = "products/$productId/edit"
    }

    // Sale screens
    data object POS : Screen("pos")
    data object SaleList : Screen("sales")
    data object SaleDetail : Screen("sales/{saleId}") {
        fun createRoute(saleId: String) = "sales/$saleId"
    }
    data object AddPayment : Screen("sales/{saleId}/payment") {
        fun createRoute(saleId: String) = "sales/$saleId/payment"
    }

    // Customer screens
    data object CustomerList : Screen("customers")
    data object CustomerDetail : Screen("customers/{customerId}") {
        fun createRoute(customerId: String) = "customers/$customerId"
    }
    data object CreateCustomer : Screen("customers/create")
    data object EditCustomer : Screen("customers/{customerId}/edit") {
        fun createRoute(customerId: String) = "customers/$customerId/edit"
    }

    // Expense screens
    data object ExpenseList : Screen("expenses")
    data object CreateExpense : Screen("expenses/create")

    // Settings
    data object Settings : Screen("settings")
    data object Profile : Screen("profile")
    data object Subscription : Screen("subscription")

    /** Screen for subscription payment/renewal when subscription is expired */
    data object SubscriptionPayment : Screen("subscription/payment")

    data object ShopMembers : Screen("shops/{shopId}/members") {
        fun createRoute(shopId: String) = "shops/$shopId/members"
    }

    // Purchase Orders
    data object PurchaseOrderList : Screen("purchase_orders")
    data object CreatePurchaseOrder : Screen("purchase_orders/create")
    data object PurchaseOrderDetail : Screen("purchase_orders/{orderId}") {
        fun createRoute(orderId: String) = "purchase_orders/$orderId"
    }

    // Savings
    data object SavingsGoals : Screen("savings")
    data object CreateSavingsGoal : Screen("savings/create")
    data object SavingsGoalDetail : Screen("savings/{goalId}") {
        fun createRoute(goalId: String) = "savings/$goalId"
    }

    // Messaging
    data object ConversationList : Screen("messages")
    data object ChatScreen : Screen("messages/{conversationId}") {
        fun createRoute(conversationId: String) = "messages/$conversationId"
    }

    // Reports
    data object Reports : Screen("reports")
    data object SalesReport : Screen("reports/sales")
    data object InventoryReport : Screen("reports/inventory")
    data object DebtReport : Screen("reports/debt")
    data object ProfitReport : Screen("reports/profit")
    data object ExpenseReport : Screen("reports/expenses")
}

