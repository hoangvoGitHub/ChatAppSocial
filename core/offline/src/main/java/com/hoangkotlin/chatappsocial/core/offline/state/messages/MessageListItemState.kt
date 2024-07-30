package com.hoangkotlin.chatappsocial.core.offline.state.messages

import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import java.util.Date

sealed class MessageListItemState

data class DateSeparatorState(val date: Date) : MessageListItemState()

data class ChatMessageItemState(
    val message: SocialChatMessage,
    val groupPosition: MessageItemGroupPosition = MessageItemGroupPosition.None,
    val parentMessageId: String? = null,
    val isMine: Boolean = false,
    val isInThread: Boolean = false,
    val currentUser: SocialChatUser? = null,
    val isMessageRead: Boolean = false,
    val shouldShowFooter: Boolean = false,
    val shouldShowSyncStatus: Boolean = true,
    val lastReadBy: List<SocialChannelRead> = emptyList()
) : MessageListItemState()

