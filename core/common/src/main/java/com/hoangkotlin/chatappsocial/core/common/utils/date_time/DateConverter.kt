package com.hoangkotlin.chatappsocial.core.common.utils.date_time

import java.time.Instant
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.util.Date

internal object DateConverter {

    fun toLocalDateTime(date: Date): LocalDateTime {
        return Instant.ofEpochMilli(date.time)
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime()
    }


    fun toLocalTime(date: Date): LocalTime {
        return Instant.ofEpochMilli(date.time)
            .atZone(ZoneId.systemDefault())
            .toLocalTime()
    }

    fun toDate(localDateTime: LocalDateTime): Date {
        return Date(
            localDateTime.atZone(ZoneId.systemDefault())
                .toInstant()
                .toEpochMilli()
        )
    }
}