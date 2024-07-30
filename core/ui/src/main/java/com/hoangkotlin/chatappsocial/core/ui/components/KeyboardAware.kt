package com.hoangkotlin.chatappsocial.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.imePadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * A composable function that wraps its content with a [Box] and automatically adjusts padding
 * to accommodate the on-screen keyboard (IME - Input Method Editor).
 *
 * @param content The content to be wrapped inside the [KeyboardAware]. It's typically the UI
 *               content that needs to be aware of the keyboard and adjust its layout accordingly.
 */
@Composable
fun KeyboardAware(
    content: @Composable () -> Unit
) {
    Box(modifier = Modifier.imePadding()) {
        content()
    }
}