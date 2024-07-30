package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import android.util.Log
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import org.hildan.krossbow.websocket.WebSocketClient
import org.hildan.krossbow.websocket.WebSocketConnection
import org.hildan.krossbow.websocket.WebSocketFrame
import org.hildan.krossbow.websocket.reconnection.WebSocketReconnectionException

class SocialWebSocketConnectionProxy(
    private val baseClient: WebSocketClient,
    private val reconnectConfig: SocialReconnectConfig,
    private val httpHeaders: Map<String, String>,
    private var currentConnection: WebSocketConnection,
) : WebSocketConnection {
    private val scope = CoroutineScope(CoroutineName("krossbow-reconnection-watcher"))

    override val url: String
        get() = currentConnection.url
    override val canSend: Boolean
        get() = currentConnection.canSend

    private val _frames: Channel<WebSocketFrame> = Channel()
    override val incomingFrames: Flow<WebSocketFrame> = _frames.receiveAsFlow()

    init {
        scope.launch {
            while (isActive) {
                try {
                    currentConnection.incomingFrames.collect {
                        _frames.send(it)
                    }
                    _frames.close()
                    break
                } catch (e: CancellationException) {
                    throw e // let cancellation through
                } catch (e: Exception) {
                    try {
                        currentConnection = reconnect(e)
                    } catch (e: CancellationException) {
                        throw e // let cancellation through
                    } catch (e: Exception) {
                        _frames.close(e)
                        break
                    }
                }
            }
        }
    }

    @Throws(WebSocketReconnectionException::class)
    private suspend fun reconnect(cause: Exception): WebSocketConnection {
        var lastAttemptException: Exception = cause
        repeat(reconnectConfig.maxAttempts) { attempt ->
            Log.d(TAG, "reconnect: $attempt")
            reconnectConfig.reconnectListener?.onReconnect()
            if (!reconnectConfig.shouldReconnect(lastAttemptException, attempt)) {
                throw lastAttemptException
            }
            try {
                delay(reconnectConfig.delayStrategy.computeDelay(attempt))
                return baseClient.connect(currentConnection.url, httpHeaders).also {
                    reconnectConfig.afterReconnect(this)
                    reconnectConfig.reconnectListener?.onSuccess(this)
                }
            } catch (e: CancellationException) {
                e.printStackTrace()
                throw e // let cancellation through
            } catch (e: Exception) {
                e.printStackTrace()
                lastAttemptException = e
            }
        }
        throw WebSocketReconnectionException(
            currentConnection.url,
            reconnectConfig.maxAttempts,
            lastAttemptException
        ).also {
            reconnectConfig.reconnectListener?.onFailure(it)

        }
    }

    override suspend fun sendText(frameText: String) {
        currentConnection.sendText(frameText)
    }

    override suspend fun sendBinary(frameData: ByteArray) {
        currentConnection.sendBinary(frameData)
    }

    override suspend fun close(code: Int, reason: String?) {
        currentConnection.close(code, reason)
        scope.cancel()
    }

    companion object {
        private const val TAG = "SocialWebSocketConnection"
    }


}