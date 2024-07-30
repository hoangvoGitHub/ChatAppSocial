package com.hoangkotlin.chatappsocial.feature.home.components

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.chatappsocial.feature.home.model.BehindMotionAction

const val BehindMotionActionIconSize = 36
const val BehindMotionActionIconPadding = 8

@Composable
fun BehindMotionActionIcon(
    action: BehindMotionAction,
    modifier: Modifier = Modifier,
    onClick: (BehindMotionAction) -> Unit = {},
    isMute: Boolean = true
) {
    val darkTheme = isSystemInDarkTheme()

    val (tintColor, backgroundColor) = if (darkTheme) {
        Pair(action.tintColorDark, action.backgroundColorDark)
    } else {
        Pair(action.tintColor, action.backgroundColor)
    }
    Box(
        modifier = modifier
            .padding(BehindMotionActionIconPadding.dp)
            .size(BehindMotionActionIconSize.dp)
            .clip(CircleShape)
            .background(backgroundColor)
            .clickable { onClick(action) }
    ) {
        Icon(
            modifier = Modifier
                .padding(8.dp)
                .size(22.dp)
                .align(Alignment.Center)
                .drawBehind {
                    if (action == BehindMotionAction.NotificationOn && isMute)
                        drawIntoCanvas {
                            drawLine(
                                start = Offset(x =8f, y = 8f),
                                end = Offset(
                                    x = 50f,
                                    y = 50f
                                ),
                                color = tintColor,
                                strokeWidth = 4f
                            )
                        }

                },
            painter = painterResource(id = action.iconRes),
            contentDescription = stringResource(id = action.contentDescriptionRes),
            tint = tintColor
        )
    }
}

@Composable
fun BehindMotionActionIconPreview(darkTheme: Boolean) {
    SocialChatAppTheme(darkTheme = darkTheme) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = MaterialTheme.colorScheme.background)
        ) {
            BehindMotionActionIcon(action = BehindMotionAction.NotificationOn)
            BehindMotionActionIcon(action = BehindMotionAction.Trash)
        }

    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun BehindMotionActionIconPreviewDark() {
    BehindMotionActionIconPreview(darkTheme = true)
}


@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_NO)
@Composable
fun BehindMotionActionIconPreviewLight() {
    BehindMotionActionIconPreview(darkTheme = false)
}