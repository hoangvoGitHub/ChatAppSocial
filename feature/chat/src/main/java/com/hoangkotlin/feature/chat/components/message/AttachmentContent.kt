package com.hoangkotlin.feature.chat.components.message

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.hoangkotlin.chatappsocial.core.common.utils.MediaStringUtil.bytesToHumanReadableSize
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatMessageItemState
import com.hoangkotlin.chatappsocial.core.ui.components.image.rememberSocialImagePainter
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewAttachmentData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@Composable
fun Attachments(
    modifier: Modifier = Modifier,
    attachments: List<SocialChatAttachment>,
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    val groupedAttachments = remember {
        attachments.groupBy { it.type }.filterNot { it.key == null }
            .toSortedMap(compareBy { it!!.displayPriority })
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        groupedAttachments.forEach { (attachmentType, attachments) ->
            AttachmentsResult(
                attachmentType = attachmentType!!,
                attachments = attachments,
                onAttachmentClick = onAttachmentClick
            )
        }
    }
}


@Composable
fun AttachmentsResult(
    modifier: Modifier = Modifier,
    attachmentType: AttachmentType,
    attachments: List<SocialChatAttachment>,
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    when (attachmentType) {
        AttachmentType.Image -> ImageAttachmentsContent(
            modifier = modifier,
            attachments = attachments,
            onAttachmentClick = onAttachmentClick
        )

        AttachmentType.Video -> VideoAttachmentsContent()
        AttachmentType.Audio -> AudioAttachmentsContent()
        AttachmentType.File,
        AttachmentType.Unknown -> FileAttachmentsContent()
    }
}

@Composable
fun ImageAttachmentsContent(
    modifier: Modifier = Modifier,
    onAttachmentClick: (SocialChatAttachment) -> Unit,
    attachments: List<SocialChatAttachment>
) {
    if (attachments.size < 4) {
        StretchedImageAttachmentsContent(
            modifier = modifier,
            attachments = attachments,
            onAttachmentClick = onAttachmentClick
        )
    } else {
        OverlappedImageAttachmentsContent(
            modifier = modifier,
            attachments = attachments,
            onAttachmentClick = onAttachmentClick
        )
    }
}

@Composable
fun StretchedImageAttachmentsContent(
    modifier: Modifier = Modifier,
    attachments: List<SocialChatAttachment>,
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    Box(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(2.dp)) {
            attachments.forEach { attachment ->
                ImageAttachment(
                    attachment = attachment,
                    onAttachmentClick = onAttachmentClick
                )
            }
        }
    }
}

@Composable
fun OverlappedImageAttachmentsContent(
    modifier: Modifier = Modifier,
    attachments: List<SocialChatAttachment>,
    onAttachmentClick: (SocialChatAttachment) -> Unit
) {
    Box(modifier = modifier) {
        attachments.take(5).forEachIndexed { index, attachment ->
            val (rotateDegree, offsetX, offsetY) = when {
                index == 0 -> Triple(0f, 0, 0)
                (index % 2 == 0) -> Triple(
                    OverlappedImageAttachmentRotateDegree.toFloat(),
                    OverlappedImageAttachmentRotateDegree,
                    -OverlappedImageAttachmentRotateDegree
                )

                else -> Triple(
                    -OverlappedImageAttachmentRotateDegree.toFloat(),
                    -OverlappedImageAttachmentRotateDegree,
                    -OverlappedImageAttachmentRotateDegree
                )
            }
            ImageAttachment(
                attachment = attachment,
                onAttachmentClick = onAttachmentClick,
                modifier = Modifier
                    .zIndex(-index.toFloat())
                    .rotate(rotateDegree)
                    .offset(x = offsetX.dp, y = offsetY.dp)

            )
        }
    }
}

@Composable
fun ImageAttachment(
    modifier: Modifier = Modifier,
    onAttachmentClick: (SocialChatAttachment) -> Unit,
    attachment: SocialChatAttachment
) {
    val painter = rememberSocialImagePainter(
        data = attachment.upload ?: attachment.imageUrl,
        contentScale = ContentScale.Crop,
        placeholderPainter = ColorPainter(MaterialTheme.colorScheme.outlineVariant),
    )
    Box(
        modifier = modifier
            .size(95.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable {
                onAttachmentClick(attachment)
            }
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
    }
}


@Composable
fun VideoAttachmentsContent() {

}

@Composable
fun AudioAttachmentsContent() {

}

@Composable
fun FileAttachmentsContent() {

}

@Composable
fun Attachment(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment
) {
    Row(modifier = modifier) {
        Text(text = attachment.name ?: "No name")
        when (val uploadState = attachment.uploadState) {
            is UploadState.Failed -> Text(text = "Failed")
            is UploadState.Idle -> Text(text = "0/${bytesToHumanReadableSize(attachment.fileSize.toLong())}")
            is UploadState.InProgress -> Text(
                text = "${bytesToHumanReadableSize(uploadState.bytesUploaded)}/${
                    bytesToHumanReadableSize(
                        uploadState.totalBytes
                    )
                }"
            )

            is UploadState.Success -> Text(text = "Success")
            null -> Text(text = "Success")
        }
    }

}

@Preview(showBackground = true)
@Composable
fun StretchedImageAttachmentsContentPreview() {
    SocialChatAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            StretchedImageAttachmentsContent(
                modifier = Modifier.align(Alignment.Center),
                attachments = PreviewAttachmentData.imageAttachments.take(3),
                onAttachmentClick = {}
            )
        }

    }
}

@Preview(showBackground = true)
@Composable
fun OverlappedImageAttachmentsContentPreview() {
    SocialChatAppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            OverlappedImageAttachmentsContent(
                modifier = Modifier.align(Alignment.Center),
                attachments = PreviewAttachmentData.imageAttachments,
                onAttachmentClick = {}
            )
        }

    }
}


@Preview(showBackground = true)
@Composable
fun AttachmentsPreview() {
    val mockAttachments = listOf(
        SocialChatAttachment(
            fileSize = 1000,
            uploadState = UploadState.Success
        ),
        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.InProgress(1000L, 2000L)
        ),

        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.Failed("")
        ),
        SocialChatAttachment(
            fileSize = 2000,
            uploadState = UploadState.Idle
        )

    )

    val mockMessageItem = ChatMessageItemState(
        message = SocialChatMessage(
            "test_id",
            attachments = mockAttachments
        )
    )
    SocialChatAppTheme {
        Attachments(attachments = mockMessageItem.message.attachments,
            onAttachmentClick = {})
    }
}

private const val OverlappedImageAttachmentRotateDegree = 5