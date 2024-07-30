package com.hoangkotlin.chatappsocial.feature.friend.model

import com.hoangkotlin.chatappsocial.core.model.SocialChatFriend
import com.hoangkotlin.chatappsocial.core.ui.FriendPossibleAction

data class ChatFriendItemState(
    val chatFriend: SocialChatFriend,
    val actionType: Map<FriendPossibleAction, FriendActionState>
)



