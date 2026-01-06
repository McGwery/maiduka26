package com.hojaz.maiduka26.data.remote.api

import com.hojaz.maiduka26.data.remote.dto.request.ChangePasswordRequest
import com.hojaz.maiduka26.data.remote.dto.request.CreateCustomerRequest
import com.hojaz.maiduka26.data.remote.dto.request.CreateProductRequest
import com.hojaz.maiduka26.data.remote.dto.request.CreateSaleRequest
import com.hojaz.maiduka26.data.remote.dto.request.CreateShopRequest
import com.hojaz.maiduka26.data.remote.dto.request.CreateSubscriptionRequest
import com.hojaz.maiduka26.data.remote.dto.request.ForgotPasswordRequest
import com.hojaz.maiduka26.data.remote.dto.request.LoginRequest
import com.hojaz.maiduka26.data.remote.dto.request.RefreshTokenRequest
import com.hojaz.maiduka26.data.remote.dto.request.RegisterRequest
import com.hojaz.maiduka26.data.remote.dto.request.SendOtpRequest
import com.hojaz.maiduka26.data.remote.dto.request.SyncDownloadRequest
import com.hojaz.maiduka26.data.remote.dto.request.SyncUploadRequest
import com.hojaz.maiduka26.data.remote.dto.request.UpdateProductRequest
import com.hojaz.maiduka26.data.remote.dto.request.UpdateProfileRequest
import com.hojaz.maiduka26.data.remote.dto.request.UpdateShopRequest
import com.hojaz.maiduka26.data.remote.dto.request.VerifyOtpRequest
import com.hojaz.maiduka26.data.remote.dto.response.ApiResponse
import com.hojaz.maiduka26.data.remote.dto.response.AuthResponse
import com.hojaz.maiduka26.data.remote.dto.response.CustomerResponse
import com.hojaz.maiduka26.data.remote.dto.response.ProductResponse
import com.hojaz.maiduka26.data.remote.dto.response.SaleResponse
import com.hojaz.maiduka26.data.remote.dto.response.ShopResponse
import com.hojaz.maiduka26.data.remote.dto.response.SubscriptionResponse
import com.hojaz.maiduka26.data.remote.dto.response.SyncDownloadResponse
import com.hojaz.maiduka26.data.remote.dto.response.SyncUploadResponse
import com.hojaz.maiduka26.data.remote.dto.response.TokenResponse
import com.hojaz.maiduka26.data.remote.dto.response.UserResponse
import retrofit2.Response
import retrofit2.http.*

/**
 * Main API service interface for network operations.
 */
interface ApiService {

    // ==================== Authentication ====================

    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<ApiResponse<AuthResponse>>

    @POST("auth/logout")
    suspend fun logout(): Response<ApiResponse<Unit>>

    @POST("auth/refresh")
    suspend fun refreshToken(@Body request: RefreshTokenRequest): Response<ApiResponse<TokenResponse>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ApiResponse<Unit>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<ApiResponse<Unit>>

    @POST("auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): Response<ApiResponse<Unit>>

    // ==================== User ====================

    @GET("user/profile")
    suspend fun getProfile(): Response<ApiResponse<UserResponse>>

    @PUT("user/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<ApiResponse<UserResponse>>

    @POST("user/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest): Response<ApiResponse<Unit>>

    // ==================== Shops ====================

    @GET("shops")
    suspend fun getShops(): Response<ApiResponse<List<ShopResponse>>>

    @GET("shops/{shopId}")
    suspend fun getShopById(@Path("shopId") shopId: String): Response<ApiResponse<ShopResponse>>

    @POST("shops")
    suspend fun createShop(@Body request: CreateShopRequest): Response<ApiResponse<ShopResponse>>

    @PUT("shops/{shopId}")
    suspend fun updateShop(
        @Path("shopId") shopId: String,
        @Body request: UpdateShopRequest
    ): Response<ApiResponse<ShopResponse>>

    @DELETE("shops/{shopId}")
    suspend fun deleteShop(@Path("shopId") shopId: String): Response<ApiResponse<Unit>>

    // ==================== Products ====================

    @GET("shops/{shopId}/products")
    suspend fun getProducts(@Path("shopId") shopId: String): Response<ApiResponse<List<ProductResponse>>>

    @GET("shops/{shopId}/products/{productId}")
    suspend fun getProductById(
        @Path("shopId") shopId: String,
        @Path("productId") productId: String
    ): Response<ApiResponse<ProductResponse>>

    @POST("shops/{shopId}/products")
    suspend fun createProduct(
        @Path("shopId") shopId: String,
        @Body request: CreateProductRequest
    ): Response<ApiResponse<ProductResponse>>

    @PUT("shops/{shopId}/products/{productId}")
    suspend fun updateProduct(
        @Path("shopId") shopId: String,
        @Path("productId") productId: String,
        @Body request: UpdateProductRequest
    ): Response<ApiResponse<ProductResponse>>

    @DELETE("shops/{shopId}/products/{productId}")
    suspend fun deleteProduct(
        @Path("shopId") shopId: String,
        @Path("productId") productId: String
    ): Response<ApiResponse<Unit>>

    // ==================== Sales ====================

    @GET("shops/{shopId}/sales")
    suspend fun getSales(
        @Path("shopId") shopId: String,
        @Query("from") from: String? = null,
        @Query("to") to: String? = null,
        @Query("status") status: String? = null
    ): Response<ApiResponse<List<SaleResponse>>>

    @GET("shops/{shopId}/sales/{saleId}")
    suspend fun getSaleById(
        @Path("shopId") shopId: String,
        @Path("saleId") saleId: String
    ): Response<ApiResponse<SaleResponse>>

    @POST("shops/{shopId}/sales")
    suspend fun createSale(
        @Path("shopId") shopId: String,
        @Body request: CreateSaleRequest
    ): Response<ApiResponse<SaleResponse>>

    // ==================== Customers ====================

    @GET("shops/{shopId}/customers")
    suspend fun getCustomers(@Path("shopId") shopId: String): Response<ApiResponse<List<CustomerResponse>>>

    @POST("shops/{shopId}/customers")
    suspend fun createCustomer(
        @Path("shopId") shopId: String,
        @Body request: CreateCustomerRequest
    ): Response<ApiResponse<CustomerResponse>>

    // ==================== Subscriptions ====================

    @GET("shops/{shopId}/subscription")
    suspend fun getSubscription(@Path("shopId") shopId: String): Response<ApiResponse<SubscriptionResponse>>

    @POST("shops/{shopId}/subscription")
    suspend fun createSubscription(
        @Path("shopId") shopId: String,
        @Body request: CreateSubscriptionRequest
    ): Response<ApiResponse<SubscriptionResponse>>

    // ==================== Sync ====================

    @POST("sync/upload")
    suspend fun uploadSyncData(@Body request: SyncUploadRequest): Response<ApiResponse<SyncUploadResponse>>

    @POST("sync/download")
    suspend fun downloadSyncData(@Body request: SyncDownloadRequest): Response<ApiResponse<SyncDownloadResponse>>
}

