@file:OptIn(ExperimentalCoroutinesApi::class)

package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.UserDao
import com.hojaz.maiduka26.data.local.entity.ShopEntity
import com.hojaz.maiduka26.data.local.entity.SubscriptionEntity
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.mapper.UserMapper.toDomain
import com.hojaz.maiduka26.data.remote.api.ApiService
import com.hojaz.maiduka26.data.remote.dto.request.LoginRequest
import com.hojaz.maiduka26.data.remote.dto.request.RefreshTokenRequest
import com.hojaz.maiduka26.data.remote.dto.request.RegisterRequest
import com.hojaz.maiduka26.data.remote.dto.request.VerifyOtpRequest
import com.hojaz.maiduka26.data.remote.dto.response.ActiveShopResponse
import com.hojaz.maiduka26.data.remote.dto.response.ActiveSubscriptionResponse
import com.hojaz.maiduka26.data.remote.dto.response.AuthData
import com.hojaz.maiduka26.data.remote.dto.response.UserResponse
import com.hojaz.maiduka26.domain.model.User
import com.hojaz.maiduka26.domain.repository.AuthRepository
import com.hojaz.maiduka26.util.DateTimeUtil
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * Handles authentication operations with offline-first approach.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
    private val shopDao: com.hojaz.maiduka26.data.local.dao.ShopDao,
    private val subscriptionDao: com.hojaz.maiduka26.data.local.dao.SubscriptionDao,
    private val apiService: ApiService,
    private val preferencesManager: PreferencesManager,
    private val networkMonitor: NetworkMonitor
) : AuthRepository {

    override suspend fun login(email: String, password: String): Either<Throwable, User> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection. Please check your network.").left()
            }

            val response = apiService.login(LoginRequest(email = email, password = password))

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                    ?: return Exception("Invalid response from server").left()

                // Save user to local database
                val userEntity = authData.user.toUserEntity()
                userDao.insert(userEntity)

                // Save session with token data
                preferencesManager.saveUserSession(
                    userId = authData.user.id,
                    userName = authData.user.name,
                    email = authData.user.email,
                    phone = authData.user.phone,
                    accessToken = authData.token.accessToken,
                    refreshToken = "", // No refresh token in this response
                    tokenExpiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000) // Default 24 hours
                )

                // Save active shop if present
                authData.user.activeShop?.let { activeShop ->
                    preferencesManager.setActiveShop(
                        shopId = activeShop.id,
                        shopName = activeShop.name
                    )
                    // Save shop to local database
                    saveActiveShopToDatabase(activeShop)
                }

                Timber.d("User logged in: ${authData.user.id}")
                authData.user.toDomain().right()
            } else {
                val errorMessage = response.body()?.message ?: "Login failed"
                Exception(errorMessage).left()
            }
        } catch (e: Exception) {
            Timber.e(e, "Login error")
            e.left()
        }
    }

    override suspend fun loginWithPhone(phone: String, password: String): Either<Throwable, User> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection. Please check your network.").left()
            }

            val response = apiService.login(LoginRequest(phone = phone, password = password))

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                    ?: return Exception("Invalid response from server").left()

                val userEntity = authData.user.toUserEntity()
                userDao.insert(userEntity)

                preferencesManager.saveUserSession(
                    userId = authData.user.id,
                    userName = authData.user.name,
                    email = authData.user.email,
                    phone = authData.user.phone,
                    accessToken = authData.token.accessToken,
                    refreshToken = "",
                    tokenExpiry = System.currentTimeMillis() + (24 * 60 * 60 * 1000)
                )

                // Save active shop if present
                authData.user.activeShop?.let { activeShop ->
                    preferencesManager.setActiveShop(
                        shopId = activeShop.id,
                        shopName = activeShop.name
                    )
                    saveActiveShopToDatabase(activeShop)
                }

                Timber.d("User logged in with phone: ${authData.user.id}")
                authData.user.toDomain().right()
            } else {
                val errorMessage = response.body()?.message ?: "Login failed"
                Exception(errorMessage).left()
            }
        } catch (e: Exception) {
            Timber.e(e, "Phone login error")
            e.left()
        }
    }

    override suspend fun register(
        name: String,
        email: String?,
        phone: String?,
        password: String
    ): Either<Throwable, User> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection. Please check your network.").left()
            }

            val response = apiService.register(
                RegisterRequest(
                    name = name,
                    email = email,
                    phone = phone,
                    password = password,
                    passwordConfirmation = password
                )
            )

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                    ?: return Exception("Invalid response from server").left()

                val userEntity = authData.user.toUserEntity()
                userDao.insert(userEntity)

                // Save partial session - user still needs to complete OTP verification and shop setup
                // Do NOT use saveUserSession as it sets isLoggedIn = true
                preferencesManager.savePartialSession(
                    userId = authData.user.id,
                    userName = authData.user.name,
                    email = authData.user.email,
                    phone = authData.user.phone,
                    accessToken = authData.token.accessToken
                )

                // Note: Active shop is unlikely for new registration, but handle if present
                // Don't save shop here - let the registration flow complete shop setup

                Timber.d("User registered: ${authData.user.id}")
                authData.user.toDomain().right()
            } else {
                val errorMessage = response.body()?.message ?: "Registration failed"
                Exception(errorMessage).left()
            }
        } catch (e: Exception) {
            Timber.e(e, "Registration error")
            e.left()
        }
    }

    override suspend fun logout(): Either<Throwable, Unit> {
        return try {
            // Try to logout from server if online
            if (networkMonitor.isOnline) {
                try {
                    apiService.logout()
                } catch (e: Exception) {
                    Timber.w(e, "Server logout failed, proceeding with local logout")
                }
            }

            // Clear local session
            preferencesManager.clearSession()

            Timber.d("User logged out")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Logout error")
            e.left()
        }
    }

    override fun getCurrentUser(): Flow<User?> {
        return preferencesManager.userPreferencesFlow.flatMapLatest { prefs ->
            val userId = prefs.userId
            if (userId != null) {
                userDao.getUserByIdFlow(userId).map { it?.toDomain() }
            } else {
                flowOf(null)
            }
        }
    }

    override fun isLoggedIn(): Flow<Boolean> {
        return preferencesManager.isLoggedIn
    }

    override suspend fun refreshToken(): Either<Throwable, Unit> {
        return try {
            val prefs = preferencesManager.userPreferencesFlow.first()
            val refreshToken = prefs.refreshToken
                ?: return Exception("No refresh token available").left()

            if (!networkMonitor.isOnline) {
                return Exception("No internet connection").left()
            }

            val response = apiService.refreshToken(RefreshTokenRequest(refreshToken))

            if (response.isSuccessful && response.body()?.success == true) {
                val authData = response.body()?.data
                    ?: return Exception("Invalid response from server").left()

                preferencesManager.updateTokens(
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken ?: "",
                    tokenExpiry = System.currentTimeMillis() + ((authData.expiresIn ?: 86400L) * 1000)
                )

                Timber.d("Token refreshed")
                Unit.right()
            } else {
                // Token refresh failed, user needs to re-login
                preferencesManager.clearSession()
                Exception("Session expired. Please login again.").left()
            }
        } catch (e: Exception) {
            Timber.e(e, "Token refresh error")
            e.left()
        }
    }

    override suspend fun requestPasswordReset(email: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection").left()
            }

            val response = apiService.forgotPassword(
                com.hojaz.maiduka26.data.remote.dto.request.ForgotPasswordRequest(email)
            )

            if (response.isSuccessful && response.body()?.success == true) {
                Timber.d("Password reset requested for: $email")
                Unit.right()
            } else {
                val errorMessage = response.body()?.message ?: "Failed to request password reset"
                Exception(errorMessage).left()
            }
        } catch (e: Exception) {
            Timber.e(e, "Password reset request error")
            e.left()
        }
    }

    override suspend fun verifyOtp(target: String, otp: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection").left()
            }

            val response = apiService.verifyOtp(VerifyOtpRequest(target, otp))

            if (response.isSuccessful && response.body()?.success == true) {
                // OTP verification returns full auth data with token and user
                val authData = response.body()?.data

                if (authData != null) {
                    // Update user entity with verified status
                    val userEntity = authData.user.toUserEntity()
                    userDao.insert(userEntity)

                    // Update session with new token (keep partial session, don't set isLoggedIn yet)
                    preferencesManager.savePartialSession(
                        userId = authData.user.id,
                        userName = authData.user.name,
                        email = authData.user.email,
                        phone = authData.user.phone,
                        accessToken = authData.token.accessToken
                    )

                    // If user already has an active shop from verification, save it
                    authData.user.activeShop?.let { activeShop ->
                        preferencesManager.setActiveShop(
                            shopId = activeShop.id,
                            shopName = activeShop.name
                        )
                        saveActiveShopToDatabase(activeShop)
                        // If shop exists, complete registration
                        preferencesManager.completeRegistration()
                    }

                    Timber.d("OTP verified for: $target, user: ${authData.user.id}")
                } else {
                    Timber.d("OTP verified for: $target (no auth data returned)")
                }

                Unit.right()
            } else {
                val errorMessage = response.body()?.message ?: "OTP verification failed"
                Exception(errorMessage).left()
            }
        } catch (e: Exception) {
            Timber.e(e, "OTP verification error")
            e.left()
        }
    }

    override suspend fun sendOtp(target: String): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection").left()
            }

            // TODO: Implement send OTP API call
            // For now, simulate OTP sending
            Timber.d("OTP sent to: $target")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "OTP send error")
            e.left()
        }
    }

    override suspend fun updateProfile(
        name: String?,
        email: String?,
        phone: String?
    ): Either<Throwable, User> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection").left()
            }

            // TODO: Implement update profile API call
            // For now, update locally
            val prefs = preferencesManager.userPreferencesFlow.first()
            val userId = prefs.userId ?: return Exception("Not logged in").left()

            val existingUser = userDao.getUserById(userId)
                ?: return Exception("User not found").left()

            val updatedUser = existingUser.copy(
                name = name ?: existingUser.name,
                email = email ?: existingUser.email,
                phone = phone ?: existingUser.phone,
                updatedAt = System.currentTimeMillis(),
                syncStatus = "pending"
            )

            userDao.update(updatedUser)

            Timber.d("Profile updated for: $userId")
            updatedUser.toDomain().right()
        } catch (e: Exception) {
            Timber.e(e, "Profile update error")
            e.left()
        }
    }

    override suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection").left()
            }

            // TODO: Implement change password API call
            Timber.d("Password change requested")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Password change error")
            e.left()
        }
    }

    override suspend fun deleteAccount(): Either<Throwable, Unit> {
        return try {
            if (!networkMonitor.isOnline) {
                return Exception("No internet connection").left()
            }

            // TODO: Implement delete account API call
            preferencesManager.clearSession()

            Timber.d("Account deletion requested")
            Unit.right()
        } catch (e: Exception) {
            Timber.e(e, "Account deletion error")
            e.left()
        }
    }

    // Helper extension functions
    private fun UserResponse.toDomain(): User {
        return User(
            id = id,
            name = name,
            email = email,
            phone = phone,
            emailVerifiedAt = null, // Not in new response
            phoneVerifiedAt = if (isPhoneVerified) DateTimeUtil.now() else null,
            isPhoneLoginEnabled = isPhoneLoginEnabled,
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }
        )
    }

    private fun UserResponse.toUserEntity(): com.hojaz.maiduka26.data.local.entity.UserEntity {
        return com.hojaz.maiduka26.data.local.entity.UserEntity(
            id = id,
            name = name,
            email = email,
            phone = phone,
            emailVerifiedAt = null,
            phoneVerifiedAt = if (isPhoneVerified) System.currentTimeMillis() else null,
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) }?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }?.let { DateTimeUtil.toMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }

    /**
     * Saves the active shop and its subscription to the local database.
     * @return SubscriptionStatus indicating if user can proceed or needs to renew
     */
    private suspend fun saveActiveShopToDatabase(activeShop: ActiveShopResponse): SubscriptionStatus {
        val prefs = preferencesManager.userPreferencesFlow.first()
        val ownerId = prefs.userId ?: return SubscriptionStatus.NO_USER

        // Save shop entity
        val shopEntity = ShopEntity(
            id = activeShop.id,
            ownerId = ownerId,
            name = activeShop.name,
            businessType = activeShop.businessType.value,
            phoneNumber = activeShop.phoneNumber,
            address = activeShop.address ?: "",
            agentCode = activeShop.agentCode,
            currency = activeShop.currency.code,
            imageUrl = activeShop.imageUrl,
            isActive = activeShop.isActive,
            createdAt = activeShop.createdAt?.let { DateTimeUtil.parseDateTime(it) }
                ?.let { DateTimeUtil.toMillis(it) },
            updatedAt = activeShop.updatedAt?.let { DateTimeUtil.parseDateTime(it) }
                ?.let { DateTimeUtil.toMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
        shopDao.insert(shopEntity)
        Timber.d("Active shop saved: ${activeShop.id}")

        // Save subscription if present
        val subscriptionStatus = activeShop.activeSubscription?.let { subscription ->
            saveSubscriptionToDatabase(activeShop.id, subscription)
        } ?: SubscriptionStatus.NO_SUBSCRIPTION

        return subscriptionStatus
    }

    /**
     * Saves subscription data to the local database.
     * @return SubscriptionStatus based on subscription validity
     */
    private suspend fun saveSubscriptionToDatabase(
        shopId: String,
        subscription: ActiveSubscriptionResponse
    ): SubscriptionStatus {
        val expiresAtMillis = subscription.expiresAt?.let {
            DateTimeUtil.parseDateTime(it)?.let { dt -> DateTimeUtil.toMillis(dt) }
        }

        val subscriptionEntity = SubscriptionEntity(
            id = subscription.id,
            shopId = shopId,
            plan = subscription.plan,
            type = subscription.type,
            status = if (subscription.isActive) "active" else if (subscription.isExpired) "expired" else "pending",
            price = "12000.00", // Default monthly price TZS
            currency = "TZS",
            startsAt = null, // Not provided in response
            expiresAt = expiresAtMillis,
            autoRenew = false,
            paymentMethod = null,
            transactionReference = null,
            features = null,
            maxUsers = null,
            maxProducts = null,
            notes = null,
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )

        subscriptionDao.insert(subscriptionEntity)
        Timber.d("Subscription saved: ${subscription.id}, isActive: ${subscription.isActive}, isExpired: ${subscription.isExpired}")

        // Determine subscription status for navigation
        return when {
            subscription.isExpired -> SubscriptionStatus.EXPIRED
            subscription.isExpiringSoon -> SubscriptionStatus.EXPIRING_SOON
            subscription.isActive -> SubscriptionStatus.ACTIVE
            else -> SubscriptionStatus.INACTIVE
        }
    }

    /**
     * Checks if the current active shop has a valid subscription.
     * @return SubscriptionStatus for the active shop
     */
    suspend fun checkActiveShopSubscription(): SubscriptionStatus {
        val prefs = preferencesManager.userPreferencesFlow.first()
        val activeShopId = prefs.activeShopId ?: return SubscriptionStatus.NO_SHOP

        val subscription = subscriptionDao.getActiveSubscription(activeShopId)
            ?: return SubscriptionStatus.NO_SUBSCRIPTION

        val now = System.currentTimeMillis()
        val expiresAt = subscription.expiresAt ?: return SubscriptionStatus.ACTIVE // No expiry means forever active

        return when {
            now > expiresAt -> SubscriptionStatus.EXPIRED
            now > expiresAt - (7 * 24 * 60 * 60 * 1000) -> SubscriptionStatus.EXPIRING_SOON // 7 days warning
            subscription.status == "active" -> SubscriptionStatus.ACTIVE
            else -> SubscriptionStatus.INACTIVE
        }
    }

    /**
     * Gets the days remaining for the active shop's subscription.
     */
    suspend fun getSubscriptionDaysRemaining(): Int {
        val prefs = preferencesManager.userPreferencesFlow.first()
        val activeShopId = prefs.activeShopId ?: return 0

        val subscription = subscriptionDao.getActiveSubscription(activeShopId) ?: return 0
        val expiresAt = subscription.expiresAt ?: return Int.MAX_VALUE // No expiry

        val now = System.currentTimeMillis()
        val daysRemaining = ((expiresAt - now) / (24 * 60 * 60 * 1000)).toInt()
        return maxOf(0, daysRemaining)
    }
}

/**
 * Represents the subscription status for navigation decisions.
 */
enum class SubscriptionStatus {
    /** Subscription is active and valid */
    ACTIVE,
    /** Subscription is expiring within 7 days */
    EXPIRING_SOON,
    /** Subscription has expired - redirect to payment */
    EXPIRED,
    /** Subscription exists but is inactive */
    INACTIVE,
    /** No subscription found for the shop */
    NO_SUBSCRIPTION,
    /** No active shop selected */
    NO_SHOP,
    /** No user logged in */
    NO_USER
}

