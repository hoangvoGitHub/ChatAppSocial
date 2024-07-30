package com.hoangkotlin.chatappsocial.core.network.api

import com.hoangkotlin.chatappsocial.core.network.model.dto.SearchResultDto
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResourceRequest
import com.hoangkotlin.chatappsocial.core.network.model.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface SearchApi {

    @GET("/api/v1/chat/search")
    suspend fun search(
        @Query("query") query: String,
        @Query("offset") offset: Int,
        @Query("limit") limit: Int,
    ): Response<BaseResponse<SearchResultDto>>


    @POST("/api/v1/chat/search")
    suspend fun search(
        @Body request: SearchResourceRequest
    ): Response<BaseResponse<SearchResultDto>>
    //*
// top ->
//
// *//*
}
