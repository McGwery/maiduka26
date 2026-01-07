package com.hojaz.maiduka26.data.remote.dto.response

import com.google.gson.annotations.SerializedName

/**
 * Generic API response wrapper.
 */
data class ApiResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T? = null
)

/**
 * Base response wrappers for API responses.
 */
data class BaseResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null
)

data class SingleResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: T?
)

data class ListResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<T>
)

data class PaginatedResponse<T>(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("data") val data: List<T>,
    @SerializedName("meta") val meta: PaginationMeta
)

data class PaginationMeta(
    @SerializedName("current_page") val currentPage: Int,
    @SerializedName("last_page") val lastPage: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total") val total: Int
)

/**
 * Authentication response DTOs.
 */
data class LoginResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String? = null,
    @SerializedName("responseTime") val responseTime: Double? = null,
    @SerializedName("data") val data: AuthData?
)

data class AuthData(
    @SerializedName("token") val token: TokenData,
    @SerializedName("user") val user: UserResponse
)

data class TokenData(
    @SerializedName("accessToken") val accessToken: String,
    @SerializedName("tokenType") val tokenType: String = "Bearer"
)

/**
 * Type alias for auth response.
 */
typealias AuthResponse = AuthData

/**
 * Token refresh response.
 */
data class TokenResponse(
    @SerializedName("access_token") val accessToken: String,
    @SerializedName("refresh_token") val refreshToken: String?,
    @SerializedName("token_type") val tokenType: String = "Bearer",
    @SerializedName("expires_in") val expiresIn: Long? = null
)

data class UserResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("phone") val phone: String?,
    @SerializedName("emailVerifiedAt") val emailVerifiedAt: String?,
    @SerializedName("phoneVerifiedAt") val phoneVerifiedAt: String?,
    @SerializedName("isPhoneVerified") val isPhoneVerified: Boolean = false,
    @SerializedName("twoFactorEnabled") val twoFactorEnabled: Boolean = false,
    @SerializedName("isPhoneLoginEnabled") val isPhoneLoginEnabled: Boolean = false,
    @SerializedName("activeShop") val activeShop: ActiveShopResponse? = null,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

/**
 * Active shop from login response.
 */
data class ActiveShopResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("businessType") val businessType: BusinessTypeResponse,
    @SerializedName("phoneNumber") val phoneNumber: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("agentCode") val agentCode: String?,
    @SerializedName("currency") val currency: CurrencyResponse,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("isCurrentSelected") val isCurrentSelected: Boolean = false,
    @SerializedName("activeSubscription") val activeSubscription: ActiveSubscriptionResponse?,
    @SerializedName("createdAt") val createdAt: String?,
    @SerializedName("updatedAt") val updatedAt: String?
)

data class BusinessTypeResponse(
    @SerializedName("value") val value: String,
    @SerializedName("label") val label: String
)

data class CurrencyResponse(
    @SerializedName("code") val code: String,
    @SerializedName("symbol") val symbol: String,
    @SerializedName("label") val label: String
)

data class ActiveSubscriptionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("plan") val plan: String,
    @SerializedName("planLabel") val planLabel: String,
    @SerializedName("type") val type: String,
    @SerializedName("expiresAt") val expiresAt: String?,
    @SerializedName("daysRemaining") val daysRemaining: Int,
    @SerializedName("isActive") val isActive: Boolean = true,
    @SerializedName("isExpired") val isExpired: Boolean = false,
    @SerializedName("isExpiringSoon") val isExpiringSoon: Boolean = false
)

/**
 * Shop response DTO.
 */
data class ShopResponse(
    @SerializedName("id") val id: String,
    @SerializedName("owner_id") val ownerId: String,
    @SerializedName("name") val name: String,
    @SerializedName("business_type") val businessType: String,
    @SerializedName("phone_number") val phoneNumber: String?,
    @SerializedName("address") val address: String,
    @SerializedName("agent_code") val agentCode: String?,
    @SerializedName("currency") val currency: String,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("is_active") val isActive: Boolean = true,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Product response DTO.
 */
data class ProductResponse(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("category_id") val categoryId: String,
    @SerializedName("product_type") val productType: String,
    @SerializedName("product_name") val productName: String,
    @SerializedName("description") val description: String?,
    @SerializedName("sku") val sku: String?,
    @SerializedName("barcode") val barcode: String?,
    @SerializedName("cost_per_unit") val costPerUnit: String?,
    @SerializedName("unit_type") val unitType: String,
    @SerializedName("break_down_count_per_unit") val breakDownCountPerUnit: Int?,
    @SerializedName("small_item_name") val smallItemName: String?,
    @SerializedName("sell_whole_units") val sellWholeUnits: Boolean = true,
    @SerializedName("price_per_unit") val pricePerUnit: String?,
    @SerializedName("sell_individual_items") val sellIndividualItems: Boolean = false,
    @SerializedName("price_per_item") val pricePerItem: String?,
    @SerializedName("current_stock") val currentStock: Int?,
    @SerializedName("low_stock_threshold") val lowStockThreshold: Int?,
    @SerializedName("track_inventory") val trackInventory: Boolean = true,
    @SerializedName("image_url") val imageUrl: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Customer response DTO.
 */
data class CustomerResponse(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String?,
    @SerializedName("email") val email: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("credit_limit") val creditLimit: String = "0.00",
    @SerializedName("current_debt") val currentDebt: String = "0.00",
    @SerializedName("total_purchases") val totalPurchases: String = "0.00",
    @SerializedName("total_paid") val totalPaid: String = "0.00",
    @SerializedName("notes") val notes: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Sale response DTO.
 */
data class SaleResponse(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("customer_id") val customerId: String?,
    @SerializedName("user_id") val userId: String,
    @SerializedName("sale_number") val saleNumber: String,
    @SerializedName("subtotal") val subtotal: String,
    @SerializedName("tax_rate") val taxRate: String = "0.00",
    @SerializedName("tax_amount") val taxAmount: String = "0.00",
    @SerializedName("discount_amount") val discountAmount: String = "0.00",
    @SerializedName("discount_percentage") val discountPercentage: String = "0.00",
    @SerializedName("total_amount") val totalAmount: String,
    @SerializedName("amount_paid") val amountPaid: String = "0.00",
    @SerializedName("change_amount") val changeAmount: String = "0.00",
    @SerializedName("debt_amount") val debtAmount: String = "0.00",
    @SerializedName("profit_amount") val profitAmount: String = "0.00",
    @SerializedName("status") val status: String,
    @SerializedName("payment_status") val paymentStatus: String,
    @SerializedName("notes") val notes: String?,
    @SerializedName("sale_date") val saleDate: String,
    @SerializedName("items") val items: List<SaleItemResponse>?,
    @SerializedName("payments") val payments: List<SalePaymentResponse>?,
    @SerializedName("customer") val customer: CustomerResponse?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

data class SaleItemResponse(
    @SerializedName("id") val id: String,
    @SerializedName("sale_id") val saleId: String,
    @SerializedName("product_id") val productId: String?,
    @SerializedName("product_name") val productName: String,
    @SerializedName("product_sku") val productSku: String?,
    @SerializedName("quantity") val quantity: String,
    @SerializedName("unit_type") val unitType: String?,
    @SerializedName("original_price") val originalPrice: String,
    @SerializedName("selling_price") val sellingPrice: String,
    @SerializedName("cost_price") val costPrice: String,
    @SerializedName("discount_amount") val discountAmount: String = "0.00",
    @SerializedName("subtotal") val subtotal: String,
    @SerializedName("total") val total: String,
    @SerializedName("profit") val profit: String = "0.00"
)

data class SalePaymentResponse(
    @SerializedName("id") val id: String,
    @SerializedName("sale_id") val saleId: String,
    @SerializedName("user_id") val userId: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("reference_number") val referenceNumber: String?,
    @SerializedName("notes") val notes: String?,
    @SerializedName("payment_date") val paymentDate: String
)

/**
 * Category response DTO.
 */
data class CategoryResponse(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("description") val description: String?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Expense response DTO.
 */
data class ExpenseResponse(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("title") val title: String,
    @SerializedName("description") val description: String?,
    @SerializedName("category") val category: String,
    @SerializedName("amount") val amount: String,
    @SerializedName("expense_date") val expenseDate: String,
    @SerializedName("payment_method") val paymentMethod: String,
    @SerializedName("receipt_number") val receiptNumber: String?,
    @SerializedName("recorded_by") val recordedBy: String,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Sync response DTOs.
 */
data class SyncResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String?,
    @SerializedName("synced_at") val syncedAt: Long,
    @SerializedName("conflicts") val conflicts: List<SyncConflict>?
)

data class SyncConflict(
    @SerializedName("entity_type") val entityType: String,
    @SerializedName("entity_id") val entityId: String,
    @SerializedName("conflict_type") val conflictType: String,
    @SerializedName("server_data") val serverData: Any?
)

data class SyncDownloadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("last_sync") val lastSync: Long,
    @SerializedName("shops") val shops: List<ShopResponse>?,
    @SerializedName("products") val products: List<ProductResponse>?,
    @SerializedName("customers") val customers: List<CustomerResponse>?,
    @SerializedName("categories") val categories: List<CategoryResponse>?
)

/**
 * Subscription response DTO.
 */
data class SubscriptionResponse(
    @SerializedName("id") val id: String,
    @SerializedName("shop_id") val shopId: String,
    @SerializedName("plan") val plan: String,
    @SerializedName("type") val type: String,
    @SerializedName("status") val status: String,
    @SerializedName("price") val price: String,
    @SerializedName("currency") val currency: String,
    @SerializedName("starts_at") val startsAt: String?,
    @SerializedName("expires_at") val expiresAt: String?,
    @SerializedName("auto_renew") val autoRenew: Boolean = false,
    @SerializedName("payment_method") val paymentMethod: String?,
    @SerializedName("transaction_reference") val transactionReference: String?,
    @SerializedName("features") val features: List<String>?,
    @SerializedName("max_users") val maxUsers: Int?,
    @SerializedName("max_products") val maxProducts: Int?,
    @SerializedName("created_at") val createdAt: String?,
    @SerializedName("updated_at") val updatedAt: String?
)

/**
 * Sync upload response DTO.
 */
data class SyncUploadResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("synced_at") val syncedAt: Long,
    @SerializedName("uploaded_count") val uploadedCount: Int,
    @SerializedName("conflicts") val conflicts: List<SyncConflict>?
)

