package com.hoangkotlin.chatappsocial.feature.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.data.model.onError
import com.hoangkotlin.chatappsocial.core.data.model.onSuccess
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.offline.StateRegistry
import com.hoangkotlin.chatappsocial.core.offline.extension.queryChannelsAsState
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.ListStateData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeScreenViewModel"

@HiltViewModel
class HomeScreenViewModel @Inject constructor(
    private val chatClient: ChatClient,
    private val stateRegistry: StateRegistry
) : ViewModel() {

    private var _queryChannelsState: StateFlow<com.hoangkotlin.chatappsocial.core.offline.state.channel_list.QueryChannelListState?> =
        MutableStateFlow(null)

    val currentUser: StateFlow<SocialChatUser?> =
        chatClient.clientState.user

    private val _channelsState =
        MutableStateFlow(com.hoangkotlin.chatappsocial.core.offline.state.channel_list.ChatChannelListState())
    val channelsState = _channelsState.asStateFlow()

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private fun startObserve() {
        viewModelScope.launch {
            _queryChannelsState.filterNotNull().collect {
                it.channelsStateData.map { state ->
                    when (state) {
                        ListStateData.OfflineNoData,
                        ListStateData.Loading,
                        -> _channelsState.value.copy(
                            isLoading = true
                        )

                        ListStateData.NoQueryActive ->
                            _channelsState.value.copy(
                                isLoading = false,
                                channelItems = emptyList(),
                            )

                        is ListStateData.Result -> _channelsState.value.copy(
                            isLoading = false,
                            channelItems = state.items,
                            isLoadingMore = false,
                        )
                    }
                }.collectLatest { newState ->
                    _channelsState.value = newState
                }
            }
        }

    }

    init {
        init()
    }

    private fun init() {
        viewModelScope.launch {
            _queryChannelsState =
                chatClient.queryChannelsAsState(
                    QueryManyChannelRequest(),
                    viewModelScope,
                    stateRegistry
                )
            startObserve()
        }

    }

    fun toggleNotification(channel: SocialChatChannel) {
        // TODO: add toggle notification feature 
    }

    fun clearChannelConversation(channel: SocialChatChannel) {
        viewModelScope.launch {
            chatClient.clearConversationHistory(channel.id)
                .onSuccess {
                    showToast("Conversation deleted")
                }.onError { _, _ ->
                    showToast("Delete conversation failed")
                }
        }
    }

    fun loadMore() {
        viewModelScope.launch {
            // TODO: add load more function 
            if (_channelsState.value.isLoadingMore || _channelsState.value.isEndOfChannels) return@launch
            _channelsState.update { currentState ->
                currentState.copy(isLoadingMore = true)
            }
            delay(2_000L)
            _channelsState.update { currentState ->
                currentState.copy(isLoadingMore = false)
            }
        }
    }

    private fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }

}


