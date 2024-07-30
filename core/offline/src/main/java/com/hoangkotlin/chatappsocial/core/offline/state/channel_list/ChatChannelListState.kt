package com.hoangkotlin.chatappsocial.core.offline.state.channel_list

import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel

/**
 * Represents the Channels screen state, used to render the required UI.
 */
data class ChatChannelListState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isEndOfChannels: Boolean = false,
    val channelItems: List<SocialChatChannel> = emptyList(),
    val searchQuery: String = ""
)
