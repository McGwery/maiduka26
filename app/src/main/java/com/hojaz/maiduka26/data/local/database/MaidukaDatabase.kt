package com.hojaz.maiduka26.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hojaz.maiduka26.data.local.dao.*
import com.hojaz.maiduka26.data.local.database.converters.Converters
import com.hojaz.maiduka26.data.local.entity.*

/**
 * Main Room database for the MaiDuka POS application.
 * Handles all local data persistence for offline-first functionality.
 *
 * Tables (28 total):
 * - users, shops, active_shops, blocked_shops
 * - categories, products, customers
 * - sales, sale_items, sale_payments, sale_refunds
 * - expenses, stock_adjustments, stock_transfers
 * - purchase_orders, purchase_order_items, purchase_payments
 * - conversations, messages, message_reactions, typing_indicators
 * - savings_goals, savings_transactions, shop_savings_settings
 * - shop_members, shop_settings, shop_suppliers, subscriptions
 */
@Database(
    entities = [
        // Core entities
        UserEntity::class,
        ShopEntity::class,
        ActiveShopEntity::class,
        BlockedShopEntity::class,

        // Product & Category
        CategoryEntity::class,
        ProductEntity::class,

        // Customer
        CustomerEntity::class,

        // Sales
        SaleEntity::class,
        SaleItemEntity::class,
        SalePaymentEntity::class,
        SaleRefundEntity::class,

        // Expenses & Stock
        ExpenseEntity::class,
        StockAdjustmentEntity::class,
        StockTransferEntity::class,

        // Purchase Orders
        PurchaseOrderEntity::class,
        PurchaseOrderItemEntity::class,
        PurchasePaymentEntity::class,

        // Messaging
        ConversationEntity::class,
        MessageEntity::class,
        MessageReactionEntity::class,
        TypingIndicatorEntity::class,

        // Savings
        SavingsGoalEntity::class,
        SavingsTransactionEntity::class,
        ShopSavingsSettingsEntity::class,

        // Shop Configuration
        ShopMemberEntity::class,
        ShopSettingsEntity::class,
        ShopSupplierEntity::class,
        SubscriptionEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class MaidukaDatabase : RoomDatabase() {

    // Core DAOs
    abstract fun userDao(): UserDao
    abstract fun shopDao(): ShopDao
    abstract fun activeShopDao(): ActiveShopDao
    abstract fun blockedShopDao(): BlockedShopDao

    // Product & Category DAOs
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao

    // Customer DAO
    abstract fun customerDao(): CustomerDao

    // Sales DAOs
    abstract fun saleDao(): SaleDao
    abstract fun saleItemDao(): SaleItemDao
    abstract fun salePaymentDao(): SalePaymentDao
    abstract fun saleRefundDao(): SaleRefundDao

    // Expense & Stock DAOs
    abstract fun expenseDao(): ExpenseDao
    abstract fun stockAdjustmentDao(): StockAdjustmentDao
    abstract fun stockTransferDao(): StockTransferDao

    // Purchase Order DAOs
    abstract fun purchaseOrderDao(): PurchaseOrderDao
    abstract fun purchaseOrderItemDao(): PurchaseOrderItemDao
    abstract fun purchasePaymentDao(): PurchasePaymentDao

    // Messaging DAOs
    abstract fun conversationDao(): ConversationDao
    abstract fun messageDao(): MessageDao
    abstract fun messageReactionDao(): MessageReactionDao
    abstract fun typingIndicatorDao(): TypingIndicatorDao

    // Savings DAOs
    abstract fun savingsGoalDao(): SavingsGoalDao
    abstract fun savingsTransactionDao(): SavingsTransactionDao
    abstract fun shopSavingsSettingsDao(): ShopSavingsSettingsDao

    // Shop Configuration DAOs
    abstract fun shopMemberDao(): ShopMemberDao
    abstract fun shopSettingsDao(): ShopSettingsDao
    abstract fun shopSupplierDao(): ShopSupplierDao
    abstract fun subscriptionDao(): SubscriptionDao

    companion object {
        const val DATABASE_NAME = "maiduka_database"
    }
}
