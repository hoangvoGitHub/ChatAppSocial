package com.hoangkotlin.feature.channel_detail

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

enum class ChannelDetailAction(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int
) {
    VoiceCall(
        iconRes = uiR.drawable.phone_svgrepo_com,
        labelRes = commonR.string.friend_label
    ),
    VideoCall(
        iconRes = uiR.drawable.videocamera_record_svgrepo_com,
        labelRes = commonR.string.friend_label
    ),
    Notification(
        iconRes = uiR.drawable.bell_bing_svgrepo_com,
        labelRes = commonR.string.friend_label
    )
}

internal val DefaultChannelDetailActions = listOf(
    ChannelDetailAction.VoiceCall,
    ChannelDetailAction.VideoCall,
    ChannelDetailAction.Notification,
)