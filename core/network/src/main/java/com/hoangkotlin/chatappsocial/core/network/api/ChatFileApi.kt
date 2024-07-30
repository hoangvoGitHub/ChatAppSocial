package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.response.FileUploadResponse
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Tag

interface ChatFileApi {

    @Multipart
    @POST("/api/v1/chat/channels/{id}/image/{uploadId}")
    suspend fun sendImage(
        @Path("id") channelId: String,
        @Path("uploadId") uploadId: String,
        @Part file: MultipartBody.Part,
        @Tag progressCallback: ProgressCallback
    ): Response<FileUploadResponse>

    @Multipart
    @POST("/api/v1/chat/channels/{channelId}/image/{uploadId}")
    suspend fun sendImage(
        @Path("channelId") channelId: String,
        @Path("uploadId") uploadId: String,
        @Part file: MultipartBody.Part,
    ): Response<FileUploadResponse>

    @Multipart
    @POST("/api/v1/chat/users/profile/image")
    suspend fun uploadProfileImage(
        @Part file: MultipartBody.Part,
        @Tag progressCallback: ProgressCallback
    ): Response<FileUploadResponse>
}