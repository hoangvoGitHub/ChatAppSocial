package com.hoangkotlin.feature.channel_detail.model

import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR

enum class ChannelDetailDropdown(
    @StringRes val labelRes: Int
) {
    ShowBubble(labelRes = commonR.string.show_bubble),
    HideBubble(labelRes = commonR.string.hide_bubble)
}

val ChannelDetailDropdownsForBubble by lazy { listOf(ChannelDetailDropdown.HideBubble) }
val ChannelDetailDropdownsForNonBubble by lazy { listOf(ChannelDetailDropdown.ShowBubble) }