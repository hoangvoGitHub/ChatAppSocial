package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import kotlinx.coroutines.flow.Flow
import okhttp3.WebSocket
import okio.ByteString.Companion.toByteString
import org.hildan.krossbow.websocket.WebSocketConnection
import org.hildan.krossbow.websocket.WebSocketFrame

class SocialWebsocketConnectionAdapter(
    private val okSocket: WebSocket,
    override val incomingFrames: Flow<WebSocketFrame>,
) : WebSocketConnection {

    override val url: String
        get() = okSocket.request().url.toString()

    override val canSend: Boolean
        get() = true // all send methods are just no-ops when the session is closed, so always OK

    override suspend fun sendText(frameText: String) {
        okSocket.send(frameText)
    }

    override suspend fun sendBinary(frameData: ByteArray) {
        okSocket.send(frameData.toByteString())
    }

    override suspend fun close(code: Int, reason: String?) {
        okSocket.close(code, reason)
    }
}