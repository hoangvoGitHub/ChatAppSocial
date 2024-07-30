package com.hoangkotlin.feature.channel_detail.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

enum class ChannelDetailOptionGroup(
    @StringRes val labelRes: Int
) {
    Modification(labelRes = commonR.string.modification), Other(labelRes = commonR.string.other), PrivacyAndSupport(
        labelRes = commonR.string.privacy_and_support
    )
}


enum class ChannelDetailOptionEntry(
    val group: ChannelDetailOptionGroup,
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
) {
    Theme(
        group = ChannelDetailOptionGroup.Modification,
        iconRes = uiR.drawable.pallete_2_svgrepo_com,
        labelRes = commonR.string.theme,
    ),
    NickName(
        group = ChannelDetailOptionGroup.Modification,
        iconRes = uiR.drawable.noun_font,
        labelRes = commonR.string.nickname
    ),
    MediasAndFiles(
        group = ChannelDetailOptionGroup.Other,
        iconRes = uiR.drawable.album_svgrepo_com,
        labelRes = commonR.string.media_file,
    ),
    PinnedMessages(
        group = ChannelDetailOptionGroup.Other,
        iconRes = uiR.drawable.pin_circle_svgrepo_com,
        labelRes = commonR.string.pinned_messages
    ),
    SearchInConversation(
        group = ChannelDetailOptionGroup.Other,
        iconRes = uiR.drawable.magnifer_svgrepo_com,
        labelRes = commonR.string.search_in_conversation
    ),
    Share(
        group = ChannelDetailOptionGroup.Other,
        iconRes = uiR.drawable.share_svgrepo_com,
        labelRes = commonR.string.share
    ),
    ClearConversation(
        group = ChannelDetailOptionGroup.Other,
        iconRes = uiR.drawable.trash_bin_trash_svgrepo_com,
        labelRes = commonR.string.clear_conversation
    ),
    Block(
        group = ChannelDetailOptionGroup.PrivacyAndSupport,
        iconRes = uiR.drawable.close_circle_svgrepo_com,
        labelRes = commonR.string.block
    ),
    Report(
        group = ChannelDetailOptionGroup.PrivacyAndSupport,
        iconRes = uiR.drawable.danger_triangle_svgrepo_com,
        labelRes = commonR.string.report
    ),
}


val ChannelDetailOptionEntry.tintColor: Color
    @Composable get() = when (this) {
        ChannelDetailOptionEntry.Theme -> MaterialTheme.colorScheme.primary
        ChannelDetailOptionEntry.Report -> MaterialTheme.colorScheme.error
        else -> MaterialTheme.colorScheme.onSurface
    }

val EntriesByGroup = ChannelDetailOptionEntry.entries.groupBy(ChannelDetailOptionEntry::group)