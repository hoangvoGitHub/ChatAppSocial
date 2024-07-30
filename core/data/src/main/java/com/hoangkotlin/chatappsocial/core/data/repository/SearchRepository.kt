package com.hoangkotlin.chatappsocial.core.data.repository

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SearchResult
import com.hoangkotlin.chatappsocial.core.network.model.request.PageableRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResource
import kotlinx.coroutines.flow.Flow

interface SearchRepository {
    fun search(
        searchQuery: String,
        offset: Int,
        limit: Int = 20
    ): Flow<DataResult<SearchResult>>

    fun search(
        searchQuery: String,
        resource: Map<SearchResource, PageableRequest> = emptyMap(),
    ): Flow<DataResult<SearchResult>>
}