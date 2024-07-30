package com.hoangkotlin.chatappsocial.core.chat_client.extension

import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatAttachmentDto
import java.util.Date

fun SocialChatAttachment.asChatAttachmentDto(): ChatAttachmentDto {
    return ChatAttachmentDto(
        name = name,
        url = url,
        mimeType = mimeType,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        videoLength = videoLength,
        originalHeight = originalHeight,
        originalWidth = originalWidth,
        type = type?.modelType,
        fileSize = fileSize,
        extraData = extraData,
        createdAt = Date()
    )
}