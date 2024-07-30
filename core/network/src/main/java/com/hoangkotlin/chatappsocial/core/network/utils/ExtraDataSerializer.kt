package com.hoangkotlin.chatappsocial.core.network.utils

import com.hoangkotlin.chatappsocial.core.common.utils.DynamicLookupSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.modules.SerializersModule

internal class ExtraDataSerializer : KSerializer<Map<String, Any>> {

    @OptIn(ExperimentalSerializationApi::class)
    private val mapSerializer =
        MapSerializer(String.serializer(), DynamicLookupSerializer)

    private val adapter = Json {
        serializersModule = SerializersModule {
            contextual(Any::class) {
                DynamicLookupSerializer
            }
        }
    }

    override val descriptor: SerialDescriptor
        get() = mapSerializer.descriptor


    override fun deserialize(decoder: Decoder): Map<String, Any> {
        return  adapter.decodeFromString(decoder.decodeString())
//       return mapSerializer.deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: Map<String, Any>) {
        return mapSerializer.serialize(encoder, value)
    }
}
internal class AnySerializer : KSerializer<Any> {


    override fun deserialize(decoder: Decoder): Any {
        TODO("Not yet implemented")
    }

    override val descriptor: SerialDescriptor
        get() = TODO("Not yet implemented")

    override fun serialize(encoder: Encoder, value: Any) {
        TODO("Not yet implemented")
    }
}

