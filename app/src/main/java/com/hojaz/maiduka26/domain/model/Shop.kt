package com.hojaz.maiduka26.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a shop/store.
 */
data class Shop(
    val id: String,
    val ownerId: String,
    val name: String,
    val businessType: String,
    val phoneNumber: String? = null,
    val address: String,
    val agentCode: String? = null,
    val currency: String,
    val imageUrl: String? = null,
    val isActive: Boolean = true,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
) {
    val displayAddress: String get() = address.take(50) + if (address.length > 50) "..." else ""
}

