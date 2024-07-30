package com.hoangkotlin.chatappsocial.feature.friend.model

import kotlinx.coroutines.flow.StateFlow

interface QueryFriendListState {

    val currentQueryRequest: StateFlow<String?>

    val nextQueryRequest: StateFlow<String?>

    val isLoading: StateFlow<Boolean>

    val isLoadingMore: StateFlow<Boolean>

    val isEndOfList: StateFlow<Boolean>

    val friendStates: StateFlow<List<ChatFriendItemState>>

}
