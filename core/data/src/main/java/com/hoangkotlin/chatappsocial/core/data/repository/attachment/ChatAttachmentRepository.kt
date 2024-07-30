package com.hoangkotlin.chatappsocial.core.data.repository.attachment

import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import kotlinx.coroutines.flow.Flow

interface ChatAttachmentRepository {

    fun observeAttachmentsForMessage(messageId: String): Flow<List<SocialChatAttachment>>

    suspend fun deleteAll()

}