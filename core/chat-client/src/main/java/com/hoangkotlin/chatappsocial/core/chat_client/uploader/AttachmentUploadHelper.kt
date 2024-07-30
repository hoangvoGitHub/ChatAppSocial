package com.hoangkotlin.chatappsocial.core.chat_client.uploader

import android.webkit.MimeTypeMap
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.chat_client.extension.uploadId
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import java.io.File
import java.util.Date
import javax.inject.Inject

class AttachmentUploadHelper @Inject constructor(
    private val chatClient: ChatClient
) {
    suspend fun uploadAttachment(
        channelId: String,
        attachment: SocialChatAttachment,
        callback: ProgressCallback? = null,
        onRecoverFailedUpload: (SocialChatAttachment) -> Unit,
    ): DataResult<SocialChatAttachment> {
        val file =
            checkNotNull(attachment.upload) { "An attachment to upload must have a non null upload" }


        return when (attachment.type) {
            AttachmentType.Image -> uploadImage(
                channelId = channelId,
                file = file,
                attachment = attachment,
                callback = callback,
                onRecoverFailedUpload = onRecoverFailedUpload
            )

            AttachmentType.Video,
            AttachmentType.Audio,
            AttachmentType.File,
            AttachmentType.Unknown,
            null -> uploadFile(channelId, attachment, callback)
        }

    }


    private suspend fun uploadImage(
        channelId: String,
        file: File,
        attachment: SocialChatAttachment,
        callback: ProgressCallback? = null,
        onRecoverFailedUpload: (SocialChatAttachment) -> Unit,
    ): DataResult<SocialChatAttachment> {
        val result = chatClient.sendImage(
            channelId, attachment.uploadId!!, file, callback
        )
        val mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension)
            ?: attachment.mimeType ?: ""
        return when (result) {
            is DataResult.Success -> {
                val updatedAttachment = attachment.enrichWithNetworkResource(
                    file = file,
                    attachmentType = attachment.type ?: AttachmentType.File,
                    mimeType = mimeType,
                    url = result.data.file,
                    createdAt = result.data.createdAt
                )
                onSuccessUpload(
                    updatedAttachment,
                    callback
                )
            }

            is DataResult.Error -> {
                onErrorUpload(
                    failedAttachment = attachment,
                    callback = callback,
                    errorResult = result,
                    onRecoverFailedUpload = onRecoverFailedUpload
                )
            }
        }
    }

    private fun onSuccessUpload(
        successfulAttachment: SocialChatAttachment,
        callback: ProgressCallback?
    ): DataResult<SocialChatAttachment> {
        callback?.onSuccess(successfulAttachment.url!!)
        return DataResult.Success.Network(
            data = successfulAttachment.copy(
                uploadState = UploadState.Success
            )
        )
    }

    private fun onErrorUpload(
        failedAttachment: SocialChatAttachment,
        onRecoverFailedUpload: (SocialChatAttachment) -> Unit,
        callback: ProgressCallback?,
        errorResult: DataResult.Error
    ): DataResult<SocialChatAttachment> {
        callback?.onError(Exception(errorResult.errorMessage))
        onRecoverFailedUpload(
            failedAttachment.copy(
                uploadState = UploadState.Failed(
                    message = errorResult.errorMessage
                )
            )
        )
        return errorResult
    }

    suspend fun uploadFile(
        channelId: String,
        attachment: SocialChatAttachment,
        callback: ProgressCallback? = null
    ): DataResult<SocialChatAttachment> {
        return DataResult.Success.Network(data = SocialChatAttachment())
    }

    private fun SocialChatAttachment.enrichWithNetworkResource(
        file: File,
        attachmentType: AttachmentType,
        mimeType: String,
        url: String,
        createdAt: Date,
    ): SocialChatAttachment {
        return copy(
            name = if (name.isNullOrBlank()) file.name else name,
            fileSize = file.length().toInt(),
            mimeType = mimeType,
            url = url,
            uploadState = UploadState.Success,
            type = attachmentType,
            imageUrl = if (attachmentType == AttachmentType.Image) url else imageUrl,
            createdAt = createdAt
        )
    }

}