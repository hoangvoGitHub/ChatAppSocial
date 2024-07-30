package com.hoangkotlin.chatappsocial.core.ui.components.image

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale

/**
 * Renders an image the [painter] provides. It allows for customization,
 * uses the 'avatar' shape from [ChatTheme.shapes] for the clipping and exposes an [onClick] action.
 *
 * @param painter The painter for the image.
 * @param modifier Modifier for styling.
 * @param shape The shape of the avatar.
 * @param contentDescription Description of the image.
 * @param onClick OnClick action, that can be nullable.
 */
@Composable
fun AvatarImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    contentDescription: String? = null,
    onClick: (() -> Unit)? = null,
) {
    val clickableModifier: Modifier = if (onClick != null) {
        modifier.clickable(
            onClick = onClick,
            indication = rememberRipple(bounded = false),
            interactionSource = remember { MutableInteractionSource() }
        )
    } else {
        modifier
    }

    Image(
        modifier = clickableModifier.clip(shape),
        contentScale = ContentScale.Crop,
        painter = painter,
        contentDescription = contentDescription
    )
}
