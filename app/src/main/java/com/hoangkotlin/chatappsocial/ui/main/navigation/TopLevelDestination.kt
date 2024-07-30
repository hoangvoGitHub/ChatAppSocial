package com.hoangkotlin.chatappsocial.ui.main.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.feature.friend.navigation.friendsRoute
import com.hoangkotlin.chatappsocial.feature.home.navigation.channelsRoute
import com.hoangkotlin.chatappsocial.feature.profile.navigation.profileRoute
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

enum class TopLevelDestination(
    @DrawableRes val iconRes: Int,
    val routeMatcher: String,
    @StringRes val labelText: Int
) {
    HOME(
        iconRes = uiR.drawable.home_smile_angle_svgrepo_com,
        routeMatcher = channelsRoute,
        labelText = commonR.string.home_label,
    ),

    FRIENDS(
        iconRes = uiR.drawable.users_group_rounded_svgrepo_com,
        routeMatcher = friendsRoute,
        labelText = commonR.string.friend_label,
    ),



    PROFILE(
        iconRes = uiR.drawable.user_rounded_svgrepo_com,
        routeMatcher = profileRoute,
        labelText = commonR.string.profile_label,
    ),

}