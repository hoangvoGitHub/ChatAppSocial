package com.hoangkotlin.chatappsocial.feature.friend.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

enum class SocialChatFriendTab(
    @StringRes val displayNameRes: Int,
    @DrawableRes val iconRes: Int,
) {
    FriendList(
        displayNameRes = commonR.string.friend_list,
        iconRes = uiR.drawable.users_group_rounded_svgrepo_com
    ),
    FriendRequest(
        displayNameRes = commonR.string.friend_requests,
        iconRes = uiR.drawable.hand_shake_svgrepo_com
    )
}