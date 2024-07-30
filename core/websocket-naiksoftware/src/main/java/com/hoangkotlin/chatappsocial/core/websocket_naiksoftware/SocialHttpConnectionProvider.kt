package com.hoangkotlin.chatappsocial.core.websocket_naiksoftware


import io.reactivex.Completable
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import ua.naiksoftware.stomp.dto.LifecycleEvent
import java.util.TreeMap


class SocialHttpConnectionProvider(
    private val uri: String,
    private val connectHeaders: Map<String, String>?,
    private val client: OkHttpClient
) : SocialAbstractConnectionProvider() {


    private var openSocket: WebSocket? = null


    override fun rawDisconnect() {
        openSocket?.close(1000, "")
    }

    override fun createWebSocketConnection() {
        val requestBuilder: Request.Builder = Request.Builder()
            .url(uri)

        connectHeaders?.let {
            addConnectionHeadersToBuilder(requestBuilder, it)

        }

        openSocket = client.newWebSocket(requestBuilder.build(),
            object : WebSocketListener() {
                override fun onOpen(webSocket: WebSocket, response: Response) {
                    val openEvent = LifecycleEvent(LifecycleEvent.Type.OPENED)
                    openEvent.handshakeResponseHeaders = response.extractHeaders()
                    emitLifecycleEvent(openEvent)
                }

                override fun onMessage(webSocket: WebSocket, text: String) {
                    emitMessage(text)
                    handleReceiptAck(text)
                }

                override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                    emitMessage(bytes.utf8())
                    handleReceiptAck(bytes.utf8())

                }

                override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                    openSocket = null
                    emitLifecycleEvent(LifecycleEvent(LifecycleEvent.Type.CLOSED))
                }

                override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                    // in OkHttp, a Failure is equivalent to a JWS-Error *and* a JWS-Close
                    emitLifecycleEvent(LifecycleEvent(LifecycleEvent.Type.ERROR, Exception(t)))
                    openSocket = null
                    emitLifecycleEvent(LifecycleEvent(LifecycleEvent.Type.CLOSED))
                }

                override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                    webSocket.close(code, reason)
                }
            }
        )
    }


    override fun send(stompMessage: String?): Completable {
        return super.send(stompMessage)
    }


    override fun rawSend(stompMessage: String) {
        openSocket?.send(stompMessage)
    }

    override fun getSocket(): Any? {
        return openSocket
    }

    private fun Response.extractHeaders(): TreeMap<String, String> =
        TreeMap<String, String>().apply {
            putAll(headers)
        }

    private fun addConnectionHeadersToBuilder(
        requestBuilder: Request.Builder,
        mConnectHttpHeaders: Map<String, String>
    ) {
        for ((key, value) in mConnectHttpHeaders) {
            requestBuilder.addHeader(key, value)
        }
    }


    companion object {
        private const val TAG = "SocialHttpConnectionProvider"
    }
}