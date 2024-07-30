package com.hoangkotlin.chatappsocial.core.network.utils

import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatEventType
import com.hoangkotlin.chatappsocial.core.network.model.dto.MessageReadEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.NewMessageEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.TypingStartEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.TypingStopEventDto
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

object ChatEventSerializer : JsonContentPolymorphicSerializer<ChatEventDto>(ChatEventDto::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ChatEventDto> {
        val type = element.jsonObject["type"]?.jsonPrimitive?.content
        return when (type?.let { ChatEventType.valueOf(it) }) {
            ChatEventType.NewMessage -> NewMessageEventDto.serializer()
            ChatEventType.TypingStart -> TypingStartEventDto.serializer()
            ChatEventType.TypingStop -> TypingStopEventDto.serializer()
            ChatEventType.Read -> MessageReadEventDto.serializer()
            null -> throw Exception("Unknown Item type")
        }
    }
}