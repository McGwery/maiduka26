package com.hojaz.maiduka26.data.remote.dto.request

import com.google.gson.annotations.SerializedName

// ==================== Authentication Requests ====================

data class LoginRequest(
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("password")
    val password: String
)

data class RegisterRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("password")
    val password: String,
    @SerializedName("password_confirmation")
    val passwordConfirmation: String
)

data class RefreshTokenRequest(
    @SerializedName("refresh_token")
    val refreshToken: String
)

data class ForgotPasswordRequest(
    @SerializedName("email")
    val email: String
)

data class VerifyOtpRequest(
    @SerializedName("target")
    val target: String,
    @SerializedName("otp")
    val otp: String
)

data class SendOtpRequest(
    @SerializedName("target")
    val target: String
)

data class ChangePasswordRequest(
    @SerializedName("current_password")
    val currentPassword: String,
    @SerializedName("new_password")
    val newPassword: String,
    @SerializedName("new_password_confirmation")
    val newPasswordConfirmation: String
)

data class UpdateProfileRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("phone")
    val phone: String? = null
)

// ==================== Shop Requests ====================

data class CreateShopRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("business_type")
    val businessType: String,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("currency")
    val currency: String = "TZS"
)

data class UpdateShopRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("business_type")
    val businessType: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("currency")
    val currency: String? = null
)

// ==================== Product Requests ====================

data class CreateProductRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("sku")
    val sku: String? = null,
    @SerializedName("barcode")
    val barcode: String? = null,
    @SerializedName("category_id")
    val categoryId: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("cost_price")
    val costPrice: String,
    @SerializedName("selling_price")
    val sellingPrice: String,
    @SerializedName("quantity")
    val quantity: Int = 0,
    @SerializedName("low_stock_threshold")
    val lowStockThreshold: Int = 10,
    @SerializedName("unit")
    val unit: String = "piece",
    @SerializedName("image_url")
    val imageUrl: String? = null
)

data class UpdateProductRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("sku")
    val sku: String? = null,
    @SerializedName("barcode")
    val barcode: String? = null,
    @SerializedName("category_id")
    val categoryId: String? = null,
    @SerializedName("description")
    val description: String? = null,
    @SerializedName("cost_price")
    val costPrice: String? = null,
    @SerializedName("selling_price")
    val sellingPrice: String? = null,
    @SerializedName("quantity")
    val quantity: Int? = null,
    @SerializedName("low_stock_threshold")
    val lowStockThreshold: Int? = null,
    @SerializedName("unit")
    val unit: String? = null,
    @SerializedName("image_url")
    val imageUrl: String? = null
)

// ==================== Sale Requests ====================

data class CreateSaleRequest(
    @SerializedName("customer_id")
    val customerId: String? = null,
    @SerializedName("items")
    val items: List<SaleItemRequest>,
    @SerializedName("discount_amount")
    val discountAmount: String = "0.00",
    @SerializedName("tax_amount")
    val taxAmount: String = "0.00",
    @SerializedName("payment_method")
    val paymentMethod: String = "cash",
    @SerializedName("amount_paid")
    val amountPaid: String,
    @SerializedName("notes")
    val notes: String? = null
)

data class SaleItemRequest(
    @SerializedName("product_id")
    val productId: String,
    @SerializedName("quantity")
    val quantity: Int,
    @SerializedName("unit_price")
    val unitPrice: String,
    @SerializedName("discount")
    val discount: String = "0.00"
)

// ==================== Customer Requests ====================

data class CreateCustomerRequest(
    @SerializedName("name")
    val name: String,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("email")
    val email: String? = null,
    @SerializedName("address")
    val address: String? = null,
    @SerializedName("credit_limit")
    val creditLimit: String = "0.00"
)

// ==================== Subscription Requests ====================

data class CreateSubscriptionRequest(
    @SerializedName("plan")
    val plan: String,
    @SerializedName("payment_method")
    val paymentMethod: String,
    @SerializedName("auto_renew")
    val autoRenew: Boolean = true
)

// ==================== Sync Requests ====================

data class SyncUploadRequest(
    @SerializedName("shop_id")
    val shopId: String,
    @SerializedName("last_synced_at")
    val lastSyncedAt: Long? = null,
    @SerializedName("products")
    val products: List<Map<String, Any?>>? = null,
    @SerializedName("sales")
    val sales: List<Map<String, Any?>>? = null,
    @SerializedName("customers")
    val customers: List<Map<String, Any?>>? = null,
    @SerializedName("expenses")
    val expenses: List<Map<String, Any?>>? = null
)

data class SyncDownloadRequest(
    @SerializedName("shop_id")
    val shopId: String,
    @SerializedName("last_synced_at")
    val lastSyncedAt: Long? = null
)

