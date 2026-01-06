package com.hojaz.maiduka26.util

object Constants {
    // API Configuration
    const val BASE_URL = "https://maiduka.hojaz.com/api/"
    const val CONNECT_TIMEOUT = 30L
    const val READ_TIMEOUT = 30L
    const val WRITE_TIMEOUT = 30L

    // Database
    const val DATABASE_NAME = "maiduka_database"
    const val DATABASE_VERSION = 1

    // DataStore
    const val PREFERENCES_NAME = "maiduka_preferences"
    const val USER_PREFERENCES_KEY = "user_preferences"

    // Sync Configuration
    const val SYNC_INTERVAL_MINUTES = 15L
    const val SYNC_RETRY_COUNT = 3
    const val SYNC_BACKOFF_DELAY_SECONDS = 30L

    // Pagination
    const val DEFAULT_PAGE_SIZE = 20
    const val MAX_PAGE_SIZE = 100

    // Date Formats
    const val API_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    const val DISPLAY_DATE_FORMAT = "dd/MM/yyyy"
    const val DISPLAY_TIME_FORMAT = "HH:mm"
    const val DISPLAY_DATE_TIME_FORMAT = "dd/MM/yyyy HH:mm"

    // Currency
    const val DEFAULT_CURRENCY = "TZS"
    const val DEFAULT_CURRENCY_SYMBOL = "TSh"

    // Payment Methods
    object PaymentMethod {
        const val CASH = "cash"
        const val MOBILE_MONEY = "mobile_money"
        const val BANK_TRANSFER = "bank_transfer"
        const val CREDIT = "credit"
        const val CHEQUE = "cheque"
    }

    // Sale Status
    object SaleStatus {
        const val COMPLETED = "completed"
        const val PENDING = "pending"
        const val CANCELLED = "cancelled"
        const val REFUNDED = "refunded"
        const val PARTIALLY_REFUNDED = "partially_refunded"
    }

    // Payment Status
    object PaymentStatus {
        const val PAID = "paid"
        const val PARTIALLY_PAID = "partially_paid"
        const val PENDING = "pending"
        const val DEBT = "debt"
    }

    // Product Types
    object ProductType {
        const val PHYSICAL = "physical"
        const val SERVICE = "service"
        const val DIGITAL = "digital"
    }

    // Unit Types
    object UnitType {
        const val BOX = "box"
        const val CARTON = "carton"
        const val PIECE = "piece"
        const val PACK = "pack"
        const val BOTTLE = "bottle"
        const val BAG = "bag"
        const val SACHET = "sachet"
        const val KG = "kg"
        const val GRAM = "gram"
        const val LITER = "liter"
        const val ML = "ml"
    }

    // Stock Adjustment Types
    object StockAdjustmentType {
        const val DAMAGED = "damaged"
        const val EXPIRED = "expired"
        const val LOST = "lost"
        const val THEFT = "theft"
        const val PERSONAL_USE = "personal_use"
        const val DONATION = "donation"
        const val RETURN_TO_SUPPLIER = "return_to_supplier"
        const val OTHER = "other"
        const val RESTOCK = "restock"
        const val ADJUSTMENT = "adjustment"
    }

    // Message Types
    object MessageType {
        const val TEXT = "text"
        const val IMAGE = "image"
        const val VIDEO = "video"
        const val AUDIO = "audio"
        const val DOCUMENT = "document"
        const val PRODUCT = "product"
        const val LOCATION = "location"
    }

    // Savings Types
    object SavingsType {
        const val PERCENTAGE = "percentage"
        const val FIXED_AMOUNT = "fixed_amount"
    }

    // Savings Goal Status
    object SavingsGoalStatus {
        const val ACTIVE = "active"
        const val COMPLETED = "completed"
        const val CANCELLED = "cancelled"
        const val PAUSED = "paused"
    }

    // Subscription Plans
    object SubscriptionPlan {
        const val FREE = "free"
        const val BASIC = "basic"
        const val PREMIUM = "premium"
        const val ENTERPRISE = "enterprise"
    }

    // Sync Status
    object SyncStatus {
        const val PENDING = "pending"
        const val SYNCED = "synced"
        const val FAILED = "failed"
        const val CONFLICT = "conflict"
    }

    // Shop Roles
    object ShopRole {
        const val OWNER = "owner"
        const val MANAGER = "manager"
        const val CASHIER = "cashier"
        const val STAFF = "staff"
    }
}

