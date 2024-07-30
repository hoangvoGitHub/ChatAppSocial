package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import org.hildan.krossbow.websocket.WebSocketClient
import org.hildan.krossbow.websocket.WebSocketConnection

class SocialReconnectingWebSocketClient(
    val baseClient: WebSocketClient,
    private val reconnectConfig: SocialReconnectConfig,
) : WebSocketClient {

    override suspend fun connect(url: String, headers: Map<String, String>): WebSocketConnection {
        val firstConnection = baseClient.connect(url, headers)
        return SocialWebSocketConnectionProxy(baseClient, reconnectConfig, headers, firstConnection)
    }
}