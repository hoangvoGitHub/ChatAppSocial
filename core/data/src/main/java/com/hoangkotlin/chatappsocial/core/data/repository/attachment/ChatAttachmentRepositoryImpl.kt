package com.hoangkotlin.chatappsocial.core.data.repository.attachment

import com.hoangkotlin.chatappsocial.core.data.extension.asSocialAttachment
import com.hoangkotlin.chatappsocial.core.database.attachment.ChatAttachmentDao
import com.hoangkotlin.chatappsocial.core.database.model.ChatAttachmentEntity
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatAttachmentRepositoryImpl @Inject constructor(
    private val attachmentDao: ChatAttachmentDao
) : ChatAttachmentRepository {
    override fun observeAttachmentsForMessage(messageId: String): Flow<List<SocialChatAttachment>> {
        return attachmentDao.observeAttachmentsForMessage(messageId).map { attachments ->
            attachments.map(ChatAttachmentEntity::asSocialAttachment)
        }
    }

    override suspend fun deleteAll() {
        attachmentDao.deleteAll()
    }
}