package com.hoangkotlin.chatappsocial.core.websocket

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatUsersRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SendChatMessageRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.UpdateChatMessageRequest

interface ChatApi {

   suspend fun sendMessage(
        channelId: String,
        request: SendChatMessageRequest
    ): DataResult<SocialChatMessage>

   suspend fun getMessage(messageId: String): DataResult<SocialChatMessage>

   suspend fun updateMessage(
        messageId: String,
        request: UpdateChatMessageRequest
    ): DataResult<SocialChatMessage>

   suspend fun queryChannels(
        queryRequest: QueryManyChannelRequest
    ): DataResult<List<SocialChatChannel>>

   suspend fun queryChannel(
        channelId: String,
        queryRequest: QueryChatChannelRequest
    ): DataResult<SocialChatChannel>

   suspend fun deleteChannel(
        channelId: String
    ): DataResult<SocialChatChannel>

   suspend fun queryChatUsers(
        request: QueryChatUsersRequest
    ): DataResult<List<SocialChatUser>>

}