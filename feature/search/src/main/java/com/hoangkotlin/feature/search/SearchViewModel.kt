package com.hoangkotlin.feature.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.network.model.request.PageableRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SearchResource
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val chatClient: ChatClient
) : ViewModel() {

    private val searchResources: List<SearchResource> = defaultSearchResource

    private val _searchQuery = MutableStateFlow(SearchQueryState())
    val searchQuery = _searchQuery.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val _rawSearchResultUiState = _searchQuery.flatMapLatest { queryState ->
        if (queryState.query.length < SEARCH_QUERY_MIN_LENGTH) {
            flowOf(SearchResultUiState.EmptyQuery as SearchResultUiState)
        } else {
            val resource: Map<SearchResource, PageableRequest> =
                searchResources.associateWith { PageableRequest() }

            chatClient.search(queryState.query, resource)
                .transformLatest { result ->
                    when (result) {
                        is DataResult.Error -> emit(SearchResultUiState.LoadFailed(result.errorCode))
                        is DataResult.Success -> emit(SearchResultUiState.Success(result.data.asSearchResultState()))
                    }
                }
        }
    }

    val searchResultUiState = _rawSearchResultUiState.map {
        when (it) {
            SearchResultUiState.EmptyQuery,
            is SearchResultUiState.LoadFailed,
            SearchResultUiState.Loading -> it

            is SearchResultUiState.Success -> it.copy(
                searchResult = it.searchResult.takeWithLimit(
                    defaultUserToDisplayLimit,
                    defaultChannelToDisplayLimit
                )
            )
        }
    }.stateIn(
        viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = SearchResultUiState.Loading,
    )

    fun onSearchQueryChanged(query: String) {
        if (_searchQuery.value.query != query) {
            _searchQuery.update { currentState ->
                currentState.copy(query = query)
            }
        }
    }

    fun performAction(userId: String, action: FriendPossibleAction) {
        viewModelScope.launch {
            when (action) {
                FriendPossibleAction.RemoveFriend -> chatClient.removeFriend(userId)
                FriendPossibleAction.RemoveRequest -> chatClient.rejectFriend(userId)
                FriendPossibleAction.AcceptFriend -> chatClient.acceptFriend(userId)
                FriendPossibleAction.RejectFriend -> chatClient.rejectFriend(userId)
                FriendPossibleAction.SendRequest -> chatClient.addFriend(userId)
            }

        }
    }

    companion object {
        private const val TAG = "SearchViewModel"

        /** Minimum length where search query is considered as [SearchResultUiState.EmptyQuery] */
        private const val SEARCH_QUERY_MIN_LENGTH = 2

        private const val defaultUserToDisplayLimit = 5
        private const val defaultChannelToDisplayLimit = 5
        private val defaultSearchResource = SearchResource.entries

    }

}
