package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import org.hildan.krossbow.websocket.WebSocketConnection
import org.hildan.krossbow.websocket.reconnection.FixedDelay
import org.hildan.krossbow.websocket.reconnection.RetryDelayStrategy
import org.hildan.krossbow.websocket.reconnection.WebSocketReconnectionException
import kotlin.time.Duration.Companion.seconds

/**
 * The default value for the maximum number of reconnection attempts before giving up.
 */
internal const val SOCIAL_DEFAULT_MAX_ATTEMPTS = 5

/**
 * The default value for the reconnection delay strategy.
 */
internal val SOCIAL_DEFAULT_DELAY_STRATEGY = FixedDelay(1.seconds)

data class SocialReconnectConfig(
    val maxAttempts: Int = SOCIAL_DEFAULT_MAX_ATTEMPTS,
    /**
     * Defines the time to wait before each reconnection attempt.
     */
    val delayStrategy: RetryDelayStrategy = SOCIAL_DEFAULT_DELAY_STRATEGY,
    /**
     * A predicate to decide whether the web socket should be reconnected when the given `exception` occur.
     * The `attempt` parameter is the index of the current reconnection attempt in a series of retries.
     *
     * When the web socket throws an exception, this predicate is called with attempt 0 before trying to reconnect.
     * If the predicate returns false, the exception is rethrown and no reconnection is attempted.
     * If the predicate returns true, a reconnection is attempted.
     *
     * If the reconnection fails, the predicate is called again with attempt 1, and so on.
     * If the reconnection succeeds, and later a new error occurs on the web socket, the predicate will be called
     * again, with attempt 0.
     *
     * The predicate will not be called if [maxAttempts] is reached. If you want to control the maximum attempts via
     * the predicate, set [maxAttempts] to a bigger value (such as [Int.MAX_VALUE]).
     */
    val shouldReconnect: suspend (exception: Throwable, attempt: Int) -> Boolean = { _, _ -> true },
    /**
     * A callback called each time the web socket is successfully reconnected.
     *
     * The [WebSocketConnection] is the same proxy instance after each reconnect, it is just provided for convenience.
     * It is *not* the new underlying connection, which is an implementation detail.
     */
    val afterReconnect: suspend (WebSocketConnection) -> Unit = {},

    val onReconnect: suspend () -> Unit = {},

    val reconnectListener: ReconnectListener? = null
)

abstract class ReconnectListener {
    open fun onReconnect(){}

    fun onSuccess(connection: WebSocketConnection){}

    fun onFailure(e: WebSocketReconnectionException){}
}
