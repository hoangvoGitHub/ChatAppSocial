package com.hoangkotlin.feature.chat.components.composer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import com.hoangkotlin.chatappsocial.core.ui.R as uiR

@Composable
fun CancelAttachmentIcon(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    Icon(
        modifier = modifier
            .clip(CircleShape)
            .background(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.background.copy(alpha = 0.5f)
            )
            .clickable(
                onClick = onClick
            ),
        painter = painterResource(id = uiR.drawable.ic_close),
        contentDescription = null,
        tint = MaterialTheme.colorScheme.onBackground
    )
}
