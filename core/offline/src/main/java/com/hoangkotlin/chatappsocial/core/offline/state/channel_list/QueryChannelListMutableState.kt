package com.hoangkotlin.chatappsocial.core.offline.state.channel_list

import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.chat_client.handler.ChatEventHandler
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.offline.event.ChatChannelListEventHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update


// TODO: Need to provide a initial channel listState for this state
private const val TAG = "QueryChannelListMutable"

class QueryChannelListMutableState(
    private val userScope: CoroutineScope,
    chatClient: ChatClient,
) : QueryChannelListState {


    private val _channels = MutableStateFlow<Map<String, SocialChatChannel>>(emptyMap())

    private val _eventHandler = ChatChannelListEventHandler(_channels, chatClient)

    private val _isLoading = MutableStateFlow(false)
    private val _isLoadingMore = MutableStateFlow(false)
    private val _currentRequest = MutableStateFlow<QueryManyChannelRequest?>(null)
    private val _nextRequest = MutableStateFlow<QueryManyChannelRequest?>(null)

    val currentLoading: MutableStateFlow<Boolean>
        get() = if (_channels.value.isEmpty()) _isLoading else _isLoadingMore

    private val _isEndOfChannels = MutableStateFlow(false)

    private val _channelsOffset: MutableStateFlow<Int> = MutableStateFlow(0)

    private val _sortedChannels: StateFlow<List<SocialChatChannel>> =
        _channels.map { channelMap ->
            channelMap.values.sortedByDescending {
                it.lastMessage?.createdAt ?: it.updatedAt ?: it.createdAt
            }
        }
            .stateIn(userScope, SharingStarted.Eagerly, emptyList())


    override val currentQueryRequest: StateFlow<QueryManyChannelRequest?>
        get() = _currentRequest

    override val nextQueryRequest: StateFlow<QueryManyChannelRequest?>
        get() = currentQueryRequest.combine(_channelsOffset) { currentRequest, currentOffset ->
            currentRequest?.copy(offset = currentOffset)
        }.stateIn(userScope, SharingStarted.Eagerly, null)

    override val isLoading: StateFlow<Boolean>
        get() = _isLoading

    override val isLoadingMore: StateFlow<Boolean>
        get() = _isLoadingMore

    override val isEndOfChannels: StateFlow<Boolean>
        get() = _isEndOfChannels

    override val channels: StateFlow<List<SocialChatChannel>>
        get() = _sortedChannels

    override val channelsStateData: StateFlow<ListStateData<SocialChatChannel>>
        get() = _isLoading.combine(_sortedChannels) { loading, channels ->
            when {
                loading -> ListStateData.Loading
                channels.isEmpty() -> ListStateData.OfflineNoData
                else -> ListStateData.Result(channels)
            }
        }
            .stateIn(userScope, SharingStarted.Eagerly, ListStateData.NoQueryActive)

    override val eventHandler: ChatEventHandler
        get() = _eventHandler

    fun upsertChannels(channelList: List<SocialChatChannel>) {
        _channels.value += channelList.associateBy(SocialChatChannel::id)
    }

    fun upsertChannel(channel: SocialChatChannel) {
        upsertChannels(listOf(channel))
    }

    fun setCurrentRequest(queryManyChannelRequest: QueryManyChannelRequest) {
        _currentRequest.value = queryManyChannelRequest
    }

    fun setChannelsOffset(offset: Int) {
        _channelsOffset.value = offset
    }

    fun setLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    fun setIsEndOfChannels(isEnd: Boolean) {
        _isEndOfChannels.value = isEnd
    }

    fun clearConversationHistory(
        channelId: String,
    ): SocialChatChannel? {
        var channelToClear: SocialChatChannel? = null
        _channels.update { map ->
            map.toMutableMap().apply {
                channelToClear = remove(channelId)
            }
        }
        return channelToClear
    }

    override fun clearState() {
        _channels.value = emptyMap()
        _isLoading.value = false
        _isLoadingMore.value = false
        _currentRequest.value = null
        _isEndOfChannels.value = false
        _channelsOffset.value = 0
    }


}