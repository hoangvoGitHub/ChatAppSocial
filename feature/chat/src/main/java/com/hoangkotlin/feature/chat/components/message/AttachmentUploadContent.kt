package com.hoangkotlin.feature.chat.components.message

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CheckCircleOutline
import androidx.compose.material.icons.rounded.ErrorOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.chat_client.extension.getDisplayName
import com.hoangkotlin.chatappsocial.core.common.utils.MediaStringUtil
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatMessageItemState
import com.hoangkotlin.chatappsocial.core.ui.components.image.rememberSocialImagePainter
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewChannelData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme
import com.hoangkotlin.feature.chat.model.AttachmentUploadPosition

@Composable
fun AttachmentUploads(
    modifier: Modifier = Modifier,
    messageItem: ChatMessageItemState
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        messageItem.message.attachments.forEachIndexed { index, attachment ->
            val position = calculatePositionForUpload(
                index,
                messageItem.message.attachments.size
            )
            AttachmentUpload(
                attachment = attachment,
                displayPosition = position
            )
        }
    }
}

@Composable
fun AttachmentUpload(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment,
    displayPosition: AttachmentUploadPosition = AttachmentUploadPosition.None
) {
    val shape = displayPosition.itemShape()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.outlineVariant,
                shape = shape
            )
            .padding(4.dp),
    ) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.background,
                    shape = RoundedCornerShape(10.dp)
                )
                .padding(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            UploadLeadingContent(
                modifier = Modifier.padding(end = 8.dp),
                attachment = attachment
            )

            Column {
                UploadFileName(fileName = attachment.getDisplayName())

                UploadStateContent(attachment = attachment)

            }
        }
    }
}

@Composable
fun UploadLeadingContent(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment
) {
    Box(modifier = modifier) {
        when (attachment.type) {
            AttachmentType.Image -> ImageUploadLeadingContent(attachment = attachment)
            AttachmentType.Video -> VideoUploadLeadingContent(attachment = attachment)
            AttachmentType.Audio,
            AttachmentType.File,
            AttachmentType.Unknown,
            null -> VideoUploadLeadingContent(attachment = attachment)
        }
    }
}

@Composable
fun UploadFileName(
    modifier: Modifier = Modifier,
    fileName: String
) {
    Text(
        modifier = modifier,
        text = fileName,
        maxLines = 1,
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onBackground,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
fun UploadStateContent(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment
) {
    Box(modifier = modifier) {
        when (val uploadState = attachment.uploadState) {
            is UploadState.Idle -> UploadProgressText(
                totalBytes = attachment.fileSize.toLong(),
                uploadedBytes = 0
            )

            is UploadState.InProgress -> UploadProgressText(
                totalBytes = uploadState.totalBytes,
                uploadedBytes = uploadState.bytesUploaded
            )

            is UploadState.Success -> UploadSuccessIndicator()

            is UploadState.Failed,
            null -> UploadFailedIndicator()
        }
    }
}

@Composable
fun UploadProgressText(
    modifier: Modifier = Modifier,
    totalBytes: Long,
    uploadedBytes: Long
) {
    val totalBytesText = MediaStringUtil.bytesToHumanReadableSize(totalBytes)
    val uploadedBytesText = MediaStringUtil.bytesToHumanReadableSize(uploadedBytes)
    Text(
        text = "$uploadedBytesText / $totalBytesText",
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = modifier.alpha(0.7f)
    )
}

@Composable
fun UploadSuccessIndicator(
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier.size(16.dp),
        imageVector = Icons.Rounded.CheckCircleOutline,
        tint = MaterialTheme.colorScheme.onBackground,
        contentDescription = null,
    )
}

@Composable
fun UploadFailedIndicator(
    modifier: Modifier = Modifier
) {
    Icon(
        modifier = modifier.size(16.dp),
        imageVector = Icons.Rounded.ErrorOutline,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.error
    )
}

@Composable
fun ImageUploadLeadingContent(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment
) {
    val painter = rememberSocialImagePainter(
        data = attachment.upload ?: attachment.imageUrl ?: attachment.thumbnailUrl,
        placeholderPainter = ColorPainter(MaterialTheme.colorScheme.outlineVariant),
        contentScale = ContentScale.Crop,
    )
    Image(
        contentScale = ContentScale.Crop,
        painter = painter, contentDescription = "ImageUploadLeadingContent",
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .size(32.dp)

    )
}

@Composable
fun VideoUploadLeadingContent(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment
) {
    Box(modifier = modifier.size(32.dp))
}

@Composable
fun AudioUploadLeadingContent(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment
) {
    Box(modifier = modifier.size(48.dp))
}

@Composable
fun FileUploadLeadingContent(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment
) {
    Box(modifier = modifier.size(48.dp))
}

@Preview(showBackground = true)
@Composable
fun AttachmentUploadPreview() {
    SocialChatAppTheme {
        val configuration = LocalConfiguration.current
        val screenWidth = configuration.screenWidthDp.dp
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.End
        ) {
            AttachmentUploads(
                modifier = Modifier.width(screenWidth.times(0.6f)),
                messageItem = ChatMessageItemState(
                    message = PreviewChannelData.messageWithAttachmentsInProgress
                )
            )
        }

    }
}

@Preview(showBackground = true, uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun AttachmentUploadPreviewDark() {
    AttachmentUploadPreview()
}

private fun calculatePositionForUpload(index: Int, size: Int): AttachmentUploadPosition {
    check(index < size) { "Index must be smaller than size" }
    return when {
        index == 0 && size == 1 -> AttachmentUploadPosition.None
        index == 0 && size > 1 -> AttachmentUploadPosition.Top
        index == size - 1 -> AttachmentUploadPosition.Bottom
        else -> AttachmentUploadPosition.Middle
    }
}

private fun AttachmentUploadPosition.itemShape(): Shape {
    return when (this) {
        AttachmentUploadPosition.None -> RoundedCornerShape(14.dp)
        AttachmentUploadPosition.Top -> RoundedCornerShape(
            topStart = 14.dp,
            topEnd = 14.dp,
            bottomEnd = 0.dp,
            bottomStart = 14.dp
        )

        AttachmentUploadPosition.Bottom -> RoundedCornerShape(
            topStart = 14.dp,
            topEnd = 0.dp,
            bottomStart = 14.dp,
            bottomEnd = 14.dp
        )

        AttachmentUploadPosition.Middle -> RoundedCornerShape(
            topStart = 14.dp,
            topEnd = 0.dp,
            bottomStart = 14.dp,
            bottomEnd = 0.dp
        )
    }
}