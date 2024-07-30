package com.hoangkotlin.chatappsocial.core.chat_client

import android.util.Log
import com.hoangkotlin.chatappsocial.core.chat_client.api.ChatApi
import com.hoangkotlin.chatappsocial.core.chat_client.client.SocialClientState
import com.hoangkotlin.chatappsocial.core.chat_client.downloader.FileDownloader
import com.hoangkotlin.chatappsocial.core.chat_client.extension.asUpChatMessageDto
import com.hoangkotlin.chatappsocial.core.chat_client.handler.ChatEventHandler
import com.hoangkotlin.chatappsocial.core.chat_client.handler.ClearConversationManager
import com.hoangkotlin.chatappsocial.core.chat_client.handler.QueryChannelManager
import com.hoangkotlin.chatappsocial.core.chat_client.handler.QueryChannelsManager
import com.hoangkotlin.chatappsocial.core.chat_client.handler.SentEventHandler
import com.hoangkotlin.chatappsocial.core.chat_client.interceptor.Interceptor
import com.hoangkotlin.chatappsocial.core.chat_client.interceptor.SendMessageInterceptor
import com.hoangkotlin.chatappsocial.core.chat_client.socket.SocketListener
import com.hoangkotlin.chatappsocial.core.chat_client.socket.SocketManager
import com.hoangkotlin.chatappsocial.core.chat_client.uploader.FileUploader
import com.hoangkotlin.chatappsocial.core.chat_client.utils.ConnectionData
import com.hoangkotlin.chatappsocial.core.chat_client.utils.DisconnectCause
import com.hoangkotlin.chatappsocial.core.common.di.DispatchersModule
import com.hoangkotlin.chatappsocial.core.common.di.IOScope
import com.hoangkotlin.chatappsocial.core.common.model.ConnectionState
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.data.model.Result
import com.hoangkotlin.chatappsocial.core.data.model.onSuccess
import com.hoangkotlin.chatappsocial.core.data.repository.RepositoryFacade
import com.hoangkotlin.chatappsocial.core.datastore.SocialPreferencesDataSource
import com.hoangkotlin.chatappsocial.core.model.ChatAppUser
import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatFriend
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadedImage
import com.hoangkotlin.chatappsocial.core.model.events.ChatEvent
import com.hoangkotlin.chatappsocial.core.network.model.dto.UpMessageReadEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.UpNewMessageEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.UpTypingStartEventDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.UpTypingStopEventDto
import com.hoangkotlin.chatappsocial.core.network.model.request.PageableRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResource
import com.hoangkotlin.chatappsocial.core.network.model.request.SendChatMessageRequest
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import com.hoangkotlin.chatappsocial.core.notifications.SocialChatNotifications
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import java.io.File
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.cancellation.CancellationException

private const val TAG = "ChatClient"

@Singleton
class ChatClient @Inject constructor(
    private val api: ChatApi,
    private val localDataSource: SocialPreferencesDataSource,
    val clientState: SocialClientState,
    private val repository: RepositoryFacade,
    @IOScope private val userScope: CoroutineScope,
    private val fileUploader: FileUploader,
    private val fileDownloader: FileDownloader,
    private val sentEventHandler: SentEventHandler,
    private val socketManager: SocketManager,
    private val notifications: SocialChatNotifications
) {

    private val interceptors = mutableSetOf<Interceptor>()
    private val eventHandlers = mutableSetOf<ChatEventHandler>()
    private val queryChannelsManagers = mutableSetOf<QueryChannelsManager>()
    private val queryChannelManagers = mutableSetOf<QueryChannelManager>()
    private val clearConversationMangers = mutableSetOf<ClearConversationManager>()

    suspend fun queryChatUser(userId: String): DataResult<SocialChatUser> {
        return api.queryChatUser(userId)
    }

    private suspend fun queryChatUser(): DataResult<SocialChatUser> {
        return api.queryChatUser()
    }


    suspend fun queryChannel(channelId: String, messageLimit: Int) = queryChannel(
        channelId,
        QueryChatChannelRequest(messageLimit = messageLimit)
    )

    suspend fun queryChannel(
        channelId: String,
        request: QueryChatChannelRequest = QueryChatChannelRequest()
    ): DataResult<SocialChatChannel> {
        callQueryChannelHandlers { it.onQueryChannelRequest(channelId, request) }
        return api.queryChannel(channelId, request).also { result ->
            callQueryChannelHandlers { it.onQueryChannelResult(request, result) }
        }
    }

    suspend fun clearConversationHistory(
        channelId: String
    ): DataResult<Unit> {
        callCleanConversationHandlers { it.onClearConversationStart(channelId) }
        val result = api.cleanConversationHistory(channelId)
        callCleanConversationHandlers { it.onClearConversationResult(channelId, result) }
        return result
    }

    suspend fun sendImage(
        channelId: String,
        uploadId: String,
        file: File,
        callback: ProgressCallback?
    ): DataResult<UploadedImage> {
        return fileUploader.sendImage(channelId, uploadId, file, callback)
    }

    suspend fun uploadProfileImage(file: File, progressCallback: ProgressCallback) {
        fileUploader.uploadProfileImage(file, progressCallback)
    }

    suspend fun queryChannels(request: QueryManyChannelRequest): DataResult<List<SocialChatChannel>> {
        callQueryChannelsHandlers { it.onQueryChannelsRequest(request) }
        return api.queryChannels(request).also { result ->
            callQueryChannelsHandlers { it.onQueryChannelsResult(result) }
        }
    }

    suspend fun markRead(channelId: String, messageId: String): Unit {
        sentEventHandler.sentEvent(
            channelId, buildUpReadEvent(
                channelId,
                messageId
            )
        )
//        api.markRead(channelId, messageId).also {
//            Log.d(TAG, "markRead: for $messageId result $it")
//        }
    }

    suspend fun downloadAttachment(attachment: SocialChatAttachment): Result<Unit> {
        return fileDownloader.downloadFile(attachment)
    }


    suspend fun queryChatUserByUsername(username: String) = api.queryChatUserByUsername(username)

    suspend fun queryFriends(
        query: String = "",
        limit: Int = 20,
        offset: Int = 0,
        status: FriendStatus,
        sortBy: String? = null,
    ): DataResult<List<SocialChatFriend>> = api.queryFriend(status = status.name)


    suspend fun acceptFriend(friendUserId: String): DataResult<SocialChatFriend> =
        api.acceptFriend(friendUserId = friendUserId)


    suspend fun rejectFriend(friendUserId: String): DataResult<SocialChatFriend?> =
        api.rejectFriend(friendUserId = friendUserId)

    suspend fun removeFriend(friendUserId: String): DataResult<SocialChatFriend> =
        api.acceptFriend(friendUserId = friendUserId)

    suspend fun addFriend(userId: String): DataResult<SocialChatFriend> =
        api.addFriend(friendUserId = userId)

    fun search(
        searchQuery: String, offset: Int, limit: Int = 20
    ) = repository.search(searchQuery, offset, limit)

    fun search(
        searchQuery: String,
        resource: Map<SearchResource, PageableRequest> = emptyMap(),
    ) = repository.search(searchQuery, resource)

    suspend fun createChannel(
        name: String? = null,
        message: String? = null,
        memberIds: List<String>,
        type: String,
    ): DataResult<SocialChatChannel> {
        return api.createChannel(name, message, memberIds, type)
    }

    fun subscribeChannel(
        channelId: String,
        eventHandler: ChatEventHandler
    ) {
        socketManager.subscribeTopic(channelId, eventHandler)
    }

    fun unsubscribeChannel(
        channelId: String,
        eventHandler: ChatEventHandler
    ) {
        socketManager.unsubscribeTopic(
            channelId,
            eventHandler
        )
    }

    suspend fun sendMessage(channelId: String, message: SocialChatMessage) {
        val request = SendChatMessageRequest(
            message = message.asUpChatMessageDto()
        )

        val validInterceptors = interceptors.filterIsInstance<SendMessageInterceptor>()

        validInterceptors.fold(
            Result.Success(message),
            operation = { _: Result<SocialChatMessage>, interceptor ->
                interceptor.interceptMessage(channelId, message)
            }).onSuccess {
//            api.sendMessage(channelId, request)
//                .onSuccessSuspend {
//                    Log.d(TAG, "sendMessage: success send ${it.text}")
//                }
//                .onErrorsSuspend { errorMessage, errorCode ->
//                    Log.d(TAG, "sendMessage: Error send ${errorMessage}")
//                }

            sentEventHandler.sentEvent(
                channelId,
                buildUpMessageEvent(channelId, message)
            )
        }

    }


    val connectionState = Channel<ConnectionState>(Channel.CONFLATED)

    // listener for global state
    private val socketListener: SocketListener = object : SocketListener {
        override fun onConnected(connectionData: ConnectionData?) {
            Log.d(TAG, "onConnected: ")
            connectionState.trySend(ConnectionState.CONNECTED)
            clientState.setConnectionState(ConnectionState.CONNECTED)
        }

        override fun onConnecting() {
            Log.d(TAG, "onConnecting: ")
            connectionState.trySend(ConnectionState.CONNECTING)
            clientState.setConnectionState(ConnectionState.CONNECTING)
        }

        override fun onDisconnected(cause: DisconnectCause) {
            Log.d(TAG, "onDisconnected: ")
            connectionState.trySend(ConnectionState.OFFLINE)
            clientState.setConnectionState(ConnectionState.OFFLINE)
        }

        override fun onError(error: String) {
            Log.d(TAG, "onError: ")
            connectionState.trySend(ConnectionState.OFFLINE)
            clientState.setConnectionState(ConnectionState.OFFLINE)
        }

        override fun onEvent(event: ChatEvent) {
            callEventHandler { it.onChatEvent(event) }
        }
    }

    fun showNotification(channel: SocialChatChannel, message: SocialChatMessage) {
        if (shouldShowNotification(message.user.id)) {
            notifications.displayNotification(
                channel = channel,
                message = message
            )
        }
    }


    fun addSocketListener(listener: SocketListener) {
        socketManager.addSocketListener(listener)
    }

    fun removeSocketListener(listener: SocketListener) {
        socketManager.removeSocketListener(listener)
    }

    private var setUpUserJob: Job? = null

    fun setUpUser(user: ChatAppUser) {
        socketManager.removeSocketListener(socketListener)
        setUpUserJob?.cancel()

        socketManager.addSocketListener(socketListener)
        setUpUserJob = userScope.launch(DispatchersModule.providesIODispatcher()) {
            val queryCachedUserJob = async {
                user.chatUserId?.let { userId ->
                    repository.selectChatUser(userId)?.let {
                        clientState.setUser(it)
                    }
                    clientState.setConnectionState(ConnectionState.OFFLINE)
                }
            }

            val queryNetworkUserJob = async {
                when (val result = queryChatUser()) {
                    is DataResult.Error -> {}
                    is DataResult.Success -> {
                        // Cancel the queryCachedUser job if the queryNetworkUser job completes successfully
                        queryCachedUserJob.cancel()
                        localDataSource.updateUserData(
                            result.data.image,
                            result.data.id
                        )
                        clientState.setUser(result.data)
                        repository.insert(result.data)
                        socketManager.connect(result.data)
                    }
                }
            }

            try {
                // Await both jobs
                queryCachedUserJob.await()
                queryNetworkUserJob.await()
            } catch (e: CancellationException) {
                // Handle the cancellation exception if needed
                e.printStackTrace()
            }


            // second job

        }
    }


    fun addQueryChannelsHandler(handler: QueryChannelsManager) {
        queryChannelsManagers.add(handler)
    }

    fun addQueryChannelManager(handler: QueryChannelManager) {
        queryChannelManagers.add(handler)
    }

    fun addClearConversationManager(manager: ClearConversationManager) {
        clearConversationMangers.add(manager)
    }

    fun removeClearConversationManager(manager: ClearConversationManager) {
        clearConversationMangers.remove(manager)
    }


    fun removeQueryChannelsHandler(handler: QueryChannelsManager) {
        queryChannelsManagers.remove(handler)
    }

    fun removeQueryChannelHandler(handler: QueryChannelManager) {
        queryChannelManagers.remove(handler)
    }

    fun addEventHandler(eventHandler: ChatEventHandler) {
        synchronized(eventHandlers) {
            eventHandlers.add(eventHandler)
        }
    }

    fun removeEventHandler(eventHandler: ChatEventHandler) {
        synchronized(eventHandlers) {
            eventHandlers.remove(eventHandler)
        }
    }

    fun addInterceptor(interceptor: Interceptor) {
        interceptors.add(interceptor)
    }

    fun removeInterceptor(interceptor: Interceptor) {
        interceptors.remove(interceptor)
    }

    private fun callEventHandler(call: (ChatEventHandler) -> Unit) {
        synchronized(eventHandlers) {
            eventHandlers.forEach(call)
        }
    }

    private fun callQueryChannelsHandlers(call: (QueryChannelsManager) -> Unit) {
        queryChannelsManagers.forEach(call)
    }


    private fun callQueryChannelHandlers(call: (QueryChannelManager) -> Unit) {
        queryChannelManagers.forEach(call)
    }

    private fun callCleanConversationHandlers(call: (ClearConversationManager) -> Unit) {
        clearConversationMangers.forEach(call)
    }


    fun releaseConnection() {
        clientState.clearState()
        socketManager.removeSocketListener(socketListener)
        socketManager.releaseConnection()
        eventHandlers.clear()
        queryChannelManagers.clear()
        queryChannelsManagers.clear()
        interceptors.clear()
        clearConversationMangers.clear()
    }

    fun launch(function: suspend CoroutineScope.() -> Unit): Job {
        return userScope.launch(block = function)
    }

    private fun buildUpMessageEvent(
        channelId: String,
        message: SocialChatMessage
    ): UpNewMessageEventDto {
        return UpNewMessageEventDto(
            createdAt = Date(),
            cid = channelId,
            message = message.asUpChatMessageDto()
        )
    }

    private fun buildUpReadEvent(
        channelId: String,
        messageId: String
    ): UpMessageReadEventDto {
        return UpMessageReadEventDto(
            createdAt = Date(),
            cid = channelId,
            messageId = messageId
        )
    }

    private fun buildUpTypingStartEvent(
        channelId: String,
    ): UpTypingStartEventDto {
        return UpTypingStartEventDto(
            createdAt = Date(),
            cid = channelId,
        )
    }

    private fun buildUpTypingStopEvent(
        channelId: String,
    ): UpTypingStopEventDto {
        return UpTypingStopEventDto(
            createdAt = Date(),
            cid = channelId,
        )
    }

    private fun shouldShowNotification(messageSenderId: String): Boolean {
        return clientState.user.value?.id != messageSenderId
    }

    companion object {
        private const val TAG = "ChatClient"
    }
}



