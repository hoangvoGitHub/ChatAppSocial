package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.ChatEventSerializer
import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable(with = ChatEventSerializer::class)
sealed class ChatEventDto {
    abstract val type: ChatEventType
    abstract val createdAt: Date
}

@Serializable
enum class ChatEventType {
    NewMessage,
    TypingStart,
    TypingStop,
    Read
}

@Serializable
data class MessageReadEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    val user: DownChatUserDto,
    val message: DownChatMessageDto,
    override val type: ChatEventType = ChatEventType.Read
) : ChatEventDto()

@Serializable
data class NewMessageEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    val user: DownChatUserDto,
    val message: DownChatMessageDto,
    val unreadCount: Int = 0,
    override val type: ChatEventType = ChatEventType.NewMessage
) : ChatEventDto()

@Serializable
data class TypingStartEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    val user: DownChatUserDto,
    override val type: ChatEventType = ChatEventType.TypingStart
) : ChatEventDto()

@Serializable
data class TypingStopEventDto(
    @Serializable(with = DateSerializer::class)
    override val createdAt: Date,
    val cid: String,
    val user: DownChatUserDto,
    override val type: ChatEventType = ChatEventType.TypingStop
) : ChatEventDto()

