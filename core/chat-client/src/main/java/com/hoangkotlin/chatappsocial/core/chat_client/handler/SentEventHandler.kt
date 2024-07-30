package com.hoangkotlin.chatappsocial.core.chat_client.handler

import com.hoangkotlin.chatappsocial.core.chat_client.socket.NaikChatSocket
import com.hoangkotlin.chatappsocial.core.chat_client.utils.EventMapper.payload
import com.hoangkotlin.chatappsocial.core.network.model.dto.UpChatEventDto
import com.hoangkotlin.chatappsocial.core.websocket_krossbow.SocialSocketStompClient
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

interface SentEventHandler {
    fun sentEvent(destination: String, event: UpChatEventDto)
}

class DefaultSentEventHandler @Inject constructor(
    private val chatSocket: NaikChatSocket,
    private val stompSocket: SocialSocketStompClient,
    ) : SentEventHandler {
    override fun sentEvent(destination: String, event: UpChatEventDto) {
        event.payload()?.let { payload ->
//            chatSocket.sendEvent(destination, payload)
            runBlocking {
                stompSocket.sendMessage(destination, payload, "hello")
            }

        }

    }
}