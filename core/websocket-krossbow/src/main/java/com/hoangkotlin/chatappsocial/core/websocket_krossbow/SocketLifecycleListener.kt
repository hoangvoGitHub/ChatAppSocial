package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import okhttp3.Response

interface SocketLifecycleListener {
    fun onConnecting()

    fun onOpen()
    /**
     * Invoked when the remote peer has indicated that no more incoming messages will be transmitted.
     */
    fun onClosing(code: Int, reason: String)

    /**
     * Invoked when both peers have indicated that no more messages will be transmitted and the
     * connection has been successfully released. No further calls to this listener will be made.
     */
    fun onClosed(code: Int, reason: String)

    /**
     * Invoked when a web socket has been closed due to an error reading from or writing to the
     * network. Both outgoing isand incoming messages may have been lost. No further calls to this
     * listener will be made.
     */
    fun onFailure( t: Throwable, response: Response?)
}