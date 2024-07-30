package com.hoangkotlin.feature.search

import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR
enum class SearchResultGroup(
    @StringRes val titleResId: Int,
) {
  Users(titleResId = commonR.string.users),
  Channels(titleResId =  commonR.string.channels),
  Messages(titleResId =  commonR.string.messages),
}