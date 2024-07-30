package com.hoangkotlin.chatappsocial.core.offline

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryMessagesDirection
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import com.hoangkotlin.chatappsocial.core.offline.state.channel_list.QueryChannelListState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatChannelState
import javax.inject.Inject

class StateRegistry @Inject constructor(
    private val stateHolder: ChatStateHolder,
) {
    fun mutableChannel(channelId: String): ChatChannelState {
        return stateHolder.mutableChannel(channelId)
    }

    fun queryChannelsState(): QueryChannelListState {
        return stateHolder.queryChannelsState()
    }

    fun clearState(){
        stateHolder.clearState()
    }

    suspend fun loadOlderMessage(
        cid: String, messageLimit: Int,
        queryChannelCall: suspend (channelId: String, request: QueryChatChannelRequest) -> DataResult<SocialChatChannel>
    ): DataResult<SocialChatChannel> {
        val baseMessageId = stateHolder
            .mutableChannel(cid)
            .sortedVisibleMessages.value
            .takeUnless(Collection<SocialChatMessage>::isEmpty)?.first()
            ?.id ?: ""

        val request = QueryChatChannelRequest(
            direction = QueryMessagesDirection.OLDER_THAN,
            messageLimit = messageLimit,
            baseMessageId = baseMessageId
        )
        return queryChannelCall(cid, request)

    }

}