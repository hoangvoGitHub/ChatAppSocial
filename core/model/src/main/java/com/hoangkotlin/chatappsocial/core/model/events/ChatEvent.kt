package com.hoangkotlin.chatappsocial.core.model.events

import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import java.util.Date
//
//enum class EventType{
//    NewMessage,
//    TypingStart,
//    TypingStop,
//    Read
//}

sealed class  ChatEvent {
    abstract val type: String
    abstract val createdAt: Date
}

sealed class CidEvent: ChatEvent(){
    abstract val cid: String
}
 sealed interface HasChannel {
     val channel: SocialChatChannel
}

sealed interface HasMessage{
    val message: SocialChatMessage
}

sealed interface HasUser {
    val user: SocialChatUser
}

data class MessageReadEvent(
    override val type: String,
    override val createdAt: Date,
    override val cid: String,
    override val message: SocialChatMessage,
    val user: SocialChatUser
): HasMessage, CidEvent()


data class ChannelUpdateEvent(
    override val type: String,
    override val channel: SocialChatChannel,
    override val cid: String,
    override val createdAt: Date,
    val message: SocialChatMessage?
):HasChannel, CidEvent()

data class NewMessageEvent(
    override val type: String,
    override val createdAt: Date,
    override val cid: String,
    override val message: SocialChatMessage,
//    override val user: SocialChatUser,
    val unreadCount: Int = 0,
): HasMessage, CidEvent()

data class TypingStartEvent(
    override val type: String ,
    override val createdAt: Date,
    override val cid: String,
    override val user: SocialChatUser,
): CidEvent(), HasUser

data class TypingStopEvent(
    override val type: String,
    override val createdAt: Date,
    override val cid: String,
    override val user: SocialChatUser,
): CidEvent(), HasUser


