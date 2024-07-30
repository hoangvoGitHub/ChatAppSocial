package com.hoangkotlin.chatappsocial.core.network.utils

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatterBuilder
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Date::class)
object DateSerializer : KSerializer<Date> {


    // Use a pattern that matches the ISO_OFFSET_DATE_TIME format
    private const val ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX"

    @SuppressLint("NewApi")
    private val simpleDateFormat: SimpleDateFormat =
        SimpleDateFormat(ISO_DATE_TIME_PATTERN, Locale.US).apply {
            timeZone = TimeZone.getDefault()
        }

    override fun deserialize(decoder: Decoder): Date {
        val dateString = decoder.decodeString()
        try {
            return simpleDateFormat.parse(dateString)
                ?: throw ParseException("Invalid date format", 0)
        } catch (e: ParseException) {
            throw IllegalArgumentException("Failed to parse date", e)
        }
    }

    override fun serialize(encoder: Encoder, value: Date) {
        val dateString = simpleDateFormat.format(value)
        encoder.encodeString(dateString)
    }
}

//@OptIn(ExperimentalSerializationApi::class)
//@Serializer(forClass = Date::class)
//object NullableDateSerializer : KSerializer<Date?> {
//
//
//    // Use a pattern that matches the ISO_OFFSET_DATE_TIME format
//    private val ISO_DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSSX"
//
//    @SuppressLint("NewApi")
//    private val simpleDateFormat: SimpleDateFormat =
//        SimpleDateFormat(ISO_DATE_TIME_PATTERN, Locale.US).apply {
//            timeZone = TimeZone.getDefault()
//        }
//
//    override fun deserialize(decoder: Decoder): Date? {
//        val dateString = decoder.decodeString()
//        try {
//            return simpleDateFormat.parse(dateString)
//        } catch (e: ParseException) {
//            throw IllegalArgumentException("Failed to parse date", e)
//        }
//    }
//
//    override fun serialize(encoder: Encoder, value: Date?) {
//        val dateString = value?.let {
//            simpleDateFormat.format(it)
//        } ?: "null"
//        encoder.encodeString(dateString)
//    }
//}