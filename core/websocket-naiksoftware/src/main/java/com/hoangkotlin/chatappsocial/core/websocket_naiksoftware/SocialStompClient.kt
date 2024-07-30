package com.hoangkotlin.chatappsocial.core.websocket_naiksoftware

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.CompletableSource
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.subjects.BehaviorSubject
import io.reactivex.subjects.PublishSubject
import ua.naiksoftware.stomp.HeartBeatTask
import ua.naiksoftware.stomp.dto.LifecycleEvent
import ua.naiksoftware.stomp.dto.StompCommand
import ua.naiksoftware.stomp.dto.StompHeader
import ua.naiksoftware.stomp.dto.StompMessage
import ua.naiksoftware.stomp.pathmatcher.PathMatcher
import ua.naiksoftware.stomp.pathmatcher.SimplePathMatcher
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

class SocialStompClient(
    private val connectionProvider: SocialAbstractConnectionProvider
) {
    private val SUPPORTED_VERSIONS = "1.1,1.2"
    private val DEFAULT_ACK = "auto"

    //    private var connectionProvider: ConnectionProvider? = null
    private var topics: ConcurrentHashMap<String, String?>? = null
    private var legacyWhitespace = false

    private var messageStream: PublishSubject<StompMessage>? = null
    private var connectionStream: BehaviorSubject<Boolean?>? = null
    private var streamMap: ConcurrentHashMap<String, Flowable<StompMessage>> = ConcurrentHashMap()
    private var pathMatcher: PathMatcher = SimplePathMatcher()
    private var lifecycleDisposable: Disposable? = null
    private var messagesDisposable: Disposable? = null
    private val lifecyclePublishSubject: PublishSubject<LifecycleEvent> by lazy {
        PublishSubject.create()
    }
    private var headers: List<StompHeader>? = null
    private val heartBeatTask: HeartBeatTask by lazy {
        HeartBeatTask(
            { pingMessage: String -> sendHeartBeat(pingMessage) }
        ) { lifecyclePublishSubject.onNext(LifecycleEvent(LifecycleEvent.Type.FAILED_SERVER_HEARTBEAT)) }
    }


    /**
     * Sets the heartbeat interval to request from the server.
     *
     *
     * Not very useful yet, because we don't have any heartbeat logic on our side.
     *
     * @param ms heartbeat time in milliseconds
     */
    fun withServerHeartbeat(ms: Int): SocialStompClient {
        heartBeatTask.serverHeartbeat = ms
        return this
    }

    /**
     * Sets the heartbeat interval that client propose to send.
     *
     *
     * Not very useful yet, because we don't have any heartbeat logic on our side.
     *
     * @param ms heartbeat time in milliseconds
     */
    fun withClientHeartbeat(ms: Int): SocialStompClient {
        heartBeatTask.clientHeartbeat = ms
        return this
    }

    /**
     * Connect without reconnect if connected
     */
    fun connect() {
        connect(null)
    }

    /**
     * Connect to websocket. If already connected, this will silently fail.
     *
     * @param _headers HTTP headers to send in the INITIAL REQUEST, i.e. during the protocol upgrade
     */
    @SuppressLint("CheckResult")
    fun connect(_headers: List<StompHeader>?) {
        Log.d(TAG, "Connect")
        headers = _headers
        if (isConnected()) {
            Log.d(TAG, "Already connected, ignore")
            return
        }
        lifecycleDisposable = connectionProvider.lifecycle()
            .subscribe { lifecycleEvent: LifecycleEvent ->
                when (lifecycleEvent.type) {
                    LifecycleEvent.Type.OPENED -> {
                        val headers: MutableList<StompHeader> =
                            ArrayList()
                        headers.add(StompHeader(StompHeader.VERSION, SUPPORTED_VERSIONS))
                        headers.add(
                            StompHeader(
                                StompHeader.HEART_BEAT,
                                heartBeatTask.clientHeartbeat
                                    .toString() + "," + heartBeatTask.serverHeartbeat
                            )
                        )
                        if (_headers != null) headers.addAll(_headers)
                        connectionProvider.send(
                            StompMessage(
                                StompCommand.CONNECT,
                                headers,
                                null
                            ).compile(legacyWhitespace)
                        )
                            .subscribe {
                                Log.d(TAG, "Publish open")
                                lifecyclePublishSubject.onNext(lifecycleEvent)
                            }
                    }

                    LifecycleEvent.Type.CLOSED -> {
                        Log.d(TAG, "Socket closed")
                        disconnect()
                    }

                    LifecycleEvent.Type.ERROR -> {
                        Log.d(
                            TAG,
                            "Socket closed with error"
                        )
                        lifecyclePublishSubject.onNext(lifecycleEvent)
                    }

                    else -> Unit
                }
            }
        messagesDisposable = connectionProvider.messages()
            .map { data: String? ->
                StompMessage.from(
                    data
                )
            }
            .filter { message: StompMessage? ->
                heartBeatTask.consumeHeartBeat(
                    message
                )
            }
            .doOnNext(getMessageStream()::onNext)
            .filter { msg: StompMessage -> msg.stompCommand == StompCommand.CONNECTED }
            .subscribe(
                {
                    getConnectionStream().onNext(
                        true
                    )
                },
                { onError: Throwable? ->
                    Log.e(
                        TAG,
                        "Error parsing message",
                        onError
                    )
                })
    }

    @Synchronized
    private fun getConnectionStream(): BehaviorSubject<Boolean?> {
        if (connectionStream == null || connectionStream!!.hasComplete()) {
            connectionStream = BehaviorSubject.createDefault(false)
        }
        return connectionStream!!
    }

    @Synchronized
    private fun getMessageStream(): PublishSubject<StompMessage> {
        if (messageStream == null || messageStream!!.hasComplete()) {
            return PublishSubject.create<StompMessage>().also {
                messageStream = it
            }
        }
        return messageStream!!
    }

    fun send(destination: String): Completable {
        return send(destination)
    }

    fun send(destination: String, data: String? = null): Completable {
        return send(
            StompMessage(
                StompCommand.SEND, listOf(StompHeader(StompHeader.DESTINATION, destination)),
                data
            )
        )
    }

    fun send(
        destination: String, data: String? = null,
        recipientId: String? = null
    ): Completable {
        val headers = if (recipientId != null) {
            listOf(
                StompHeader(StompHeader.DESTINATION, destination),
                StompHeader("receipt", recipientId)
            )
        } else {
            listOf(
                StompHeader(StompHeader.DESTINATION, destination)
            )
        }
        return send(
            StompMessage(
                StompCommand.SEND,
                headers,
                data,
            ),
            recipientId
        )
    }


    fun send(
        stompMessage: StompMessage,
        receiptId: String? = null
    ): Completable {
        val completable: Completable =
            connectionProvider.send(stompMessage.compile(legacyWhitespace), receiptId)
        val connectionComplete: CompletableSource = getConnectionStream()
            .filter { isConnected: Boolean? -> isConnected!! }
            .firstElement().ignoreElement()
        return completable
            .startWith(connectionComplete)
    }

    @SuppressLint("CheckResult")
    private fun sendHeartBeat(pingMessage: String) {
        val completable: Completable = connectionProvider.send(pingMessage)
        val connectionComplete: CompletableSource = getConnectionStream()
            .filter { isConnected: Boolean? -> isConnected!! }
            .firstElement().ignoreElement()
        completable.startWith(connectionComplete)
            .onErrorComplete()
            .subscribe()
    }

    fun lifecycle(): Flowable<LifecycleEvent>? {
        return lifecyclePublishSubject.toFlowable(BackpressureStrategy.BUFFER)
    }

    /**
     * Disconnect from server, and then reconnect with the last-used headers
     */
    @SuppressLint("CheckResult")
    fun reconnect() {
        disconnectCompletable()
            .subscribe(
                { connect(headers) }
            ) { e: Throwable? ->
                Log.e(
                    TAG,
                    "Disconnect error",
                    e
                )
            }
    }

    @SuppressLint("CheckResult")
    fun disconnect() {
        disconnectCompletable().subscribe(
            {}
        ) { e: Throwable? ->
            Log.e(
                TAG,
                "Disconnect error",
                e
            )
        }
    }

    fun disconnectCompletable(): Completable {
        heartBeatTask.shutdown()
        if (lifecycleDisposable != null) {
            lifecycleDisposable!!.dispose()
        }
        if (messagesDisposable != null) {
            messagesDisposable!!.dispose()
        }
        return connectionProvider.disconnect()
            .doFinally(Action {
                Log.d(TAG, "Stomp disconnected")
                getConnectionStream().onComplete()
                getMessageStream().onComplete()
                lifecyclePublishSubject.onNext(LifecycleEvent(LifecycleEvent.Type.CLOSED))
            })
    }

    fun topic(destinationPath: String?): Flowable<StompMessage>? {
        return topic(destinationPath, null)
    }

    fun topic(destPath: String?, headerList: List<StompHeader>?): Flowable<StompMessage>? {
        if (destPath == null) return Flowable.error(IllegalArgumentException("Topic path cannot be null")) else if (!streamMap!!.containsKey(
                destPath
            )
        ) streamMap[destPath] =
            Completable.defer {
                subscribePath(
                    destPath,
                    headerList
                )
            }.andThen(
                getMessageStream()
                    .filter { msg: StompMessage? ->
                        pathMatcher.matches(
                            destPath,
                            msg
                        )
                    }
                    .toFlowable(BackpressureStrategy.BUFFER)
                    .doFinally { unsubscribePath(destPath).subscribe() }
                    .share())
        return streamMap!![destPath]
    }

    private fun subscribePath(
        destinationPath: String,
        headerList: List<StompHeader>?
    ): Completable? {
        val topicId = UUID.randomUUID().toString()
        if (topics == null) topics = ConcurrentHashMap()

        // Only continue if we don't already have a subscription to the topic
        if (topics!!.containsKey(destinationPath)) {
            Log.d(TAG, "Attempted to subscribe to already-subscribed path!")
            return Completable.complete()
        }
        topics!![destinationPath] = topicId
        val headers: MutableList<StompHeader> = ArrayList()
        headers.add(StompHeader(StompHeader.ID, topicId))
        headers.add(StompHeader(StompHeader.DESTINATION, destinationPath))
        headers.add(StompHeader(StompHeader.ACK, DEFAULT_ACK))
        if (headerList != null) headers.addAll(headerList)
        return send(
            StompMessage(
                StompCommand.SUBSCRIBE,
                headers, null
            )
        )
            .doOnError {
                unsubscribePath(
                    destinationPath
                ).subscribe()
            }
    }


    private fun unsubscribePath(dest: String): Completable {
        streamMap!!.remove(dest)
        val topicId = topics!![dest] ?: return Completable.complete()
        topics!!.remove(dest)
        Log.d(
            TAG,
            "Unsubscribe path: $dest id: $topicId"
        )
        return send(
            StompMessage(
                StompCommand.UNSUBSCRIBE,
                listOf(StompHeader(StompHeader.ID, topicId)),
                null
            )
        ).onErrorComplete()
    }

    /**
     * Set the wildcard or other matcher for Topic subscription.
     *
     *
     * Right now, the only options are simple, rmq supported.
     * But you can write you own matcher by implementing [PathMatcher]
     *
     *
     * When set to [ua.naiksoftware.stomp.pathmatcher.RabbitPathMatcher], topic subscription allows for RMQ-style wildcards.
     *
     *
     *
     * @param pathMatcher Set to [SimplePathMatcher] by default
     */
    fun setPathMatcher(pathMatcher: PathMatcher) {
        this.pathMatcher = pathMatcher
    }

    fun isConnected(): Boolean {
        return getConnectionStream().value!!
    }

    /**
     * Reverts to the old frame formatting, which included two newlines between the message body
     * and the end-of-frame marker.
     *
     *
     * Legacy: Body\n\n^@
     *
     *
     * Default: Body^@
     *
     * @param legacyWhitespace whether to append an extra two newlines
     * @see [The STOMP spec](http://stomp.github.io/stomp-specification-1.2.html.STOMP_Frames)
     */
    fun setLegacyWhitespace(legacyWhitespace: Boolean) {
        this.legacyWhitespace = legacyWhitespace
    }

    /** returns the to topic (subscription id) corresponding to a given destination
     * @param dest the destination
     * @return the topic (subscription id) or null if no topic corresponds to the destination
     */
    fun getTopicId(dest: String): String? {
        return topics!![dest]
    }

    companion object {
        private const val TAG = "SocialStompClient"
    }
}