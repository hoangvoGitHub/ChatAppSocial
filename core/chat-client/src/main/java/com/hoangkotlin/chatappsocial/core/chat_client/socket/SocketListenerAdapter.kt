package com.hoangkotlin.chatappsocial.core.chat_client.socket

import com.hoangkotlin.chatappsocial.core.chat_client.utils.ConnectionData
import com.hoangkotlin.chatappsocial.core.chat_client.utils.DisconnectCause
import com.hoangkotlin.chatappsocial.core.chat_client.utils.EventMapper.decodeToChatEventDto
import com.hoangkotlin.chatappsocial.core.chat_client.utils.asChatEvent
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent
import com.hoangkotlin.chatappsocial.core.websocket_krossbow.RawSocketListener
import okhttp3.Response

class SocketListenerAdapter(private val socketListener: SocketListener) : RawSocketListener {
    override fun onConnecting() {
        socketListener.onConnecting()
    }

    override fun onOpen(user: SocialChatUser) {
        socketListener.onConnected(ConnectionData(user))
    }

    override fun onClosed(code: Int, reason: String) {
        socketListener.onDisconnected(DisconnectCause.Error(reason))
    }

    override fun onFailure(t: Throwable, response: Response?) {
        socketListener.onDisconnected(DisconnectCause.Error(response?.message))
    }

    override fun onEvent(event: String) {
        parseEvent(event)?.let {
            socketListener.onEvent(it)
        }
    }

    private fun parseEvent(event: String): ChatEvent? {
        return event.decodeToChatEventDto()?.asChatEvent()
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SocketListenerAdapter) return false

        return socketListener == other.socketListener
    }

    override fun hashCode(): Int {
        return socketListener.hashCode()
    }
}