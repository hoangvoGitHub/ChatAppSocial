package com.hoangkotlin.chatappsocial.core.common.utils.date_time

import android.content.Context
import android.text.format.DateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class DefaultDateFormatter(
    private val dateContext: DateContext
) : DateFormatter {

    constructor(context: Context) : this(DefaultDateContext(context))


    private val timeFormatter12h = DateTimeFormatter.ofPattern("h:mm a")
    private val timeFormatter24h = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatterDayOfWeekWithTime12h = DateTimeFormatter.ofPattern("EEEE,h:mm a")
    private val dateFormatterDayOfWeekWithTime24h = DateTimeFormatter.ofPattern("EEEE, HH:mm")
    private val dateFormatterDayOfYearWithTime12h = DateTimeFormatter.ofPattern("MMMM dd, h:mm a")
    private val dateFormatterDayOfYearWithTime24h = DateTimeFormatter.ofPattern("MMMM dd, HH:mm")


    private val dateFormatterFullDate: DateTimeFormatter
        // Re-evaluated every time to account for runtime Locale changes
        get() = DateTimeFormatter.ofPattern(dateContext.dateTimePattern())

    override fun formatDate(localDateTime: LocalDateTime?): String {
        localDateTime ?: return ""

        val localDate = localDateTime.toLocalDate()
        val localTime = localDateTime.toLocalTime()
        val zonedDateTime =
            ZonedDateTime.of(localDate, localTime, ZoneId.systemDefault())

        val formatter = when {
            localDate.isToday() -> {
                if (dateContext.is24Hour())
                    timeFormatter24h
                else
                    timeFormatter12h
            }

            localDate.isInThisWeek() -> {
                if (dateContext.is24Hour())
                    dateFormatterDayOfWeekWithTime24h
                else
                    dateFormatterDayOfWeekWithTime12h
            }

            localDate.isInThisYear() -> {
                if (dateContext.is24Hour()) {
                    dateFormatterDayOfYearWithTime24h
                } else {
                    dateFormatterDayOfYearWithTime12h
                }
            }

            else -> dateFormatterFullDate
        }

        return formatter.format(zonedDateTime).capitalize(Locale.getDefault())
    }

    override fun formatTime(localTime: LocalTime?): String {
        localTime ?: return ""
        val formatter = if (dateContext.is24Hour()) timeFormatter24h else timeFormatter12h
        return formatter.format(localTime).capitalize(Locale.getDefault())
    }

    private fun LocalDate.isToday(): Boolean {
        return this == dateContext.now()
    }

    private fun LocalDate.isInThisWeek(): Boolean {
        return this.isInThisYear()
                && this.isInThisMonth()
                && this.dayOfMonth >= (dateContext.now().dayOfMonth - (dateContext.now().dayOfWeek.value - 1))
                && this.dayOfMonth <= (dateContext.now().dayOfMonth + (DAYS_IN_A_WEEK - dateContext.now().dayOfWeek.value - 1))
    }

    private fun LocalDate.isInThisMonth(): Boolean {
        return this.month == dateContext.now().month && this.isInThisYear()
    }

    private fun LocalDate.isInThisYear(): Boolean {
        return this.year == dateContext.now().year
    }

    interface DateContext {
        fun now(): LocalDate
        fun is24Hour(): Boolean
        fun dateTimePattern(): String
    }


    private class DefaultDateContext(
        private val context: Context
    ) : DateContext {
        override fun now(): LocalDate = LocalDate.now()

        override fun is24Hour(): Boolean {
            return DateFormat.is24HourFormat(context)
        }

        override fun dateTimePattern(): String {
            if (is24Hour()) {
                return DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM dd yyyy, HH:mm")
            }
            return DateFormat.getBestDateTimePattern(Locale.getDefault(), "MMMM dd yyyy, h:mm a")

        }


    }
}

fun String.capitalize(locale: Locale = Locale.getDefault()): String {
    return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase(locale) else it.toString() }
}