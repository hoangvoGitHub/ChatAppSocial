package com.hoangkotlin.chatappsocial.core.ui.components.image

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser

@Composable
fun SocialUserAvatar(
    user: SocialChatUser,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    textStyle: TextStyle = MaterialTheme.typography.labelLarge,
    contentDescription: String? = null,
    showOnlineIndicator: Boolean = true,
    onlineIndicatorAlignment: OnlineIndicatorAlignment = OnlineIndicatorAlignment.TopEnd,
    initialsAvatarOffset: DpOffset = DpOffset(0.dp, 0.dp),
    onlineIndicator: @Composable BoxScope.() -> Unit = {
        DefaultOnlineIndicator(onlineIndicatorAlignment)
    },
    onClick: (() -> Unit)? = null,
) {
    Box(modifier = modifier) {
        SocialAvatar(
            modifier = Modifier.fillMaxSize(),
            imageUrl = user.image,
            initials = user.name.trim()
                .split("\\s+".toRegex())
                .take(2)
                .joinToString(separator = "") { it.take(1).uppercase() },
            textStyle = textStyle,
            shape = shape,
            contentDescription = contentDescription,
            onClick = onClick,
            initialsAvatarOffset = initialsAvatarOffset
        )

        if (showOnlineIndicator && user.isOnline) {
            onlineIndicator()
        }
    }
}

@Composable
internal fun BoxScope.DefaultOnlineIndicator(onlineIndicatorAlignment: OnlineIndicatorAlignment) {
    OnlineIndicator(modifier = Modifier.align(onlineIndicatorAlignment.alignment))
}

@Composable
fun OnlineIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(12.dp)
            .background(MaterialTheme.colorScheme.background, CircleShape)
            .padding(2.dp)
            .background(Color.Green, CircleShape)
    )
}