package com.hoangkotlin.chatappsocial.core.ui

import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR

sealed interface FriendPossibleAction {
    @get:StringRes
    val label: Int

    sealed class ActionForFriend : FriendPossibleAction
    sealed class ActionForNoneFriend : FriendPossibleAction
    sealed class ActionForFriendRequestFromOther : FriendPossibleAction
    sealed class ActionForFriendRequestFromMe : FriendPossibleAction


    data object RemoveFriend : ActionForFriend() {
        override val label: Int
            get() = commonR.string.remove_friend
    }

    data object RemoveRequest : ActionForFriendRequestFromMe() {
        override val label: Int
            get() = commonR.string.remove_request
    }

    data object RejectFriend : ActionForFriendRequestFromOther() {
        override val label: Int
            get() = commonR.string.reject_friend
    }

    data object AcceptFriend : ActionForFriendRequestFromOther() {
        override val label: Int
            get() = commonR.string.accept_friend
    }

    data object SendRequest : ActionForNoneFriend() {
        override val label: Int
            get() = commonR.string.send_request
    }


}





