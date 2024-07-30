package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.MapSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

@Serializable
data class QueryChatChannelRequest(
    val direction: QueryMessagesDirection = QueryMessagesDirection.OLDER_THAN_OR_EQUAL,
    val baseMessageId: String = "",
    val messageLimit: Int = 50,
    val memberLimit: Int = 20,
    val watch: Boolean = false,
)

object MapSerializer : KSerializer<Map<String, String>> {

    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

    override fun deserialize(decoder: Decoder): Map<String, String> {
        return mapSerializer.deserialize(decoder)
    }

    override val descriptor: SerialDescriptor
        get() = mapSerializer.descriptor

    override fun serialize(encoder: Encoder, value: Map<String, String>) {
        mapSerializer.serialize(encoder, value)
    }
}

object ListMapSerializer : KSerializer<List<Map<String, String>>> {

    private val mapSerializer = MapSerializer(String.serializer(), String.serializer())

    private val listSerializer = ListSerializer(mapSerializer)
    override fun deserialize(decoder: Decoder): List<Map<String, String>> {
        return listSerializer.deserialize(decoder)
    }

    override val descriptor: SerialDescriptor
        get() = listSerializer.descriptor

    override fun serialize(encoder: Encoder, value: List<Map<String, String>>) {
        listSerializer.serialize(encoder, value)
    }
}