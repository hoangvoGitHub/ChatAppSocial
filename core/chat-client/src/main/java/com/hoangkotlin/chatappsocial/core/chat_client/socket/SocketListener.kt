
package com.hoangkotlin.chatappsocial.core.chat_client.socket

import com.hoangkotlin.chatappsocial.core.chat_client.utils.ConnectionData
import com.hoangkotlin.chatappsocial.core.chat_client.utils.DisconnectCause
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent

/**
 * Listener which is invoked for WebSocket events.
 */
interface SocketListener {

    /**
     * Invoked when the connection begins to establish and socket state changes to Connecting.
     */
    fun onConnecting()

    /**
     * Invoked when we receive the first [ConnectedEvent] in this connection.
     *
     * Note: This is not invoked when the ws connection is opened but when the [ConnectedEvent] is received.
     *
     * @param event [ConnectedEvent] sent by server as first event once the connection is established.
     */
    fun onConnected(connectionData: ConnectionData?)

    /**
     * Invoked when the web socket connection is disconnected.
     *
     * @param cause [DisconnectCause] reason of disconnection.
     */
    fun onDisconnected(cause: DisconnectCause)

    /**
     * Invoked when there is any error in this web socket connection.
     *
     * @param error [ChatError] object with the error details.
     */
    fun onError(error: String)

    /**
     * Invoked when we receive any successful event.
     *
     * @param event parsed [ChatEvent] received in this web socket connection.
     */
    fun onEvent(event: ChatEvent)
}
