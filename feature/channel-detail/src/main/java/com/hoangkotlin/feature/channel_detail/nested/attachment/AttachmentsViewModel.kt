package com.hoangkotlin.feature.channel_detail.nested.attachment

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.offline.StateRegistry
import com.hoangkotlin.chatappsocial.core.offline.extension.watchMedias
import com.hoangkotlin.chatappsocial.core.offline.extension.watchOtherAttachments
import com.hoangkotlin.feature.channel_detail.navigation.channelArg
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AttachmentsViewModel @Inject constructor(
    private val chatClient: ChatClient,
    private val stateRegistry: StateRegistry,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

     val channelId: StateFlow<String?> = savedStateHandle.getStateFlow(channelArg, null)

    private val _selectedFilter = MutableStateFlow(AttachmentsFilter.Media)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val _attachmentsState = MutableStateFlow(AttachmentsState())
    val attachmentsState = _attachmentsState.asStateFlow()

    init {
        observeFilter()
    }

    private fun observeFilter() {
        viewModelScope.launch {
            combine(_selectedFilter, channelId) { filter, channelId ->
                if (channelId == null) {
                    _attachmentsState.value = _attachmentsState.value.copy(
                        isLoading = true,
                        attachments = emptyList()
                    )
                }
                when (filter) {
                    AttachmentsFilter.Media -> chatClient.watchMedias(
                        channelId!!, stateRegistry, viewModelScope
                    )

                    AttachmentsFilter.File -> chatClient.watchOtherAttachments(
                        channelId!!, stateRegistry, viewModelScope
                    )

                    else -> flowOf(emptyList())
                }.collectLatest {
                    _attachmentsState.value = _attachmentsState.value.copy(
                        isLoading = false,
                        attachments = it
                    )
                }
            }.collect()
        }
    }


    fun onFilterSelected(attachmentsFilter: AttachmentsFilter) {
        _selectedFilter.value = attachmentsFilter
    }
}

data class AttachmentsState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val endOfAttachments: Boolean = false,
    val attachments: List<SocialChatAttachment> = emptyList(),
)