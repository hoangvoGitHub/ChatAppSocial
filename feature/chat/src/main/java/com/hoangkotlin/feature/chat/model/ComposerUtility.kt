package com.hoangkotlin.feature.chat.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

enum class ComposerUtility(
    @DrawableRes val iconRes: Int,
    @StringRes val labelRes: Int,
) {
    Camera(
        iconRes = uiR.drawable.camera_svgrepo_com,
        labelRes = commonR.string.camera_image_source_label
    ),
    Gallery(
        iconRes = uiR.drawable.gallery_add_svgrepo_com,
        labelRes = commonR.string.camera_image_source_label
    ),
    Voice(
        iconRes = uiR.drawable.microphone_3_svgrepo_com,
        labelRes = commonR.string.camera_image_source_label
    ),
    File(
        iconRes = uiR.drawable.folder_with_files_svgrepo_com,
        labelRes = commonR.string.camera_image_source_label
    ),
    Map(
        iconRes = uiR.drawable.map_arrow_square_svgrepo_com,
        labelRes = commonR.string.map
    ),
}

internal val DefaultPanelUtilities = listOf(
    ComposerUtility.File,
    ComposerUtility.Map,
)