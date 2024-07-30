package com.hoangkotlin.chatappsocial.feature.home.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.hoangkotlin.chatappsocial.core.ui.theme.primaryDark
import com.hoangkotlin.chatappsocial.core.ui.theme.primaryLight
import com.hoangkotlin.chatappsocial.core.common.R as commonR
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

enum class BehindMotionAction(
    @DrawableRes val iconRes: Int,
    val tintColor: Color,
    val tintColorDark: Color,
    val backgroundColor: Color,
    val backgroundColorDark: Color,
    @StringRes val contentDescriptionRes: Int,
) {
    NotificationOn(
        iconRes = uiR.drawable.notification,
        tintColor = Color.White,
        tintColorDark = Color.Black,
        backgroundColor = primaryLight,
        backgroundColorDark = primaryDark,
        contentDescriptionRes = commonR.string.notification
    ),
    NotificationOff(
        iconRes = uiR.drawable.notification,
        tintColor = Color.White,
        tintColorDark = Color.Black,
        backgroundColor = primaryLight,
        backgroundColorDark = primaryDark,
        contentDescriptionRes = commonR.string.notification
    ),
    Trash(
        iconRes = uiR.drawable.trash,
        tintColor = Color.White,
        tintColorDark = Color.Black,
        backgroundColor = Color.Red,
        backgroundColorDark = Color.Red,
        contentDescriptionRes = commonR.string.trash
    )
}