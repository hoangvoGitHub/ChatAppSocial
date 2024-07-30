package com.hoangkotlin.chatappsocial.core.chat_client.socket

import com.hoangkotlin.chatappsocial.core.chat_client.handler.ChatEventHandler
import com.hoangkotlin.chatappsocial.core.chat_client.utils.ConnectionData
import com.hoangkotlin.chatappsocial.core.chat_client.utils.DisconnectCause
import com.hoangkotlin.chatappsocial.core.chat_client.utils.EventMapper.decodeToChatEventDto
import com.hoangkotlin.chatappsocial.core.chat_client.utils.asChatEvent
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent
import com.hoangkotlin.chatappsocial.core.websocket_krossbow.SocialSocketStompClient
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface SocketManager {
    fun subscribeTopic(topic: String, eventHandler: ChatEventHandler)

    fun unsubscribeTopic(topic: String, eventHandler: ChatEventHandler)

    fun addSocketListener(socketListener: SocketListener)

    fun removeSocketListener(socketListener: SocketListener)

    fun connect(user: SocialChatUser)

    fun releaseConnection()

}

class DefaultSocketManager @Inject constructor(
    private val socket: NaikChatSocket,
    private val stompSocket: SocialSocketStompClient,
) : SocketManager {
    override fun subscribeTopic(topic: String, eventHandler: ChatEventHandler) {
//        socket.subscribeChannel(topic)
        stompSocket.subscribe(topic, onEvent = { rawEvent ->
            val eventDto = rawEvent.decodeToChatEventDto()
            eventDto?.let {
                eventHandler.onChatEvent(it.asChatEvent())
            }

        })
        addSocketListener(SocketEventListener(eventHandler))
    }

    override fun unsubscribeTopic(topic: String, eventHandler: ChatEventHandler) {
//        socket.unsubscribeChannel(topic)
        stompSocket.unsubscribe(topic)
        removeSocketListener(SocketEventListener(eventHandler))
    }

    override fun addSocketListener(socketListener: SocketListener) {
//        socket.addSocketListener(socketListener)
        stompSocket.addSocketListener(SocketListenerAdapter(socketListener))
    }

    override fun removeSocketListener(socketListener: SocketListener) {
//        socket.removeSocketListener(socketListener)
        stompSocket.removeSocketListener(SocketListenerAdapter(socketListener))
    }

    override fun connect(user: SocialChatUser) {
//        socket.setUser(user)
//        socket.connect()
        stompSocket.connect(user)

    }

    override fun releaseConnection() {
//        socket.releaseConnection()
        runBlocking { stompSocket.disconnect() }

    }

    companion object {
        private const val TAG = "SocketManager"
    }
}

class SocketEventListener(
    private val eventHandler: ChatEventHandler
) : SocketListener {
    override fun onConnecting() = Unit

    override fun onConnected(connectionData: ConnectionData?) = Unit

    override fun onDisconnected(cause: DisconnectCause) = Unit

    override fun onError(error: String) = Unit

    override fun onEvent(event: ChatEvent) {
        eventHandler.onChatEvent(event)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is SocketEventListener) return false
        return eventHandler == other.eventHandler
    }

    override fun hashCode(): Int {
        return eventHandler.hashCode()
    }
}
