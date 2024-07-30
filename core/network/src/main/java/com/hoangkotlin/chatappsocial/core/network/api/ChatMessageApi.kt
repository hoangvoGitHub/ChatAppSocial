package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.request.SendChatMessageRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.UpdateChatMessageRequest
import com.hoangkotlin.chatappsocial.core.network.model.response.ChatMessageResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ChatMessageApi {

    @POST("/api/v1/chat/channels/{channelId}/messages")
    suspend fun sendMessage(
        @Path("channelId") channelId: String,
        @Body message: SendChatMessageRequest
    ):Response<ChatMessageResponse>


     @GET("/api/v1/chat/channels/{channelId}/messages")
    suspend fun queryMessages(
        @Path("channelId") channelId: String,
        @Body message: SendChatMessageRequest
    ):Response<ChatMessageResponse>


    @GET("/api/v1/chat/messages/{id}")
    suspend fun queryMessage(
        @Path("id") messageId: String
    ):Response<ChatMessageResponse>

    @POST("/api/v1/chat/messages/{id}")
    suspend  fun updateMessage(
        @Path("id") messageId: String,
        @Body message: UpdateChatMessageRequest,
    ): Response<ChatMessageResponse>
}