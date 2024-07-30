package com.hoangkotlin.feature.chat.utils

import android.content.Context
import android.net.Uri
import com.hoangkotlin.chatappsocial.core.chat_client.utils.AttachmentHelper
import com.hoangkotlin.chatappsocial.core.model.AttachmentMetaData
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton


@Singleton
class ChatAttachmentHelper @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val attachmentHelper: AttachmentHelper = AttachmentHelper()

    private fun getAttachmentsFromMetaData(metaData: List<AttachmentMetaData>): List<SocialChatAttachment> {
        return metaData.map { it.toAttachment(context) }
    }

    fun getAttachmentsFromUris(uris: List<Uri>): List<SocialChatAttachment> {
        return getAttachmentsMetadataFromUris(uris).let(::getAttachmentsFromMetaData)
    }

    private fun getAttachmentsMetadataFromUris(uris: List<Uri>): List<AttachmentMetaData> {
        return attachmentHelper.getAttachmentsFromUriList(context, uris)
    }

}

fun AttachmentMetaData.toAttachment(context: Context): SocialChatAttachment {
    val fileFromUri = AttachmentHelper().getCachedFileFromUri(context, this)
    return SocialChatAttachment(
        upload = fileFromUri,
        type = type,
        name = title ?: fileFromUri.name ?: "",
        fileSize = size.toInt(),
        mimeType = mimeType,
        originalWidth = width,
        originalHeight = height
    )
}