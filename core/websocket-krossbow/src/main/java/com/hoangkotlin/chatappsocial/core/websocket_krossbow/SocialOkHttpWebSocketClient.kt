package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Headers.Companion.toHeaders
import okhttp3.OkHttpClient
import okhttp3.Request
import org.hildan.krossbow.websocket.WebSocketClient
import org.hildan.krossbow.websocket.WebSocketConnection
import org.hildan.krossbow.websocket.WebSocketListenerFlowAdapter

/**
 * A custom [org.hildan.krossbow.websocket.okhttp.OkHttpWebSocketClient] with the addition of a [SocketLifecycleListener]
 * */
class SocialOkHttpWebSocketClient(
    private val client: OkHttpClient = OkHttpClient(),
    private val lifecycleListener: SocketLifecycleListener? = null
) : WebSocketClient {

    override suspend fun connect(
        url: String, headers: Map<String, String>,

        ): WebSocketConnection {
        val request = Request.Builder().url(url).headers(headers.toHeaders()).build()
        val channelListener = WebSocketListenerFlowAdapter()

        return suspendCancellableCoroutine { continuation ->
            val okHttpListener = SocialSocketListenerAdapter(
                continuation, channelListener,
                lifecycleListener
            )
            val ws = client.newWebSocket(request, okHttpListener)
            continuation.invokeOnCancellation {
                ws.cancel()
            }
        }
    }
}

fun WebSocketClient.withCustomAutoReconnect(
    config: SocialReconnectConfig
): WebSocketClient = when (this) {
    is SocialReconnectingWebSocketClient -> SocialReconnectingWebSocketClient(baseClient, config)
    else -> SocialReconnectingWebSocketClient(this, config)
}

