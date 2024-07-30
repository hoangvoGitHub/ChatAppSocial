package com.hoangkotlin.chatappsocial.core.data.extension

import com.hoangkotlin.chatappsocial.core.database.model.ChatAttachmentEntity
import com.hoangkotlin.chatappsocial.core.database.model.UploadStateEntity
import com.hoangkotlin.chatappsocial.core.database.model.UploadStateEntity.Companion.UPLOAD_STATE_FAILED
import com.hoangkotlin.chatappsocial.core.database.model.UploadStateEntity.Companion.UPLOAD_STATE_IN_PROGRESS
import com.hoangkotlin.chatappsocial.core.database.model.UploadStateEntity.Companion.UPLOAD_STATE_SUCCESS
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.model.attachment.toAttachmentType
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import java.io.File

fun ChatAttachmentEntity.asSocialAttachment(): SocialChatAttachment {
    return SocialChatAttachment(
        name = name,
        url = url,
        mimeType = mimeType,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        originalHeight = originalHeight,
        originalWidth = originalWidth,
        upload = uploadFilePath?.let(::File),
        uploadState = uploadState?.toModel(uploadFilePath?.let(::File)),
        type = type?.toAttachmentType(),
        fileSize = fileSize ?: 0,
        extraData = extraData.toMutableMap()

    )
}

fun SocialChatAttachment.asChatAttachmentEntity(
    messageId: String, index: Int
): ChatAttachmentEntity {
    return ChatAttachmentEntity(
        id = getOrGenerateId(messageId, index),
        messageId = messageId,
        name = name,
        url = url,
        mimeType = mimeType,
        imageUrl = imageUrl,
        thumbnailUrl = thumbnailUrl,
        originalHeight = originalHeight,
        originalWidth = originalWidth,
        uploadFilePath = upload?.path,
        uploadState = uploadState?.toEntity(),
        type = type?.modelType,
        fileSize = fileSize,
        extraData = extraData,
    )
}


private fun UploadState.toEntity(): UploadStateEntity {
    val (statusCode, errorMessage) = when (this) {
        is UploadState.Success -> UPLOAD_STATE_SUCCESS to null
        is UploadState.Idle -> UPLOAD_STATE_IN_PROGRESS to null
        is UploadState.InProgress -> UPLOAD_STATE_IN_PROGRESS to null
        is UploadState.Failed -> UPLOAD_STATE_FAILED to (
                this.message
                )
    }
    return UploadStateEntity(statusCode, errorMessage)
}

private fun UploadStateEntity.toModel(uploadFile: File?): UploadState =
    when (this.statusCode) {
        UPLOAD_STATE_SUCCESS -> UploadState.Success
        UPLOAD_STATE_IN_PROGRESS -> UploadState.InProgress(0, uploadFile?.length() ?: 0)
        UPLOAD_STATE_FAILED -> UploadState.Failed(message = this.errorMessage ?: "")
        else -> error("Integer value of $statusCode can't be mapped to UploadState")
    }

private fun SocialChatAttachment.getOrGenerateId(messageId: String, index: Int): String {
    return if (extraData.containsKey(ChatAttachmentEntity.EXTRA_DATA_ID_KEY)) {
       extraData[ChatAttachmentEntity.EXTRA_DATA_ID_KEY] as String
    } else {
        ChatAttachmentEntity.generateId(messageId, index).also { id ->
            extraData[ChatAttachmentEntity.EXTRA_DATA_ID_KEY] = id
        }
    }
}