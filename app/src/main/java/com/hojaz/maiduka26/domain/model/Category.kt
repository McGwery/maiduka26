package com.hojaz.maiduka26.domain.model

import java.time.LocalDateTime

/**
 * Domain model representing a product category.
 */
data class Category(
    val id: String,
    val name: String,
    val description: String? = null,
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null
)

