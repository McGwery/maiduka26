package com.hojaz.maiduka26.util

import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale
import java.util.TimeZone

/**
 * Utility class for date and time operations.
 */
object DateTimeUtil {

    private val defaultZoneId: ZoneId = ZoneId.of("Africa/Dar_es_Salaam")

    // Formatters
    private val apiFormatter = DateTimeFormatter.ofPattern(Constants.API_DATE_FORMAT)
    private val displayDateFormatter = DateTimeFormatter.ofPattern(Constants.DISPLAY_DATE_FORMAT)
    private val displayTimeFormatter = DateTimeFormatter.ofPattern(Constants.DISPLAY_TIME_FORMAT)
    private val displayDateTimeFormatter = DateTimeFormatter.ofPattern(Constants.DISPLAY_DATE_TIME_FORMAT)

    /**
     * Gets current timestamp in milliseconds.
     */
    fun currentTimeMillis(): Long = System.currentTimeMillis()

    /**
     * Gets current LocalDateTime in the default timezone.
     */
    fun now(): LocalDateTime = LocalDateTime.now(defaultZoneId)

    /**
     * Gets current LocalDate in the default timezone.
     */
    fun today(): LocalDate = LocalDate.now(defaultZoneId)

    /**
     * Adds days to a LocalDateTime.
     */
    fun plusDays(dateTime: LocalDateTime, days: Long): LocalDateTime {
        return dateTime.plusDays(days)
    }

    /**
     * Adds hours to a LocalDateTime.
     */
    fun plusHours(dateTime: LocalDateTime, hours: Long): LocalDateTime {
        return dateTime.plusHours(hours)
    }

    /**
     * Adds minutes to a LocalDateTime.
     */
    fun plusMinutes(dateTime: LocalDateTime, minutes: Long): LocalDateTime {
        return dateTime.plusMinutes(minutes)
    }

    /**
     * Converts milliseconds to LocalDateTime.
     */
    fun fromMillis(millis: Long): LocalDateTime {
        return Instant.ofEpochMilli(millis)
            .atZone(defaultZoneId)
            .toLocalDateTime()
    }

    /**
     * Converts LocalDateTime to milliseconds.
     */
    fun toMillis(dateTime: LocalDateTime): Long {
        return dateTime.atZone(defaultZoneId)
            .toInstant()
            .toEpochMilli()
    }

    /**
     * Parses API date string to LocalDateTime.
     */
    fun parseApiDate(dateString: String?): LocalDateTime? {
        return try {
            dateString?.let { LocalDateTime.parse(it, apiFormatter) }
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Formats LocalDateTime to API date string.
     */
    fun formatApiDate(dateTime: LocalDateTime): String {
        return apiFormatter.format(dateTime)
    }

    /**
     * Formats LocalDateTime for display.
     */
    fun formatDisplayDate(dateTime: LocalDateTime): String {
        return displayDateFormatter.format(dateTime)
    }

    /**
     * Formats LocalDate for display.
     */
    fun formatDisplayDate(date: LocalDate): String {
        return displayDateFormatter.format(date)
    }

    /**
     * Formats LocalDateTime for display with time.
     */
    fun formatDisplayDateTime(dateTime: LocalDateTime): String {
        return displayDateTimeFormatter.format(dateTime)
    }

    /**
     * Formats time only for display.
     */
    fun formatDisplayTime(dateTime: LocalDateTime): String {
        return displayTimeFormatter.format(dateTime)
    }

    /**
     * Gets a human-readable relative time string (e.g., "2 hours ago").
     */
    fun getRelativeTimeString(dateTime: LocalDateTime): String {
        val now = now()
        val minutes = ChronoUnit.MINUTES.between(dateTime, now)
        val hours = ChronoUnit.HOURS.between(dateTime, now)
        val days = ChronoUnit.DAYS.between(dateTime, now)

        return when {
            minutes < 1 -> "Just now"
            minutes < 60 -> "$minutes min ago"
            hours < 24 -> "$hours hr ago"
            days < 7 -> "$days days ago"
            days < 30 -> "${days / 7} weeks ago"
            days < 365 -> "${days / 30} months ago"
            else -> "${days / 365} years ago"
        }
    }

    /**
     * Gets start of day for a given LocalDateTime.
     */
    fun startOfDay(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.toLocalDate().atStartOfDay()
    }

    /**
     * Gets start of day for a given LocalDate.
     */
    fun startOfDay(date: LocalDate): LocalDateTime {
        return date.atStartOfDay()
    }

    /**
     * Gets end of day for a given LocalDateTime.
     */
    fun endOfDay(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.toLocalDate().atTime(23, 59, 59, 999999999)
    }

    /**
     * Gets end of day for a given LocalDate.
     */
    fun endOfDay(date: LocalDate): LocalDateTime {
        return date.atTime(23, 59, 59, 999999999)
    }

    /**
     * Gets start of month for a given LocalDateTime.
     */
    fun startOfMonth(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.withDayOfMonth(1).toLocalDate().atStartOfDay()
    }

    /**
     * Gets end of month for a given LocalDateTime.
     */
    fun endOfMonth(dateTime: LocalDateTime): LocalDateTime {
        return dateTime.withDayOfMonth(dateTime.toLocalDate().lengthOfMonth())
            .toLocalDate()
            .atTime(23, 59, 59, 999999999)
    }

    /**
     * Checks if a date is today.
     */
    fun isToday(dateTime: LocalDateTime): Boolean {
        return dateTime.toLocalDate() == today()
    }

    /**
     * Checks if a date is yesterday.
     */
    fun isYesterday(dateTime: LocalDateTime): Boolean {
        return dateTime.toLocalDate() == today().minusDays(1)
    }

    /**
     * Gets the number of days between two dates.
     */
    fun daysBetween(start: LocalDate, end: LocalDate): Long {
        return ChronoUnit.DAYS.between(start, end)
    }

    /**
     * Converts Date to LocalDateTime.
     */
    fun fromDate(date: Date): LocalDateTime {
        return date.toInstant()
            .atZone(defaultZoneId)
            .toLocalDateTime()
    }

    /**
     * Converts LocalDateTime to Date.
     */
    fun toDate(dateTime: LocalDateTime): Date {
        return Date.from(dateTime.atZone(defaultZoneId).toInstant())
    }

    /**
     * Parses a date string from API to LocalDateTime.
     * Tries multiple formats for flexibility.
     */
    fun parseDateTime(dateString: String?): LocalDateTime? {
        if (dateString.isNullOrBlank()) return null

        return try {
            LocalDateTime.parse(dateString, apiFormatter)
        } catch (e: Exception) {
            try {
                // Try ISO format
                LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
            } catch (e: Exception) {
                try {
                    // Try with Z suffix
                    Instant.parse(dateString).atZone(defaultZoneId).toLocalDateTime()
                } catch (e: Exception) {
                    null
                }
            }
        }
    }

    /**
     * Parses a date string to milliseconds.
     */
    fun parseMillis(dateString: String?): Long? {
        return parseDateTime(dateString)?.let { toMillis(it) }
    }

    /**
     * Gets start of today in milliseconds.
     */
    fun getStartOfDay(): Long {
        return toMillis(startOfDay(now()))
    }

    /**
     * Gets start of a specific day in milliseconds.
     */
    fun getStartOfDay(dateTime: LocalDateTime): Long {
        return toMillis(startOfDay(dateTime))
    }

    /**
     * Gets end of today in milliseconds.
     */
    fun getEndOfDay(): Long {
        return toMillis(endOfDay(now()))
    }

    /**
     * Gets start of week (Monday) for the current week.
     */
    fun getStartOfWeek(): LocalDateTime {
        val today = today()
        val daysFromMonday = (today.dayOfWeek.value - 1).toLong()
        return today.minusDays(daysFromMonday).atStartOfDay()
    }

    /**
     * Formats milliseconds to display date string.
     */
    fun formatMillisToDisplayDate(millis: Long): String {
        return formatDisplayDate(fromMillis(millis))
    }

    /**
     * Formats milliseconds to display date time string.
     */
    fun formatMillisToDisplayDateTime(millis: Long): String {
        return formatDisplayDateTime(fromMillis(millis))
    }
}

