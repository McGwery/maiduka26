package com.hojaz.maiduka26.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

/**
 * Utility class for currency formatting.
 */
object CurrencyFormatter {

    private val currencySymbols = mapOf(
        "TZS" to "TSh",
        "KES" to "KSh",
        "UGX" to "USh",
        "USD" to "$",
        "EUR" to "€",
        "GBP" to "£"
    )

    private val defaultDecimalFormat = DecimalFormat("#,##0.00")
    private val wholeNumberFormat = DecimalFormat("#,##0")

    /**
     * Formats a double value to currency string.
     */
    fun format(amount: Double, currencyCode: String = "TZS", showDecimals: Boolean = true): String {
        val symbol = currencySymbols[currencyCode] ?: currencyCode
        val formattedNumber = if (showDecimals) {
            defaultDecimalFormat.format(amount)
        } else {
            wholeNumberFormat.format(amount)
        }
        return "$symbol $formattedNumber"
    }

    /**
     * Formats a BigDecimal value to currency string.
     */
    fun format(amount: BigDecimal, currencyCode: String = "TZS", showDecimals: Boolean = true): String {
        return format(amount.toDouble(), currencyCode, showDecimals)
    }

    /**
     * Formats a string amount to currency string.
     */
    fun format(amount: String?, currencyCode: String = "TZS", showDecimals: Boolean = true): String {
        val value = amount?.toDoubleOrNull() ?: 0.0
        return format(value, currencyCode, showDecimals)
    }

    /**
     * Formats a number to compact form (e.g., 1.5K, 2.3M).
     */
    fun formatCompact(amount: Double, currencyCode: String = "TZS"): String {
        val symbol = currencySymbols[currencyCode] ?: currencyCode
        val formattedNumber = when {
            amount >= 1_000_000_000 -> String.format("%.1fB", amount / 1_000_000_000)
            amount >= 1_000_000 -> String.format("%.1fM", amount / 1_000_000)
            amount >= 1_000 -> String.format("%.1fK", amount / 1_000)
            else -> wholeNumberFormat.format(amount)
        }
        return "$symbol $formattedNumber"
    }

    /**
     * Parses a currency string back to double.
     */
    fun parse(formattedAmount: String): Double {
        // Remove currency symbol and spaces
        val cleanedString = formattedAmount
            .replace(Regex("[^\\d.,\\-]"), "")
            .replace(",", "")
        return cleanedString.toDoubleOrNull() ?: 0.0
    }

    /**
     * Gets the currency symbol for a currency code.
     */
    fun getSymbol(currencyCode: String): String {
        return currencySymbols[currencyCode] ?: currencyCode
    }

    /**
     * Formats amount with positive/negative indicator.
     */
    fun formatWithSign(amount: Double, currencyCode: String = "TZS"): String {
        val formatted = format(kotlin.math.abs(amount), currencyCode)
        return when {
            amount > 0 -> "+$formatted"
            amount < 0 -> "-$formatted"
            else -> formatted
        }
    }

    /**
     * Formats percentage.
     */
    fun formatPercentage(value: Double, decimals: Int = 1): String {
        return String.format("%.${decimals}f%%", value)
    }

    /**
     * Rounds amount to 2 decimal places.
     */
    fun round(amount: Double): Double {
        return BigDecimal(amount)
            .setScale(2, RoundingMode.HALF_UP)
            .toDouble()
    }

    /**
     * Calculates discount amount from percentage.
     */
    fun calculateDiscount(originalPrice: Double, discountPercentage: Double): Double {
        return round(originalPrice * (discountPercentage / 100))
    }

    /**
     * Calculates percentage of a value.
     */
    fun calculatePercentage(value: Double, total: Double): Double {
        if (total == 0.0) return 0.0
        return round((value / total) * 100)
    }
}

