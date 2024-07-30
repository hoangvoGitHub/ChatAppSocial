package com.hoangkotlin.feature.channel_detail.model

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.data.model.onError
import com.hoangkotlin.chatappsocial.core.data.model.onSuccess
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.offline.StateRegistry
import com.hoangkotlin.chatappsocial.core.offline.extension.watchChannelAsState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatChannelState
import com.hoangkotlin.feature.channel_detail.navigation.channelArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = "ChannelDetailViewModel"

@HiltViewModel
class ChannelDetailViewModel @Inject constructor(
    private val chatClient: ChatClient,
    private val stateRegistry: StateRegistry,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val channelId: StateFlow<String?> = savedStateHandle.getStateFlow(channelArg, null)

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    val currentUser = chatClient.clientState.user

    val currentChannel: StateFlow<ChatChannelState?> =
        channelId.filterNotNull().flatMapLatest { cid ->
            chatClient.watchChannelAsState(cid, 0, viewModelScope, stateRegistry)
        }.stateIn(viewModelScope, SharingStarted.Eagerly, null)

    private val _channelDetailState =
        MutableStateFlow<ChannelDetailUiState>(ChannelDetailUiState.Loading)
    val channelDetailState = _channelDetailState.asStateFlow()

    init {
        observeChannelState()
    }

    @OptIn(FlowPreview::class)
    private fun observeChannelState() {
        viewModelScope.launch {
            currentChannel.filterNotNull().timeout(20000L.milliseconds).catch {
                _channelDetailState.value = ChannelDetailUiState.LoadFailed
            }.collect {
                combine(
                    it.channelData,
                    it.messages
                ) { channelData, messages ->
                    _channelDetailState.value = ChannelDetailUiState.Success(
                        channelData.copy(
                            messages = messages
                        )
                    )
                }.collect()
            }
        }
    }

    fun clearConversationHistory() {
        if (channelId.value != null) {
            viewModelScope.launch {
                chatClient.clearConversationHistory(channelId.value!!)
                    .onSuccess {
                        showToast("Conversation deleted")
                    }.onError { _, _ ->
                        showToast("Delete conversation failed")
                    }
            }
        }

    }

    fun clearConversationHistory(channel: SocialChatChannel) {
        viewModelScope.launch {
            chatClient.clearConversationHistory(channel.id)
                .onSuccess {
                    showToast("Conversation deleted")
                }.onError { _, _ ->
                    showToast("Delete conversation failed")
                }
        }
    }

    fun showToast(message: String) {
        viewModelScope.launch {
            _toastMessage.emit(message)
        }
    }


}

sealed interface ChannelDetailUiState {
    data object Loading : ChannelDetailUiState

    data object LoadFailed : ChannelDetailUiState

    data class Success(val channel: SocialChatChannel) :
        ChannelDetailUiState
}