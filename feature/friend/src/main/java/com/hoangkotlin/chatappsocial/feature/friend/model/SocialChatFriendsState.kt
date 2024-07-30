package com.hoangkotlin.chatappsocial.feature.friend.model


data class SocialChatFriendsState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isEndOfChannels: Boolean = false,
    val channelItems: List<ChatFriendItemState> = emptyList(),
    val searchQuery: String = ""
)


// reject -  accept - loading

// click reject -> loading for reject-> delete from request  list
// click accept -> loading for accept-> delete from request list
// remove friend -> loading for remove -> delete  from friend list

