package com.hojaz.maiduka26.data.repository

import arrow.core.Either
import arrow.core.left
import arrow.core.right
import com.hojaz.maiduka26.data.local.dao.UserDao
import com.hojaz.maiduka26.data.local.preferences.PreferencesManager
import com.hojaz.maiduka26.data.mapper.UserMapper.toDomain
import com.hojaz.maiduka26.data.mapper.UserMapper.toEntity
import com.hojaz.maiduka26.data.remote.api.ApiService
import com.hojaz.maiduka26.data.remote.dto.request.LoginRequest
import com.hojaz.maiduka26.data.remote.dto.request.RefreshTokenRequest
import com.hojaz.maiduka26.data.remote.dto.request.RegisterRequest
import com.hojaz.maiduka26.data.remote.dto.request.VerifyOtpRequest
import com.hojaz.maiduka26.data.remote.dto.response.UserResponse
import com.hojaz.maiduka26.domain.model.User
import com.hojaz.maiduka26.domain.repository.AuthRepository
import com.hojaz.maiduka26.util.DateTimeUtil
import com.hojaz.maiduka26.util.NetworkMonitor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of AuthRepository.
 * Handles authentication operations with offline-first approach.
 */
@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val userDao: UserDao,
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

                // Save session
                preferencesManager.saveUserSession(
                    userId = authData.user.id,
                    userName = authData.user.name,
                    email = authData.user.email,
                    phone = authData.user.phone,
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                    tokenExpiry = System.currentTimeMillis() + (authData.expiresIn * 1000)
                )

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
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                    tokenExpiry = System.currentTimeMillis() + (authData.expiresIn * 1000)
                )

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

                preferencesManager.saveUserSession(
                    userId = authData.user.id,
                    userName = authData.user.name,
                    email = authData.user.email,
                    phone = authData.user.phone,
                    accessToken = authData.accessToken,
                    refreshToken = authData.refreshToken,
                    tokenExpiry = System.currentTimeMillis() + (authData.expiresIn * 1000)
                )

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
                    refreshToken = authData.refreshToken,
                    tokenExpiry = System.currentTimeMillis() + (authData.expiresIn * 1000)
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
                Timber.d("OTP verified for: $target")
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
            emailVerifiedAt = emailVerifiedAt?.let { DateTimeUtil.parseDateTime(it) },
            phoneVerifiedAt = phoneVerifiedAt?.let { DateTimeUtil.parseDateTime(it) },
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
            emailVerifiedAt = emailVerifiedAt?.let { DateTimeUtil.parseDateTime(it) }?.let { DateTimeUtil.toMillis(it) },
            phoneVerifiedAt = phoneVerifiedAt?.let { DateTimeUtil.parseDateTime(it) }?.let { DateTimeUtil.toMillis(it) },
            createdAt = createdAt?.let { DateTimeUtil.parseDateTime(it) }?.let { DateTimeUtil.toMillis(it) },
            updatedAt = updatedAt?.let { DateTimeUtil.parseDateTime(it) }?.let { DateTimeUtil.toMillis(it) },
            syncStatus = "synced",
            lastSyncedAt = System.currentTimeMillis()
        )
    }
}

