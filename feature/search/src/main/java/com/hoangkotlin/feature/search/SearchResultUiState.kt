package com.hoangkotlin.feature.search

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.SearchResult
import com.hoangkotlin.chatappsocial.core.model.search.SearchGroup
import com.hoangkotlin.chatappsocial.core.model.search.SearchUser
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction


sealed interface SearchResultUiState {
    data object Loading : SearchResultUiState

    /**
     * The state query is empty or too short. To distinguish the state between the
     * (initial state or when the search query is cleared) vs the state where no search
     * result is returned, explicitly define the empty query state.
     */
    data object EmptyQuery : SearchResultUiState

    data class LoadFailed(val errorCode: DataResult.Error.Code) : SearchResultUiState

    data class Success(
        val searchResult: SearchResultState
    ) : SearchResultUiState {
        fun isEmpty(): Boolean = searchResult.isEmpty()
    }

//    /**
//     * A state where the search contents are not ready. This happens when the *Fts tables are not
//     * populated yet.
//     */
//    data object SearchNotReady : SearchResultUiState
}

data class SearchResultState(
    val userItems: List<SearchUserItem> = emptyList(),
    val groupItems: List<SearchGroupItem> = emptyList(),
) {
    fun isEmpty(): Boolean = userItems.isEmpty() && groupItems.isEmpty()
}

data class SearchUserItem(
    val searchUser: SearchUser,
    val possibleActions: List<FriendPossibleAction>
)

data class SearchGroupItem(
    val searchGroup: SearchGroup
)

fun SearchResult.asSearchResultState(): SearchResultState {
    return SearchResultState(
        userItems = searchUsers.map(SearchUser::asSearchUserItem),
        groupItems = searchGroups.map(SearchGroup::asSearchGroupItem),

        )
}

fun SearchUser.asSearchUserItem(): SearchUserItem {
    return SearchUserItem(
        searchUser = this,
        possibleActions = this.friendStatus.extractActions().toList()
    )
}

fun FriendStatus?.extractActions(): Set<FriendPossibleAction> {
    return when (this) {
        FriendStatus.REQUEST_FROM_ME -> setOf(
            FriendPossibleAction.RemoveRequest
        )

        FriendStatus.REQUEST_FROM_OTHER -> setOf(
            FriendPossibleAction.AcceptFriend,
            FriendPossibleAction.RejectFriend,
        )

        FriendStatus.FRIEND -> setOf(
            FriendPossibleAction.RemoveFriend,
        )

        null -> setOf(
            FriendPossibleAction.SendRequest,
        )
    }
}

fun SearchGroup.asSearchGroupItem(): SearchGroupItem {
    return SearchGroupItem(searchGroup = this)
}

fun SearchResultState.takeWithLimit(
    defaultUserToDisplayLimit: Int,
    defaultChannelToDisplayLimit: Int
): SearchResultState {
    return SearchResultState(
        userItems.take(defaultUserToDisplayLimit),
        groupItems.take(defaultChannelToDisplayLimit),
    )
}


