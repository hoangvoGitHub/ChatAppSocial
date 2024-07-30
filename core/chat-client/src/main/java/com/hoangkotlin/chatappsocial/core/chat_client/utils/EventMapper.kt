package com.hoangkotlin.chatappsocial.core.chat_client.utils

import com.hoangkotlin.chatappsocial.core.chat_client.api.asSocialChatMessage
import com.hoangkotlin.chatappsocial.core.chat_client.api.asSocialChatUser
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent
import com.hoangkotlin.chatappsocial.core.model.events.MessageReadEvent
import com.hoangkotlin.chatappsocial.core.model.events.NewMessageEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStartEvent
import com.hoangkotlin.chatappsocial.core.model.events.TypingStopEvent
import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.MessageReadEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.NewMessageEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.TypingStartEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.TypingStopEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.UpChatEventDto
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import ua.naiksoftware.stomp.dto.StompMessage

private const val TAG = "EventMapper"

object EventMapper {

    private val json by lazy {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    fun String.decodeToChatEventDto(): ChatEventDto? {
        return try {
            json.decodeFromString(ChatEventDto.serializer(), this)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun StompMessage.toChatEventDto(): ChatEventDto? {
        return try {
            json.decodeFromString(ChatEventDto.serializer(), this.payload)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun UpChatEventDto.payload(): String? {
        return try {
            json.encodeToString(UpChatEventDto.serializer(), this)
        } catch (e: SerializationException) {
            e.printStackTrace()
            null
        }
    }

}

fun ChatEventDto.asChatEvent(): ChatEvent {
    return when (this) {
        is NewMessageEventDto -> this.asNewMessageEvent()
        is TypingStartEventDto -> this.asTypingStartEvent()
        is TypingStopEventDto -> this.asTypingStopEvent()
        is MessageReadEventDto -> this.asMessageReadEvent()
    }
}

fun MessageReadEventDto.asMessageReadEvent(): MessageReadEvent {
    return MessageReadEvent(
        type = type.name,
        createdAt = createdAt,
        cid = cid,
        message = message.asSocialChatMessage(),
        user = user.asSocialChatUser()
    )
}

fun NewMessageEventDto.asNewMessageEvent(): NewMessageEvent {
    return NewMessageEvent(
        type = type.name,
        createdAt = createdAt,
        cid = cid,
        message = message.asSocialChatMessage(),
        unreadCount = unreadCount
    )
}

fun TypingStartEventDto.asTypingStartEvent(): TypingStartEvent {
    return TypingStartEvent(
        type = type.name,
        createdAt = createdAt,
        cid = cid,
        user = user.asSocialChatUser()
    )
}

fun TypingStopEventDto.asTypingStopEvent(): TypingStopEvent {
    return TypingStopEvent(
        type = type.name,
        createdAt = createdAt,
        cid = cid,
        user = user.asSocialChatUser()
    )
}