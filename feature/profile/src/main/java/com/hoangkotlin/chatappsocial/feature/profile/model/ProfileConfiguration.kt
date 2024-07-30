package com.hoangkotlin.chatappsocial.feature.profile.model

import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.R as commonR

enum class ProfileConfiguration(
    val group: ConfigurationGroup,
    @StringRes val titleResId: Int,
    @StringRes val valueResId: Int
){

}


enum class ConfigurationGroup(
    @StringRes val nameResId: Int
) {
    Notification(
        nameResId = commonR.string.notification
    ),
    Appearance(
        nameResId = commonR.string.appearance
    )
}