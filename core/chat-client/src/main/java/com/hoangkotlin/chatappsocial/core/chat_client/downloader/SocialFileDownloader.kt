package com.hoangkotlin.chatappsocial.core.chat_client.downloader

import com.hoangkotlin.chatappsocial.core.chat_client.extension.isImage
import com.hoangkotlin.chatappsocial.core.data.model.Result
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.network.downloader.Downloader
import java.util.UUID
import javax.inject.Inject

class SocialFileDownloader @Inject constructor(
    private val downloader: Downloader
) : FileDownloader {

    override suspend fun downloadFile(attachment: SocialChatAttachment): Result<Unit> {
        if (attachment.isImage) {
            val downloadId = downloader.downloadFile(
                attachment.imageUrl!!,
                attachment.name ?: UUID.randomUUID().toString(),
                attachment.mimeType!!
            )
            if (downloadId == -1L) return Result.Success(Unit)
            return Result.Error()
        }
        return Result.Error()
    }
}