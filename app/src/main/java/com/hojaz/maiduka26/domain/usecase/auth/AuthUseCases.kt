package com.hojaz.maiduka26.domain.usecase.auth

import arrow.core.Either
import com.hojaz.maiduka26.domain.model.User
import com.hojaz.maiduka26.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for logging in with email.
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Either<Throwable, User> {
        return authRepository.login(email, password)
    }
}

/**
 * Use case for logging in with phone.
 */
class LoginWithPhoneUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(phone: String, password: String): Either<Throwable, User> {
        return authRepository.loginWithPhone(phone, password)
    }
}

/**
 * Use case for registering a new user.
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String,
        email: String?,
        phone: String?,
        password: String
    ): Either<Throwable, User> {
        return authRepository.register(name, email, phone, password)
    }
}

/**
 * Use case for logging out.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Either<Throwable, Unit> {
        return authRepository.logout()
    }
}

/**
 * Use case for getting the current user.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<User?> {
        return authRepository.getCurrentUser()
    }
}

/**
 * Use case for checking if user is logged in.
 */
class IsLoggedInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return authRepository.isLoggedIn()
    }
}

/**
 * Use case for validating/refreshing session.
 */
class ValidateSessionUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Either<Throwable, Unit> {
        return authRepository.refreshToken()
    }
}

/**
 * Use case for requesting password reset.
 */
class RequestPasswordResetUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String): Either<Throwable, Unit> {
        return authRepository.requestPasswordReset(email)
    }
}

/**
 * Use case for sending OTP code.
 */
class SendOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(target: String): Either<Throwable, Unit> {
        return authRepository.sendOtp(target)
    }
}

/**
 * Use case for verifying OTP code.
 */
class VerifyOtpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(target: String, code: String): Either<Throwable, Unit> {
        return authRepository.verifyOtp(target, code)
    }
}

/**
 * Use case for updating user profile.
 */
class UpdateProfileUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        name: String?,
        email: String?,
        phone: String?
    ): Either<Throwable, User> {
        return authRepository.updateProfile(name, email, phone)
    }
}

/**
 * Use case for changing password.
 */
class ChangePasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String
    ): Either<Throwable, Unit> {
        return authRepository.changePassword(currentPassword, newPassword)
    }
}


