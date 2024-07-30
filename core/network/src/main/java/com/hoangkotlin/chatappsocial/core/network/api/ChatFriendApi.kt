package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.request.ChatFriendRequest
import com.hoangkotlin.chatappsocial.core.network.model.response.BaseResponse
import com.hoangkotlin.chatappsocial.core.network.model.response.ChatFriendDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ChatFriendApi {

    @POST("/api/v1/chat/friend/add")
    suspend fun addFriend(
        @Body request: ChatFriendRequest
    ): Response<BaseResponse<ChatFriendDto>>

    @POST("/api/v1/chat/friend/accept")
    suspend fun acceptFriend(
        @Body request: ChatFriendRequest
    ): Response<BaseResponse<ChatFriendDto>>


    @POST("/api/v1/chat/friend/remove")
    suspend fun removeFriend(
        @Body request: ChatFriendRequest
    ): Response<BaseResponse<ChatFriendDto>>


    @POST("/api/v1/chat/friend/reject")
    suspend fun rejectFriend(
        @Body request: ChatFriendRequest
    ): Response<BaseResponse<ChatFriendDto>>

    @GET("/api/v1/chat/friend")
    suspend fun queryFriends(
        @Query("name") name: String = "",
        @Query("limit") limit: Int = 20,
        @Query("offset") offset: Int = 0,
        @Query("status") status: String,
        @Query("sortBy") sortBy: String? = null,
    ): Response<BaseResponse<List<ChatFriendDto>>>
}