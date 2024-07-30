package com.hoangkotlin.chatappsocial.core.chat_client.socket

import android.annotation.SuppressLint
import android.util.Log
import com.hoangkotlin.chatappsocial.core.chat_client.utils.ConnectionData
import com.hoangkotlin.chatappsocial.core.chat_client.utils.DisconnectCause
import com.hoangkotlin.chatappsocial.core.chat_client.utils.NetworkStateProvider
import com.hoangkotlin.chatappsocial.core.chat_client.utils.EventMapper.toChatEventDto
import com.hoangkotlin.chatappsocial.core.chat_client.utils.asChatEvent
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.network.di.NetworkModule
import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatEventType
import com.hoangkotlin.chatappsocial.core.network.model.dto.NewMessageEventDto
import com.hoangkotlin.chatappsocial.core.network.retrofit.NetworkConfig
import com.hoangkotlin.chatappsocial.core.network.socket.ChatSocket
import com.hoangkotlin.chatappsocial.core.websocket_naiksoftware.SocialStompClient
import com.hoangkotlin.chatappsocial.core.websocket_naiksoftware.SocialStompClientFactory
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.Job
import okhttp3.OkHttpClient
import ua.naiksoftware.stomp.dto.LifecycleEvent
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.properties.Delegates

private const val TAG = "NaikChatSocket"

@Singleton
class NaikChatSocket @Inject constructor(
    private val networkStateProvider: NetworkStateProvider,
    @Named(NetworkModule.WSClient) private val wsClient: OkHttpClient,
) : ChatSocket {
    private val socketListeners = mutableListOf<SocketListener>()
    private var mStompClient: SocialStompClient? = null
    private var socketConnectionJob: Job? = null
    private var compositeDisposable: CompositeDisposable? = null
    private var currentUser: SocialChatUser? = null
    private val topics = mutableMapOf<String, Disposable?>()
    private var reconnectionAttempts = 0
    private fun reconnect() {
        reconnectionAttempts++
        shutdownSocketConnection()
        setupSocket(currentUser)
        resubscribeChannels()
        Log.d(TAG, "reconnect: attempt: $reconnectionAttempts")
    }

    private fun setupSocket(data: SocialChatUser?) {
        if (state is SocketState.Connected) return
        val isNetworkConnected = networkStateProvider.isConnected()
        state = SocketState.Connecting
        if (isNetworkConnected && data != null) {
//            Stomp.over(
//                Stomp.ConnectionProvider.OKHTTP, NetworkConfig.WS_URL, null, wsClient
//            )
            mStompClient = SocialStompClientFactory.create(NetworkConfig.WS_URL, null, wsClient)
            resetSubscriptions()
            subscribeLifecycle()
            subscribeGlobalTopic(data)
            mStompClient?.connect()
            Log.d(TAG, "setupSocket: After")

        } else {
            state = SocketState.DisconnectedTemporarily("Network is not available")
        }
    }

    @SuppressLint("CheckResult")
    private fun shutdownSocketConnection() {
        mStompClient?.disconnectCompletable()?.subscribe {
            mStompClient = null
        }
        compositeDisposable?.dispose()
    }


    private val networkStateListener = object : NetworkStateProvider.NetworkStateListener {
        override fun onConnected() {
            Log.d(TAG, "onConnected: $state")
            if (state is SocketState.DisconnectedTemporarily || state is SocketState.NetworkDisconnected) {
                reconnect()
            }
        }

        override fun onDisconnected() {
            Log.d(TAG, "onDisconnected: $state")
            if (state is SocketState.Connected || state is SocketState.Connecting) {
                state = SocketState.NetworkDisconnected
            }
        }
    }


    var state: SocketState by Delegates.observable(
        SocketState.DisconnectedTemporarily(null) as SocketState
    ) { _, oldState, newState ->
        Log.d(TAG, "SocketState: oldState $oldState : newState $newState")
        if (oldState != newState) {
            when (newState) {
                is SocketState.Connecting -> {
                    callSocketListener { it.onConnecting() }
                }

                is SocketState.Connected -> {
                    callSocketListener { it.onConnected(newState.connectionData) }
                }

                is SocketState.DisconnectedByRequest -> {
                    shutdownSocketConnection()
                    callSocketListener { it.onDisconnected(DisconnectCause.ConnectionReleased) }
                }

                is SocketState.DisconnectedPermanently -> {
                    shutdownSocketConnection()
                    networkStateProvider.unsubscribe(networkStateListener)
                    callSocketListener {
                        it.onDisconnected(
                            DisconnectCause.UnrecoverableError(newState.error)
                        )
                    }
                }

                is SocketState.DisconnectedTemporarily -> {
                    shutdownSocketConnection()
                    callSocketListener { it.onDisconnected(DisconnectCause.Error(newState.error)) }
                }

                is SocketState.NetworkDisconnected -> {
                    shutdownSocketConnection()
                    callSocketListener { it.onDisconnected(DisconnectCause.NetworkNotAvailable) }
                }
            }
        }
    }

    override fun releaseConnection() {
        reconnectionAttempts = 0
        state = SocketState.DisconnectedByRequest
        socketListeners.clear()
        topics.clear()
    }


    fun removeSocketListener(listener: SocketListener) {
        synchronized(socketListeners) {
            socketListeners.remove(listener)
        }
    }

    fun addSocketListener(listener: SocketListener) {
        synchronized(socketListeners) {
            socketListeners.add(listener)
        }
    }

    override fun connect() {
        val isNetworkConnected = networkStateProvider.isConnected()
        if (isNetworkConnected) {
            setupSocket(currentUser)
        } else {
            state = SocketState.NetworkDisconnected
        }

        networkStateProvider.subscribe(networkStateListener)
    }

    private fun subscribeLifecycle() {
        val disposableLifecycle = mStompClient?.lifecycle()?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())?.subscribe { event ->
                Log.d(TAG, "subscribeLifecycle: ${event.message}")
                state = when (event.type) {
                    LifecycleEvent.Type.OPENED -> SocketState.Connected(
                        connectionData = currentUser?.let {
                            ConnectionData(user = it, token = "")
                        }
                    )

                    LifecycleEvent.Type.CLOSED -> SocketState.DisconnectedByRequest
                    LifecycleEvent.Type.ERROR -> {
                        callSocketListener {
                            it.onError("Socket Error")
                        }
                        SocketState.DisconnectedTemporarily(null)
                    }

                    LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> SocketState.DisconnectedTemporarily(
                        null
                    )

                    else -> state
                }
            }
        compositeDisposable?.add(disposableLifecycle!!)
    }


    private fun subscribeGlobalTopic(data: SocialChatUser) {
        val disposableTopic =
            mStompClient?.topic("/topic/${data.id}")
                ?.subscribeOn(Schedulers.io())
                ?.observeOn(AndroidSchedulers.mainThread())
                ?.subscribe { event ->
                    val chatEventDto = event.toChatEventDto()
                    Log.d(TAG, "subscribeGlobalTopic: /event/${chatEventDto?.type}")
                    // TODO: Need to filter event here, only handle new message + read event
                    callSocketListener {
                        if (chatEventDto != null) {
                            if (chatEventDto.type != ChatEventType.TypingStop ||
                                chatEventDto.type != ChatEventType.TypingStart
                            ) {
                                it.onEvent(chatEventDto.asChatEvent())
                                ack(chatEventDto)
                            }

                        }
                    }
                }
        compositeDisposable?.add(disposableTopic!!)
    }

    fun subscribeChannel(channelId: String) {
        if (topics[channelId]?.isDisposed == false) return
        topics.remove(channelId)
        topics[channelId] = mStompClient?.topic("/topic/${channelId}")
            ?.subscribeOn(Schedulers.io())
            ?.observeOn(AndroidSchedulers.mainThread())
            ?.subscribe { event ->
                val chatEvent = event.toChatEventDto()
                callSocketEventListener(channelId) {
                    if (chatEvent != null) {
                        it.onEvent(chatEvent.asChatEvent())
                        ack(chatEvent)
                    }
                }
            }
    }

    private fun resubscribeChannels() {
        topics.forEach { (channelId, subscribeDisposable) ->
            if (subscribeDisposable?.isDisposed == false) {
                subscribeDisposable.dispose()
                subscribeChannel(channelId)
            }
        }
    }

    fun sendEvent(
        destination: String,
        event: String
    ) {
        if (!isTopicSubscribed(channelId = destination)) {
            subscribeChannel(destination)
        }
        mStompClient?.send(
            "/app/chat/$destination", event,
            "hoangvo"
        )?.blockingAwait()

    }


    fun unsubscribeChannel(channelId: String) {
        topics[channelId]?.dispose()
        topics.remove(channelId)
    }


    @SuppressLint("CheckResult")
    private fun ack(event: ChatEventDto, attempt: Int = 0) {
        if (currentUser == null) return
        if (event is NewMessageEventDto) {
            mStompClient?.send(
                "/app/ack/message_received/${currentUser!!.id}", event.message.id
            )?.doOnError {
                if (attempt < 5) {
                    ack(event, attempt + 1)
                }

            }
        }

    }

    override fun onEvent(event: ChatEventDto) {

    }

    private fun callSocketListener(call: (SocketListener) -> Unit) {
        synchronized(socketListeners) {
            socketListeners.forEach(call)
        }
    }

    private fun callSocketEventListener(
        channelId: String,
        call: (SocketEventListener) -> Unit
    ) {
        synchronized(socketListeners) {
            socketListeners.forEach {
                if (it is SocketEventListener) {
                    call(it)
                }
            }
        }
    }

    private fun resetSubscriptions() {
        compositeDisposable?.dispose()
        compositeDisposable = CompositeDisposable()
    }

    fun setUser(data: SocialChatUser) {
        currentUser = data
    }

    private fun isTopicSubscribed(channelId: String): Boolean {
        return topics[channelId]?.isDisposed == false
    }

    sealed class TestState {
        data object Open : TestState()
        data object Closed : TestState()
        data object Error : TestState()
        data object FailServerHeartbeat : TestState()
    }

    sealed class SocketState {
        data object Connecting : SocketState()

        data class Connected(val connectionData: ConnectionData? = null) : SocketState()
        data object NetworkDisconnected : SocketState() {
            override fun toString(): String = "NetworkDisconnected"
        }

        data class DisconnectedTemporarily(val error: String?) : SocketState()
        data class DisconnectedPermanently(val error: String?) : SocketState()
        data object DisconnectedByRequest : SocketState()
    }
}

fun LifecycleEvent.Type.toSocketState(): NaikChatSocket.TestState {
    return when (this) {
        LifecycleEvent.Type.OPENED -> NaikChatSocket.TestState.Open
        LifecycleEvent.Type.CLOSED -> NaikChatSocket.TestState.Closed
        LifecycleEvent.Type.ERROR -> NaikChatSocket.TestState.Error
        LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT -> NaikChatSocket.TestState.FailServerHeartbeat
    }
}




