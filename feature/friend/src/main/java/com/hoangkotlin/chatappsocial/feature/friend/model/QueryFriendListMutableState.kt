package com.hoangkotlin.chatappsocial.feature.friend.model

import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.SocialChatFriend
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QueryFriendListMutableState(
    private val scope: CoroutineScope,
) : QueryFriendListState {

    private val _isLoadingMore = MutableStateFlow<Boolean>(false)

    private val _rawFriends = MutableStateFlow<Map<String, SocialChatFriend>>(emptyMap())

    private val _rawFriendsStates = MutableStateFlow<Map<String, ChatFriendItemState>>(emptyMap())

    private val _isLoading = MutableStateFlow<Boolean>(false)

    init {
        observeFriendChanges()
    }

    private fun observeFriendChanges() {
        scope.launch {
            _rawFriends.collectLatest { rawFriends ->
                updateFriendStates(rawFriends)
            }
        }
    }

    private fun updateFriendStates(friends: Map<String, SocialChatFriend>) {
        _rawFriendsStates.update { currentState ->
            currentState.toMutableMap().apply {
                friends.forEach { (id, friend) ->
                    val actionType = when (friend.status) {
                        FriendStatus.REQUEST_FROM_ME -> defaultActionsForRequestFromMe
                        FriendStatus.REQUEST_FROM_OTHER -> defaultActionsForRequestFromOther
                        FriendStatus.FRIEND -> defaultActionsForFriend
                    }
                    putIfAbsent(
                        id, ChatFriendItemState(
                            chatFriend = friend,
                            actionType = actionType
                        )
                    )
                }
            }
        }
    }

    fun updateState(id: String, action: FriendPossibleAction, state: FriendActionState) {
        // use MutableStateFlow.update to avoid race condition
        _rawFriendsStates.update { currentState ->
            if (!currentState.containsKey(id)) {
                currentState
            } else {
                currentState.toMutableMap().apply {
                    val currentActionState = this[id]!!.actionType;
                    this[id] =
                        this[id]!!.copy(actionType = currentActionState.toMutableMap().apply {
                            put(action, state)
                        })
                }

            }

        }
    }

    fun resetState(id: String, action: FriendPossibleAction) {
        updateState(id, action, FriendActionState.Idle)
    }

    override val currentQueryRequest: StateFlow<String?>
        get() = TODO("Not yet implemented")
    override val nextQueryRequest: StateFlow<String?>
        get() = TODO("Not yet implemented")
    override val isLoading: StateFlow<Boolean>
        get() = _isLoading
    override val isLoadingMore: StateFlow<Boolean>
        get() = _isLoadingMore
    override val isEndOfList: StateFlow<Boolean>
        get() = TODO("Not yet implemented")


    override val friendStates: StateFlow<List<ChatFriendItemState>>
        get() = _rawFriendsStates.map {
            it.values.sortedByDescending { friend ->
                friend.chatFriend.createdAt
            }
        }.stateIn(scope, SharingStarted.Eagerly, emptyList())


    fun setFriends(friends: List<SocialChatFriend>) {
        _rawFriends.value += friends.associateBy { it.user.id }
    }

    fun setIsLoading(isLoading: Boolean) {
        _isLoading.value = isLoading
    }

    private val defaultActionsForFriend: Map<FriendPossibleAction, FriendActionState> = mapOf(
        FriendPossibleAction.RemoveFriend to FriendActionState.Idle
    )
    private val defaultActionsForRequestFromOther: Map<FriendPossibleAction, FriendActionState> =
        mapOf(
            FriendPossibleAction.AcceptFriend to FriendActionState.Idle,
            FriendPossibleAction.RejectFriend to FriendActionState.Idle,
        )
    private val defaultActionsForRequestFromMe: Map<FriendPossibleAction, FriendActionState> =
        mapOf(
            FriendPossibleAction.RemoveRequest to FriendActionState.Idle,
        )


}

