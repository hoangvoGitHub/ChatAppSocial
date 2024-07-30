package com.hoangkotlin.chatappsocial.core.chat_client.extension

import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.network.model.dto.UpChatMessageDto


internal const val ATTACHMENT_TYPE_IMAGE = "image"
internal const val ATTACHMENT_TYPE_FILE = "file"
private const val EXTRA_DATA_UPLOAD_ID: String = "uploadId"

internal val SocialChatAttachment.isImage: Boolean
    get() = mimeType?.startsWith(ATTACHMENT_TYPE_IMAGE) ?: false

val SocialChatAttachment.isMedia: Boolean
    get() = type != null &&(type == AttachmentType.Image || type == AttachmentType.Video)


var SocialChatAttachment.uploadId: String?
    get() = extraData[EXTRA_DATA_UPLOAD_ID] as String?
    set(value) {
        value?.let { extraData[EXTRA_DATA_UPLOAD_ID] = it }
    }

fun SocialChatAttachment.getDisplayName(): String {
    return this.name ?: this.upload?.name ?: "Unknown file"
}


val SocialChatMessage.hasPendingAttachments: Boolean
    get() = this.attachments.any { attachment ->
        attachment.uploadState is UploadState.InProgress ||
                attachment.uploadState is UploadState.Idle
    }

val SocialChatMessage.hasAttachments: Boolean
    get() = this.attachments.isNotEmpty()

fun SocialChatMessage.asUpChatMessageDto(): UpChatMessageDto {
    return UpChatMessageDto(
        id = this.id,
        cid = this.cid,
        text = this.text,
        replyTo = this.replyTo?.id,
        attachments = this.attachments.map(SocialChatAttachment::asChatAttachmentDto)
    )
}


