package com.hoangkotlin.chatappsocial.core.ui.utils

import androidx.compose.runtime.staticCompositionLocalOf
import com.hoangkotlin.chatappsocial.core.common.model.ConnectionState


val LocalSocketState = staticCompositionLocalOf<ConnectionState> { ConnectionState.CONNECTING }
val LocalRunningForBubble = staticCompositionLocalOf<Boolean> { false }