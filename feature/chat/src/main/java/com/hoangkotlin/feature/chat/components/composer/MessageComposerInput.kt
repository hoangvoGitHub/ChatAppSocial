package com.hoangkotlin.feature.chat.components.composer

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.SpringSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.feature.chat.MessageInputState

@Composable
fun MessageComposerInput(
    modifier: Modifier = Modifier,
    messageInputState: MessageInputState,
    onValueChange: (String) -> Unit,
    onFocusChange: (isFocused: Boolean) -> Unit,
    onRemoveAttachment: (SocialChatAttachment) -> Unit,
) {
    val (inputValue, _, _, attachments) = messageInputState;
    Column(
        modifier = modifier
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                RoundedCornerShape(20.dp)
            )
            .animateContentSize()
    ) {
        if (attachments.isNotEmpty()) {
            DefaultAttachmentPreviews(
                attachments = attachments,
                onRemoveAttachment = onRemoveAttachment
            )
        }
        MessageComposerTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputValue,
            onFocusChange = onFocusChange,
            onValueChange = onValueChange
        )
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultAttachmentPreviews(
    modifier: Modifier = Modifier,
    attachments: List<SocialChatAttachment>,
    onRemoveAttachment: (SocialChatAttachment) -> Unit
) {
    val containsFiles = false
    remember(key1 = attachments) {
        attachments.fastAny { it.type == AttachmentType.File }
    }
    LazyRow(
        modifier = modifier
            .padding(8.dp)
            .clip(
                RoundedCornerShape(
                    topStart = 12.dp,
                    topEnd = 12.dp
                )
            ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = attachments) { attachment ->
            if (containsFiles) {
                FileAttachmentPreview(
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = SpringSpec()
                    ),
                    attachment = attachment,
                    onRemoveAttachment = {}
                )
            } else {
                MediaAttachmentPreview(
                    modifier = Modifier.animateItemPlacement(
                        animationSpec = SpringSpec()
                    ),
                    attachment = attachment,
                    onRemoveAttachment = onRemoveAttachment
                )
            }

        }
    }
}

private const val TAG = "MessageComposerInput"







