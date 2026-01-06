package com.hojaz.maiduka26.di

import android.content.Context
import androidx.room.Room
import com.hojaz.maiduka26.data.local.database.MaidukaDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing Room database and DAOs.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideMaidukaDatabase(
        @ApplicationContext context: Context
    ): MaidukaDatabase {
        return Room.databaseBuilder(
            context,
            MaidukaDatabase::class.java,
            MaidukaDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    // Core DAOs
    @Provides
    fun provideUserDao(database: MaidukaDatabase) = database.userDao()

    @Provides
    fun provideShopDao(database: MaidukaDatabase) = database.shopDao()

    @Provides
    fun provideActiveShopDao(database: MaidukaDatabase) = database.activeShopDao()

    @Provides
    fun provideBlockedShopDao(database: MaidukaDatabase) = database.blockedShopDao()

    // Product & Category DAOs
    @Provides
    fun provideCategoryDao(database: MaidukaDatabase) = database.categoryDao()

    @Provides
    fun provideProductDao(database: MaidukaDatabase) = database.productDao()

    // Customer DAO
    @Provides
    fun provideCustomerDao(database: MaidukaDatabase) = database.customerDao()

    // Sales DAOs
    @Provides
    fun provideSaleDao(database: MaidukaDatabase) = database.saleDao()

    @Provides
    fun provideSaleItemDao(database: MaidukaDatabase) = database.saleItemDao()

    @Provides
    fun provideSalePaymentDao(database: MaidukaDatabase) = database.salePaymentDao()

    @Provides
    fun provideSaleRefundDao(database: MaidukaDatabase) = database.saleRefundDao()

    // Expense & Stock DAOs
    @Provides
    fun provideExpenseDao(database: MaidukaDatabase) = database.expenseDao()

    @Provides
    fun provideStockAdjustmentDao(database: MaidukaDatabase) = database.stockAdjustmentDao()

    @Provides
    fun provideStockTransferDao(database: MaidukaDatabase) = database.stockTransferDao()

    // Purchase Order DAOs
    @Provides
    fun providePurchaseOrderDao(database: MaidukaDatabase) = database.purchaseOrderDao()

    @Provides
    fun providePurchaseOrderItemDao(database: MaidukaDatabase) = database.purchaseOrderItemDao()

    @Provides
    fun providePurchasePaymentDao(database: MaidukaDatabase) = database.purchasePaymentDao()

    // Messaging DAOs
    @Provides
    fun provideConversationDao(database: MaidukaDatabase) = database.conversationDao()

    @Provides
    fun provideMessageDao(database: MaidukaDatabase) = database.messageDao()

    @Provides
    fun provideMessageReactionDao(database: MaidukaDatabase) = database.messageReactionDao()

    @Provides
    fun provideTypingIndicatorDao(database: MaidukaDatabase) = database.typingIndicatorDao()

    // Savings DAOs
    @Provides
    fun provideSavingsGoalDao(database: MaidukaDatabase) = database.savingsGoalDao()

    @Provides
    fun provideSavingsTransactionDao(database: MaidukaDatabase) = database.savingsTransactionDao()

    @Provides
    fun provideShopSavingsSettingsDao(database: MaidukaDatabase) = database.shopSavingsSettingsDao()

    // Shop Configuration DAOs
    @Provides
    fun provideShopMemberDao(database: MaidukaDatabase) = database.shopMemberDao()

    @Provides
    fun provideShopSettingsDao(database: MaidukaDatabase) = database.shopSettingsDao()

    @Provides
    fun provideShopSupplierDao(database: MaidukaDatabase) = database.shopSupplierDao()

    @Provides
    fun provideSubscriptionDao(database: MaidukaDatabase) = database.subscriptionDao()
}
