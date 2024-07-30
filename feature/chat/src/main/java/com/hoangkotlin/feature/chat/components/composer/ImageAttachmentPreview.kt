package com.hoangkotlin.feature.chat.components.composer

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.ui.components.image.rememberSocialImagePainter
import com.hoangkotlin.chatappsocial.core.ui.previewdatas.PreviewAttachmentData
import com.hoangkotlin.chatappsocial.core.ui.theme.SocialChatAppTheme

@Composable
fun MediaAttachmentPreview(
    modifier: Modifier = Modifier,
    attachment: SocialChatAttachment,
    onRemoveAttachment: (SocialChatAttachment) -> Unit
) {
    val painter = rememberSocialImagePainter(
        data = attachment.upload ?: attachment.imageUrl ?: attachment.thumbnailUrl,
        placeholderPainter = ColorPainter(MaterialTheme.colorScheme.outlineVariant),
        contentScale = ContentScale.Crop,
    )

    Box(
        modifier = modifier
            .size(95.dp)
            .clip(RoundedCornerShape(12.dp))
    ) {
        Image(
            modifier = Modifier.fillMaxSize(),
            painter = painter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        CancelAttachmentIcon(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(4.dp),
            onClick = { onRemoveAttachment(attachment) }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewAttachmentPreview() {
    SocialChatAppTheme {
        MediaAttachmentPreview(
            attachment = PreviewAttachmentData.singleImage,
            onRemoveAttachment = {}
        )
    }

}

private const val TAG = "ImageAttachmentPreview"
