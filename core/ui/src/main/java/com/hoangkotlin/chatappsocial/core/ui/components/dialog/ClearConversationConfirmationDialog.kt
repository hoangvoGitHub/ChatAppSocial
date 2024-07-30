package com.hoangkotlin.chatappsocial.core.ui.components.dialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.hoangkotlin.chatappsocial.core.common.R
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.getDisplayName
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewUserData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@Composable
fun ClearConversationConfirmationDialog(
    modifier: Modifier = Modifier,
    channel: SocialChatChannel,
    currentUser: SocialChatUser?,
    onDismissRequest: () -> Unit,
    onConfirmation: (SocialChatChannel) -> Unit,
    dialogTitle: String = stringResource(id = R.string.clear_conversation_title),
    dialogText: String = stringResource(
        id = R.string.clear_conversation_content_text, channel.getDisplayName(
            LocalContext.current, currentUser, 3
        )
    ),
    icon: ImageVector? = null,
) {
    AlertDialog(
        modifier = modifier,
        icon = {
            icon?.let { Icon(it, contentDescription = it.name) }
        },
        title = {
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            FilledTonalButton(
                onClick = {
                    onConfirmation(channel)
                    onDismissRequest()
                }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text("Dismiss")
            }
        }
    )
}

@Preview
@Composable
fun ClearConversationConfirmationDialogPreview() {
    SocialChatAppTheme {
        ClearConversationConfirmationDialog(
            channel = PreviewChannelData.channelWithTwoUsers,
            currentUser = PreviewUserData.me,
            onDismissRequest = { /*TODO*/ },
            onConfirmation = {}
        )
    }
}