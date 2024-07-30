package com.hoangkotlin.chatappsocial.core.websocket_krossbow

import android.util.Log
import com.hoangkotlin.chatappsocial.core.common.di.IOScope
import com.hoangkotlin.chatappsocial.core.data.model.Result
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.network.di.NetworkModule
import com.hoangkotlin.chatappsocial.core.network.retrofit.NetworkConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Response
import org.hildan.krossbow.stomp.StompClient
import org.hildan.krossbow.stomp.StompSession
import org.hildan.krossbow.stomp.frame.FrameBody
import org.hildan.krossbow.stomp.headers.StompSendHeaders
import org.hildan.krossbow.stomp.subscribeText
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class SocialSocketStompClient @Inject constructor(
    @Named(NetworkModule.WSClient) private val okHttpClient: OkHttpClient,
    @IOScope private val userScope: CoroutineScope,
) {
    private var currentUser: SocialChatUser? = null

    private val listeners = mutableListOf<RawSocketListener>()

    private val subscriptions = mutableMapOf<String, Job>()


    private val lifecycleListener = object : SocketLifecycleListener {
        override fun onConnecting() {
            callSocketListener { it.onConnecting() }
        }

        override fun onOpen() {
            Log.d(TAG, "onOpen: ")
            callSocketListener { it.onOpen(currentUser!!) }
        }

        override fun onClosing(code: Int, reason: String) {
            Log.d(TAG, "onClosing: ")
//            subscriptions.forEach { it.value.cancel() }
        }

        override fun onClosed(code: Int, reason: String) {
            Log.d(TAG, "onClosed: ")
            callSocketListener { it.onClosed(code, reason) }
        }

        override fun onFailure(t: Throwable, response: Response?) {
            Log.d(TAG, "onFailure: ")
            callSocketListener { it.onFailure(t, response) }
        }
    }

    private val wsClient =
        SocialOkHttpWebSocketClient(okHttpClient, lifecycleListener).withCustomAutoReconnect(
            SocialReconnectConfig(
                reconnectListener = object : ReconnectListener() {
                    override fun onReconnect() {
                        Log.d(TAG, "onReconnect: ")
                        lifecycleListener.onConnecting()
                    }
                },
                afterReconnect = {
                    currentUser?.let {
                        subscribe(it.id)
                    }
                }
            )
        )


    private val stompClient = StompClient(wsClient)

    private var stompSession: StompSession? = null

    private var connectionJob: Job? = null

    fun connect(user: SocialChatUser) {
        if (user == currentUser && stompSession != null) return
        currentUser = user
        connectionJob = userScope.launch {
            Log.d(TAG, "connect: ")
            lifecycleListener.onConnecting()
            stompSession = stompClient.connect(NetworkConfig.WS_URL)
            currentUser?.let {
                subscribe(it.id)
            }
        }
    }

    suspend fun sendMessage(
        destination: String,
        payload: String,
        receipt: String? = null,
        onResult: (Result<Unit>) -> Unit = {}
    ) {
        userScope.launch {
            connectionJob?.join()
            runCatching {
                stompSession?.send(
                    StompSendHeaders(
                        destination = "${NetworkConfig.SENT_DESTINATION_PREFIX}/$destination",
                        receipt = receipt,
                    ),
                    FrameBody.Text(payload)
                ).let {
                    if (it?.id == receipt) {
                        onResult(Result.Success(Unit))
                    } else {
                        onResult(Result.Error())
                    }
                }
            }

        }
    }

    fun subscribe(
        destination: String,
        onEvent: (String) -> Unit = {}
    ) {
        if (subscriptions[destination]?.isActive == true) return
        subscriptions[destination] = userScope.launch {
            stompSession?.subscribeText("${NetworkConfig.TOPIC_PREFIX}/${destination}")
                ?.collect { textFrame ->
                    onEvent(textFrame)
                    callSocketListener { listener -> listener.onEvent(textFrame) }
                }
        }
    }

    fun unsubscribe(destination: String) {
        subscriptions[destination]?.cancel()
        subscriptions.remove(destination)
    }

    suspend fun disconnect() {
        stompSession?.disconnect()
        stompSession = null
        subscriptions.clear()
    }

    fun removeSocketListener(listener: RawSocketListener) {
        synchronized(listeners) {
            listeners.remove(listener)
        }
    }

    fun addSocketListener(listener: RawSocketListener) {
        synchronized(listeners) {
            listeners.add(listener)
        }
    }

    private fun callSocketListener(call: (RawSocketListener) -> Unit) {
        synchronized(listeners) {
            listeners.forEach(call)
        }
    }


    companion object {
        private const val TAG = "SocialSocketStompClient"
    }
}

