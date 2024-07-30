package com.hoangkotlin.chatappsocial.core.chat_client.handler

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest

interface QueryChannelManager {

    fun onQueryChannelRequest(channelId: String, request: QueryChatChannelRequest)

    fun onQueryChannelResult(
        request: QueryChatChannelRequest,
        result: DataResult<SocialChatChannel>
    )
}