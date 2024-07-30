package com.hoangkotlin.chatappsocial.feature.profile.model

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CameraAlt
import androidx.compose.material.icons.rounded.ImageSearch
import androidx.compose.ui.graphics.vector.ImageVector
import com.hoangkotlin.chatappsocial.core.common.R as commonR

enum class ImageSourceOption(
    val imageVector: ImageVector,
    @StringRes val labelRes: Int
) {
    Camera(
        imageVector = Icons.Rounded.CameraAlt,
        labelRes = commonR.string.camera_image_source_label
    ),
    Gallery(
        imageVector = Icons.Rounded.ImageSearch,
        labelRes = commonR.string.gallery_image_source_label
    )
}