package com.hoangkotlin.chatappsocial.feature.media_viewer.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

enum class MediaViewerAction(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
) {
    Download(
        iconRes = uiR.drawable.round_arrow_down_svgrepo_com,
        labelRes = commonR.string.media
    ),
    Edit(
        iconRes = uiR.drawable.pen_svgrepo_com,
        labelRes = commonR.string.media
    ),
    More(
        iconRes = uiR.drawable.noun_more,
        labelRes = commonR.string.media
    )
}

internal val DefaultMediaViewerActions: List<MediaViewerAction> by lazy {
    listOf(MediaViewerAction.Download, MediaViewerAction.Edit, MediaViewerAction.More)
}