package com.hoangkotlin.chatappsocial.core.chat_client.downloader

import com.hoangkotlin.chatappsocial.core.data.model.Result
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment

interface FileDownloader {
    suspend fun downloadFile(attachment: SocialChatAttachment): Result<Unit>
}