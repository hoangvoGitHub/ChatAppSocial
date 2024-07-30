package com.hoangkotlin.chatappsocial.feature.friend

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.SocialChatFriend
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction
import com.hoangkotlin.chatappsocial.core.offline.extension.getStateOrNull
import com.hoangkotlin.chatappsocial.feature.friend.model.FriendActionState
import com.hoangkotlin.chatappsocial.feature.friend.model.QueryFriendListMutableState
import com.hoangkotlin.chatappsocial.feature.friend.model.QueryFriendListState
import com.hoangkotlin.chatappsocial.feature.friend.model.SocialChatFriendsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "FriendsViewModel"

@HiltViewModel
class FriendsViewModel @Inject constructor(
    private val chatClient: ChatClient
) : ViewModel() {

    private var _queryFriendsState: StateFlow<QueryFriendListState?> =
        MutableStateFlow(null)

    private var _queryFriendRequestState: StateFlow<QueryFriendListState?> =
        MutableStateFlow(null)

    private val _friendListState: MutableStateFlow<SocialChatFriendsState> =
        MutableStateFlow(SocialChatFriendsState())

    val friendListState: StateFlow<SocialChatFriendsState> = _friendListState.asStateFlow()

    private val _friendRequestState: MutableStateFlow<SocialChatFriendsState> =
        MutableStateFlow(SocialChatFriendsState())

    val friendRequestState: StateFlow<SocialChatFriendsState> = _friendRequestState.asStateFlow()


    private var observationJobForFriends: Job? = null
    private var observationJobForFriendRequests: Job? = null


    fun initOnceObservationForFriends() {
        if (observationJobForFriends != null) return
        observationJobForFriends = viewModelScope.launch {
            _queryFriendsState = chatClient.queryFriendsAsState(
                friendStatus = FriendStatus.FRIEND,
                coroutineScope = viewModelScope
            )

            _queryFriendsState.filterNotNull().collectLatest { friendsState ->
                combine(
                    friendsState.friendStates, friendsState.isLoading, friendsState.isLoadingMore
                ) { friends, loading, loadingMore ->
                    _friendListState.value.copy(
                        isLoading = loading,
                        isLoadingMore = loadingMore,
                        channelItems = friends
                    )
                }.collect { newState ->
                    _friendListState.value = newState
                }
            }
        }
    }

    fun initOnceObservationForFriendRequests() {
        if (observationJobForFriendRequests != null) return

        observationJobForFriendRequests = viewModelScope.launch {
            _queryFriendRequestState =
                chatClient.queryFriendsAsState(coroutineScope = viewModelScope)

            _queryFriendRequestState.filterNotNull().collectLatest { friendsState ->
                combine(
                    friendsState.friendStates, friendsState.isLoading, friendsState.isLoadingMore
                ) { friends, loading, loadingMore ->
                    _friendRequestState.value.copy(
                        isLoading = loading,
                        isLoadingMore = loadingMore,
                        channelItems = friends
                    )
                }.collect { newState ->
                    _friendRequestState.value = newState
                }
            }
        }
    }


    fun performActionForFriend(friendUserId: String, action: FriendPossibleAction) {
        viewModelScope.launch {
            _queryFriendsState.filterNotNull().collectLatest { friendsState ->
                friendsState as QueryFriendListMutableState
                friendsState.updateState(friendUserId, action, FriendActionState.Loading)

                // Simulate network latency
                delay(1_000L)

                val result: DataResult<SocialChatFriend?> = when (action) {
                    FriendPossibleAction.RemoveFriend -> chatClient.removeFriend(friendUserId)
                    else -> DataResult.Error(errorMessage = "This action is not allowed.")
                }

                val newState = when (result) {
                    is DataResult.Error ->
                        FriendActionState.Failed(
                            message = result.errorMessage,
                            errorCode = result.errorCode
                        )


                    is DataResult.Success -> FriendActionState.Success
                }

                friendsState.updateState(friendUserId, action, newState)

                if (newState is FriendActionState.Failed) {
                    delay(3_000)
                    friendsState.resetState(friendUserId, action)
                }

            }
        }
    }

    fun performActionForFriendRequests(friendUserId: String, action: FriendPossibleAction) {
        viewModelScope.launch {
            _queryFriendRequestState.filterNotNull().collectLatest { friendsState ->
                friendsState as QueryFriendListMutableState
                friendsState.updateState(friendUserId, action, FriendActionState.Loading)

                // Simulate network latency
                delay(1_000L)

                val result: DataResult<SocialChatFriend?> = when (action) {
                    FriendPossibleAction.AcceptFriend -> chatClient.acceptFriend(friendUserId)
                    FriendPossibleAction.RejectFriend -> chatClient.rejectFriend(friendUserId)
                    else -> DataResult.Error(errorMessage = "This action is not allowed.")
                }

                val newState = when (result) {
                    is DataResult.Error ->
                        FriendActionState.Failed(
                            message = result.errorMessage,
                            errorCode = result.errorCode
                        )


                    is DataResult.Success -> FriendActionState.Success
                }

                friendsState.updateState(friendUserId, action, newState)

                if (newState is FriendActionState.Failed) {
                    delay(3_000)
                    friendsState.updateState(friendUserId, action, FriendActionState.Idle)
                }

            }
        }
    }

    override fun onCleared() {
        super.onCleared()

        observationJobForFriends?.cancel()
        observationJobForFriends = null

        observationJobForFriendRequests?.cancel()
        observationJobForFriendRequests = null
    }

    fun refresh(index: Int) {
        viewModelScope.launch {


            if (index == 0) {
                _queryFriendsState.filterNotNull().collectLatest { friendsState ->
                    if (_friendListState.value.isLoading) return@collectLatest

                    friendsState as QueryFriendListMutableState

                    friendsState.setIsLoading(true)

                    delay(1000L)

                    val result =
                        chatClient.queryFriends(
                            status = FriendStatus.FRIEND
                        )
                    when (result) {
                        is DataResult.Error -> {}
                        is DataResult.Success -> {
                            friendsState.setFriends(result.data)
                        }
                    }
                    friendsState.setIsLoading(false)

                }
            } else {
                _queryFriendRequestState.filterNotNull().collectLatest { friendRequestsState ->
                    if (_friendRequestState.value.isLoading) return@collectLatest

                    friendRequestsState as QueryFriendListMutableState

                    friendRequestsState.setIsLoading(true)

                    delay(1000L)

                    val result =
                        chatClient.queryFriends(
                            status = FriendStatus.REQUEST_FROM_OTHER
                        )
                    when (result) {
                        is DataResult.Error -> {}
                        is DataResult.Success -> {
                            friendRequestsState.setFriends(result.data)
                        }
                    }
                    friendRequestsState.setIsLoading(false)

                }
            }
        }

    }


}

fun com.hoangkotlin.chatappsocial.core.chat_client.ChatClient.queryFriendsAsState(
    query: String = "",
    friendStatus: FriendStatus = FriendStatus.REQUEST_FROM_OTHER,
    limit: Int = 20,
    offset: Int = 0,
    coroutineScope: CoroutineScope
): StateFlow<QueryFriendListState?> {
    return getStateOrNull(coroutineScope) {
        delay(3000L)
        QueryFriendListMutableState(scope = coroutineScope).apply {
            when (val result =
                this@queryFriendsAsState.queryFriends(
                    limit = limit,
                    offset = offset,
                    query = query,
                    status = friendStatus
                )) {
                is DataResult.Error -> {}
                is DataResult.Success -> {
                    setFriends(result.data)
                }
            }
            setIsLoading(false)
        }
    }.stateIn(
        coroutineScope,
        SharingStarted.Eagerly,
        null
    )
}