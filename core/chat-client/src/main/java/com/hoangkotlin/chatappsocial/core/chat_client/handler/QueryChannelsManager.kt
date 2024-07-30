package com.hoangkotlin.chatappsocial.core.chat_client.handler

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest

interface QueryChannelsManager {
    fun onQueryChannelsRequest(request: QueryManyChannelRequest)

    fun onQueryChannelsResult(result: DataResult<List<SocialChatChannel>>)
}