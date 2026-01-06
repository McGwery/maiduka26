package com.hojaz.maiduka26.util.extensions

/**
 * Extension functions for String.
 */

/**
 * Capitalizes the first letter of each word.
 */
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.lowercase().replaceFirstChar { it.uppercase() }
    }
}

/**
 * Removes all whitespace from the string.
 */
fun String.removeWhitespace(): String {
    return replace("\\s".toRegex(), "")
}

/**
 * Checks if the string is a valid email.
 */
fun String.isValidEmail(): Boolean {
    return android.util.Patterns.EMAIL_ADDRESS.matcher(this).matches()
}

/**
 * Checks if the string is a valid phone number (basic check).
 */
fun String.isValidPhone(): Boolean {
    val cleaned = removeWhitespace().replace("-", "").replace("+", "")
    return cleaned.length in 9..15 && cleaned.all { it.isDigit() }
}

/**
 * Formats a phone number to standard format.
 */
fun String.formatPhoneNumber(): String {
    val cleaned = removeWhitespace().replace("-", "")
    return when {
        cleaned.startsWith("0") && cleaned.length == 10 -> {
            "+255${cleaned.substring(1)}"
        }
        cleaned.startsWith("255") && cleaned.length == 12 -> {
            "+$cleaned"
        }
        cleaned.startsWith("+255") -> cleaned
        else -> cleaned
    }
}

/**
 * Truncates the string to a maximum length with ellipsis.
 */
fun String.truncate(maxLength: Int, ellipsis: String = "..."): String {
    return if (length <= maxLength) this
    else "${take(maxLength - ellipsis.length)}$ellipsis"
}

/**
 * Returns null if the string is blank.
 */
fun String?.takeIfNotBlank(): String? {
    return if (this.isNullOrBlank()) null else this
}

/**
 * Converts a string to a safe filename.
 */
fun String.toSafeFileName(): String {
    return replace(Regex("[^a-zA-Z0-9._-]"), "_")
}

/**
 * Extracts initials from a name (up to 2 characters).
 */
fun String.toInitials(): String {
    return split(" ")
        .filter { it.isNotBlank() }
        .take(2)
        .map { it.first().uppercase() }
        .joinToString("")
}

/**
 * Masks a string, showing only first and last n characters.
 */
fun String.mask(visibleChars: Int = 4, maskChar: Char = '*'): String {
    if (length <= visibleChars * 2) return this
    val start = take(visibleChars)
    val end = takeLast(visibleChars)
    val masked = maskChar.toString().repeat(length - visibleChars * 2)
    return "$start$masked$end"
}

/**
 * Checks if string contains only digits.
 */
fun String.isNumeric(): Boolean {
    return all { it.isDigit() }
}

/**
 * Parses string to Int or returns default.
 */
fun String?.toIntOrDefault(default: Int = 0): Int {
    return this?.toIntOrNull() ?: default
}

/**
 * Parses string to Double or returns default.
 */
fun String?.toDoubleOrDefault(default: Double = 0.0): Double {
    return this?.toDoubleOrNull() ?: default
}

