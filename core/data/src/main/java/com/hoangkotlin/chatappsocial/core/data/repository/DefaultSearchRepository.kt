package com.hoangkotlin.chatappsocial.core.data.repository

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.search.SearchGroup
import com.hoangkotlin.chatappsocial.core.model.SearchResult
import com.hoangkotlin.chatappsocial.core.model.search.SearchUser
import com.hoangkotlin.chatappsocial.core.network.api.SearchApi
import com.hoangkotlin.chatappsocial.core.network.model.dto.SearchGroupDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.SearchResultDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.SearchUserDto
import com.hoangkotlin.chatappsocial.core.network.model.request.PageableRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResource
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResourceRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "DefaultSearchRepository"

class DefaultSearchRepository @Inject constructor(
    private val searchApi: SearchApi
) : SearchRepository {

    override fun search(
        searchQuery: String,
        offset: Int,
        limit: Int
    ): Flow<DataResult<SearchResult>> = flow {

        try {
            val response = searchApi.search(
                query = searchQuery,
                offset = offset, limit = limit
            )
            if (response.isSuccessful && response.body() != null) {
                emit(DataResult.Success.Network(response.body()!!.data!!.asSearchResult()))
                return@flow
            }

            if (response.code() == 401) {
                emit(
                    DataResult.Error(
                        errorMessage = "Not Auth",
                        errorCode = DataResult.Error.Code.NotAuthorized
                    )
                )
                return@flow
            }
            emit(
                DataResult.Error(
                    errorMessage = "Unknown Error",
                    DataResult.Error.Code.None
                )
            )

        } catch (e: Exception) {
            emit(
                DataResult.Error(
                    errorMessage = e.message.toString(),
                    DataResult.Error.Code.None
                )
            )
        }
    }

    override fun search(
        searchQuery: String,
        resource: Map<SearchResource, PageableRequest>
    ): Flow<DataResult<SearchResult>> = flow {
        try {
            val response = searchApi.search(
                SearchResourceRequest(searchQuery, resource)
            )
            if (response.isSuccessful && response.body() != null) {
                emit(DataResult.Success.Network(response.body()!!.data!!.asSearchResult()))
                return@flow
            }

            if (response.code() == 401) {
                emit(
                    DataResult.Error(
                        errorMessage = "Not Auth",
                        errorCode = DataResult.Error.Code.NotAuthorized
                    )
                )
                return@flow
            }
            emit(
                DataResult.Error(
                    errorMessage = "Unknown Error",
                    DataResult.Error.Code.None
                )
            )

        } catch (e: Exception) {
            e.printStackTrace()
            emit(
                DataResult.Error(
                    errorMessage = e.message.toString(),
                    DataResult.Error.Code.None
                )
            )
        }
    }

}

fun SearchResultDto.asSearchResult(): SearchResult {
    return SearchResult(
        searchGroups = searchGroupDtos.map(SearchGroupDto::asSearchGroup).toList(),
        searchUsers = searchUserDtos.map(SearchUserDto::asSearchUser).toList()
    )
}

fun SearchUserDto.asSearchUser(): SearchUser {
    return SearchUser(
        id, name, image ?: "", channelId, friendStatus = FriendStatus.valueOfNullable(friendStatus)

    )
}

fun SearchGroupDto.asSearchGroup(): SearchGroup {
    return SearchGroup(id, image)
}