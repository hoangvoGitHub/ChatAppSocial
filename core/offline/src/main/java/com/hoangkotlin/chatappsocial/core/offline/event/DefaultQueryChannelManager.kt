package com.hoangkotlin.chatappsocial.core.offline.event

import com.hoangkotlin.chatappsocial.core.chat_client.handler.QueryChannelManager
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder

class DefaultQueryChannelManager(
    private val stateHolder: ChatStateHolder
) : QueryChannelManager {
    override fun onQueryChannelRequest(channelId: String, request: QueryChatChannelRequest) {
    }

    override fun onQueryChannelResult(
        request: QueryChatChannelRequest,
        result: DataResult<SocialChatChannel>
    ) {
        if (result is DataResult.Success) {
            stateHolder.updateChannelDataWithRequest(
                request = request,
                channelData = result.data
            )
        }
    }
}