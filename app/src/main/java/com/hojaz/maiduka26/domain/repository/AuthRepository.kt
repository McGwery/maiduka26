package com.hojaz.maiduka26.domain.repository

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.User
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for authentication operations.
 */
interface AuthRepository {

    /**
     * Login with email and password.
     */
    suspend fun login(email: String, password: String): Either<Throwable, User>

    /**
     * Login with phone and password.
     */
    suspend fun loginWithPhone(phone: String, password: String): Either<Throwable, User>

    /**
     * Register a new user.
     */
    suspend fun register(
        name: String,
        email: String?,
        phone: String?,
        password: String
    ): Either<Throwable, User>

    /**
     * Logout the current user.
     */
    suspend fun logout(): Either<Throwable, Unit>

    /**
     * Get the currently logged in user.
     */
    fun getCurrentUser(): Flow<User?>

    /**
     * Check if user is logged in.
     */
    fun isLoggedIn(): Flow<Boolean>

    /**
     * Refresh access token.
     */
    suspend fun refreshToken(): Either<Throwable, Unit>

    /**
     * Request password reset.
     */
    suspend fun requestPasswordReset(email: String): Either<Throwable, Unit>

    /**
     * Send OTP code to phone or email.
     */
    suspend fun sendOtp(target: String): Either<Throwable, Unit>

    /**
     * Verify OTP code.
     */
    suspend fun verifyOtp(target: String, otp: String): Either<Throwable, Unit>

    /**
     * Update user profile.
     */
    suspend fun updateProfile(
        name: String?,
        email: String?,
        phone: String?
    ): Either<Throwable, User>

    /**
     * Change password.
     */
    suspend fun changePassword(
        currentPassword: String,
        newPassword: String
    ): Either<Throwable, Unit>

    /**
     * Delete user account.
     */
    suspend fun deleteAccount(): Either<Throwable, Unit>
}

