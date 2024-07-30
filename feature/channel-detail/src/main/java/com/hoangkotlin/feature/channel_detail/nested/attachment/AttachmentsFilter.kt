package com.hoangkotlin.feature.channel_detail.nested.attachment

import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR

enum class AttachmentsFilter(
    @StringRes val labelRes: Int
) {
    Media(labelRes = commonR.string.media),
    File(labelRes = commonR.string.files),
    Other(labelRes = commonR.string.other),
}

internal val DefaultAttachmentsFilters = listOf<AttachmentsFilter>(
    AttachmentsFilter.Media,
    AttachmentsFilter.File,
    AttachmentsFilter.Other
)