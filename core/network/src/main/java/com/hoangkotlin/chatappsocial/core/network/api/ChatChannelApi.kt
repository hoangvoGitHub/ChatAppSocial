package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.response.ChatChannelResponse
import com.hoangkotlin.chatappsocial.core.network.model.request.CreateChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.MarkReadRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.MuteChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatChannelApi {

    @POST("/api/v1/chat/channels/all")
    suspend fun queryChannels(
        @Body request: QueryManyChannelRequest
    ): Response<List<ChatChannelResponse>>

    @POST("/api/v1/chat/channels/{channelId}")
    suspend fun queryChannel(
        @Path("channelId") channelId: String,
        @Body request: QueryChatChannelRequest
    ): Response<ChatChannelResponse>

    @DELETE("/api/v1/chat/channels/{channelId}")
    suspend fun deleteChannel(
        @Path("channelId") channelId: String
    ): Response<ChatChannelResponse>

    @POST("/api/v1/chat/channels/create")
    suspend fun createChannel(
        @Body request: CreateChatChannelRequest
    ): Response<ChatChannelResponse>

    @POST("/api/v1/chat/channels/{channelId}/read")
    suspend fun markRead(
        @Path("channelId") channelId: String,
        @Body request: MarkReadRequest
    ): Response<BaseResponse<String>>

    @POST("/api/v1/chat/channels/{channelId}/mute")
    suspend fun muteChannel(
        @Path("channelId") channelId: String,
        @Body request: MarkReadRequest
    ): Response<BaseResponse<String>>

    @POST("/api/v1/chat/channels/{channelId}/unmute")
    suspend fun unmuteChannel(
        @Path("channelId") channelId: String,
        @Body request: MuteChannelRequest
    ): Response<BaseResponse<String>>

    @DELETE("/api/v1/chat/channels/{channelId}/conversation")
    suspend fun deleteConversationHistory(
        @Path("channelId") channelId: String,
    ): Response<BaseResponse<String>>


}