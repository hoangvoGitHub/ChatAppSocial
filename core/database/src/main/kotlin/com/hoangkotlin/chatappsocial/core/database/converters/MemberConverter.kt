package com.hoangkotlin.chatappsocial.core.database.converters

import android.annotation.SuppressLint
import androidx.room.TypeConverter
import com.hoangkotlin.chatappsocial.core.database.model.ChatMemberEntity
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.encodeToString
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule
import java.text.SimpleDateFormat
import java.util.Date

class MemberConverter {

    private val json = Json {
        serializersModule = SerializersModule {
            contextual(ChatMemberEntity::class, ChatMemberEntity.serializer())
        }
    }

    @TypeConverter
    fun memberToString(member: ChatMemberEntity?): String? {
        println("MemberConverter - ENCODE $member")
        return json.encodeToString(member)
    }

    @TypeConverter
    fun stringToMemberMap(data: String?): ChatMemberEntity? {
        println("MemberConverter - DECODE $data")
        if (data.isNullOrEmpty() || data == "null") {
            return null
        }
        return json.decodeFromString(data)
    }
}

@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = Date::class)
object NullableDateSerializer : KSerializer<Date?> {

    @SuppressLint("SimpleDateFormat")
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

    override fun serialize(encoder: Encoder, value: Date?) {
        val dateString = value?.let { dateFormat.format(it) }
        encoder.encodeString(dateString ?: "null")
    }

    override fun deserialize(decoder: Decoder): Date? {
        val dateString = decoder.decodeString()
        return if (dateString == "null") {
            null
        } else {
            dateFormat.parse(dateString)
        }
    }
}