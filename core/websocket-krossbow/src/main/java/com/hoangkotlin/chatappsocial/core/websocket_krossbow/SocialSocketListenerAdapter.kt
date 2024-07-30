package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import kotlinx.coroutines.runBlocking
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import org.hildan.krossbow.websocket.WebSocketConnection
import org.hildan.krossbow.websocket.WebSocketConnectionException
import org.hildan.krossbow.websocket.WebSocketListenerFlowAdapter
import java.net.SocketException
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * A custom [org.hildan.krossbow.websocket.okhttp.KrossbowToOkHttpListenerAdapter] with the addition of a [SocketLifecycleListener]
 * */
class SocialSocketListenerAdapter(
    connectionContinuation: Continuation<WebSocketConnection>,
    private val channelListener: WebSocketListenerFlowAdapter,
    private val lifecycleListener: SocketLifecycleListener? = null
) : WebSocketListener() {

    private var connectionContinuation: Continuation<WebSocketConnection>? = connectionContinuation

    @Volatile
    private var isConnecting = true

    private inline fun completeConnection(resume: Continuation<WebSocketConnection>.() -> Unit) {
        val cont =
            connectionContinuation ?: error("OkHttp connection continuation already consumed")
        connectionContinuation = null // avoid leaking the continuation
        isConnecting = false
        cont.resume()
    }

    override fun onOpen(webSocket: WebSocket, response: Response) {
        val socialConnection =
            SocialWebsocketConnectionAdapter(webSocket, channelListener.incomingFrames)
        lifecycleListener?.onOpen()
        completeConnection { resume(socialConnection) }
    }

    override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
        runBlocking { channelListener.onBinaryMessage(bytes.toByteArray()) }
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        runBlocking { channelListener.onTextMessage(text) }
    }

    // overriding onClosing and not onClosed because we want to receive the Close frame from the server directly
    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        lifecycleListener?.onClosing(code, reason)
        runBlocking { channelListener.onClose(code, reason) }
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        if (isConnecting) {
            val responseBody = try {
                response?.body?.string()?.takeIf { it.isNotBlank() }
            } catch (e: SocketException) {
                // we can't always read the body when the connection failed
                t.addSuppressed(e)
                null
            }
            val exception = WebSocketConnectionException(
                url = webSocket.request().url.toString(),
                httpStatusCode = response?.code,
                additionalInfo = responseBody,
                cause = t,
            )
            completeConnection {
                resumeWithException(exception)
            }
        } else {
            lifecycleListener?.onFailure(t, response)
            channelListener.onError(t)
        }
    }

    override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
        lifecycleListener?.onClosed(code, reason)
    }
}