package com.hoangkotlin.chatappsocial.core.offline.event

import com.hoangkotlin.chatappsocial.core.chat_client.handler.QueryChannelsManager
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import com.hoangkotlin.chatappsocial.core.offline.state.asMutableState

class DefaultQueryChannelsManager(
    private val stateHolder: ChatStateHolder
) : QueryChannelsManager {
    override fun onQueryChannelsRequest(request: QueryManyChannelRequest) {
        stateHolder.queryChannelsState().asMutableState().apply {
            setLoading(true)
            setCurrentRequest(request)
        }
    }

    override fun onQueryChannelsResult(result: DataResult<List<SocialChatChannel>>) {

        stateHolder.queryChannelsState().asMutableState().apply {
            setLoading(false)
            if (result is DataResult.Success) {
                stateHolder.setQueryChannels(result.data)
                upsertChannels(result.data)
            }
        }

    }
}