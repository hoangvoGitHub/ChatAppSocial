package com.hoangkotlin.chatappsocial.core.offline.state.channel_list

import com.hoangkotlin.chatappsocial.core.chat_client.handler.ChatEventHandler
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import kotlinx.coroutines.flow.StateFlow

interface QueryChannelListState {

    val currentQueryRequest: StateFlow<QueryManyChannelRequest?>

    val nextQueryRequest: StateFlow<QueryManyChannelRequest?>

    val isLoading: StateFlow<Boolean>

    val isLoadingMore: StateFlow<Boolean>

    val isEndOfChannels: StateFlow<Boolean>

    val channels: StateFlow<List<SocialChatChannel>>

    val channelsStateData: StateFlow<ListStateData<SocialChatChannel>>

    val eventHandler: ChatEventHandler

    fun clearState()
}

