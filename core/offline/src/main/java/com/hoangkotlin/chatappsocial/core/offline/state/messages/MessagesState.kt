package com.hoangkotlin.chatappsocial.core.offline.state.messages

import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser

data class MessagesState(
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val endOfMessages: Boolean = false,
    val messageItems: List<MessageListItemState> = emptyList(),
    val currentUser: SocialChatUser? = null,
//    val newMessageState: NewMessageState? = null,
//    val parentMessageId: String? = null,
    val unreadCount: Int = 0,
)