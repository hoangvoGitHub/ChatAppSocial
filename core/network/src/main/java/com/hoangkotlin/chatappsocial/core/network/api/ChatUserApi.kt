package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatUserDto
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatUsersRequest
import com.hoangkotlin.chatappsocial.core.network.model.response.FileUploadResponse
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.Tag

interface ChatUserApi {

    @GET("/api/v1/chat/users")
    suspend fun queryChatUsers(
        @Query("payload") payload: QueryChatUsersRequest,
    ): Response<List<DownChatUserDto>>

    @GET("/api/v1/chat/users/{id}")
    suspend fun queryChatUser(
        @Path("id") id: String,
    ): Response<DownChatUserDto>


    @GET("/api/v1/chat/users/username")
    suspend fun queryChatUserByUsername(
        @Query("username") username: String,
    ): Response<DownChatUserDto>

    @GET("/api/v1/chat/users/profile")
    suspend fun queryChatUser(): Response<DownChatUserDto>



}