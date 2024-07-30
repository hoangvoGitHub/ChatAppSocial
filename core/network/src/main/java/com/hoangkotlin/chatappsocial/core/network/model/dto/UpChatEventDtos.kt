package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import java.util.Date

@Serializable(with = UpChatEventSerializer::class)
sealed class UpChatEventDto {
    abstract val type: ChatEventType
    abstract val createdAt: Date
}

@Serializable
data class UpMessageReadEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    val messageId: String,
    override val type: ChatEventType = ChatEventType.Read
) : UpChatEventDto()

@Serializable
data class UpNewMessageEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    val message: UpChatMessageDto,
    override val type: ChatEventType = ChatEventType.NewMessage
) : UpChatEventDto()

@Serializable
data class UpTypingStartEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    override val type: ChatEventType = ChatEventType.TypingStart
) : UpChatEventDto()

@Serializable
data class UpTypingStopEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    override val type: ChatEventType = ChatEventType.TypingStop
) : UpChatEventDto()

object UpChatEventSerializer :
    JsonContentPolymorphicSerializer<UpChatEventDto>(UpChatEventDto::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<UpChatEventDto> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        return when (type?.let { ChatEventType.valueOf(it) }) {
            ChatEventType.NewMessage -> UpNewMessageEventDto.serializer()
            ChatEventType.TypingStart -> UpTypingStartEventDto.serializer()
            ChatEventType.TypingStop -> UpTypingStopEventDto.serializer()
            ChatEventType.Read -> UpMessageReadEventDto.serializer()
            null -> throw Exception("Unknown Item type")
        }
    }
}

