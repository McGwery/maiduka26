package com.hojaz.maiduka26.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a user in the system.
 */
data class User(
    val id: String,
    val name: String,
    val email: String? = null,
    val phone: String? = null,
    val emailVerifiedAt: LocalDateTime? = null,
    val phoneVerifiedAt: LocalDateTime? = null,
    val isPhoneLoginEnabled: Boolean = false,
    val twoFactorEnabled: Boolean = false,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val isEmailVerified: Boolean get() = emailVerifiedAt != null
    val isPhoneVerified: Boolean get() = phoneVerifiedAt != null

    val displayName: String get() = name

    val contactInfo: String? get() = phone ?: email
}

