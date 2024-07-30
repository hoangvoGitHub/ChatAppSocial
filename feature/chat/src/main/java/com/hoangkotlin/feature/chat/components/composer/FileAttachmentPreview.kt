package com.hoangkotlin.feature.chat.components.composer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.UploadFile
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.common.utils.MediaStringUtil
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewAttachmentData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@Composable
fun FileAttachmentPreview(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment,
    onRemoveAttachment: (SocialChatAttachment) -> Unit
) {

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp, color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f),
                    RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(4.dp)
                .widthIn(max = 152.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Rounded.UploadFile,
                contentDescription = Icons.Rounded.UploadFile.name,
                tint = MaterialTheme.colorScheme.onBackground
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp)
            ) {
                Text(
                    text = attachment.name ?: "",
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    color = MaterialTheme.colorScheme.onBackground
                )
                val fileSize = attachment.upload?.length()?.let { length ->
                    MediaStringUtil.bytesToHumanReadableSize(length)
                }

                if (fileSize != null) {
                    Text(
                        text = fileSize,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.alpha(0.5f)
                    )
                }
            }

            CancelAttachmentIcon(
                modifier = Modifier.align(CenterVertically),
                onClick = {
                    onRemoveAttachment(attachment)
                })

        }

    }


}

@Preview(showBackground = true)
@Composable
fun PreviewFileAttachmentPreview() {
    SocialChatAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            FileAttachmentPreview(
                modifier = Modifier.align(Alignment.Center),
                attachment = PreviewAttachmentData.singleFile,
                onRemoveAttachment = {}
            )
        }

    }
}