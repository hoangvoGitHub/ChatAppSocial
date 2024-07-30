package com.hoangkotlin.chatappsocial.core.database.converters

import androidx.room.TypeConverter
import com.hoangkotlin.chatappsocial.core.common.utils.DynamicLookupSerializer
import com.hoangkotlin.chatappsocial.core.common.utils.toJsonObject
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.modules.SerializersModule

class ExtraDataConverter {
    @OptIn(ExperimentalSerializationApi::class)
    private val adapter = Json {
        serializersModule = SerializersModule {
            contextual(Any::class) {
                DynamicLookupSerializer
            }
        }
    }

    @TypeConverter
    fun stringToMap(data: String?): Map<String, String>? {
        if (data.isNullOrEmpty() || data == "null") {
            return emptyMap()
        }
        return adapter.decodeFromString(data)
    }

    @TypeConverter
    fun mapToString(someObjects: Map<String, String>?): String {
        if (someObjects == null) {
            return "{}"
        }
        return adapter.encodeToString(someObjects.toJsonObject())
    }
}

