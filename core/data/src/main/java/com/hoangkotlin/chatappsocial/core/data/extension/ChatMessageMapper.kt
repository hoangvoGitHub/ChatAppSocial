package com.hoangkotlin.chatappsocial.core.data.extension

import com.hoangkotlin.chatappsocial.core.database.model.ChatAttachmentEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatMessageEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import com.hoangkotlin.chatappsocial.core.database.model.WrapperChatMessageEntity
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage


fun SocialChatMessage.asWrapperChatMessageEntity(): WrapperChatMessageEntity {
    return WrapperChatMessageEntity(
        chatMessageEntity = this.asChatMessageEntity(),
        attachments = attachments.mapIndexed { index, attachment ->
            attachment.asChatAttachmentEntity(id, index)
        }
    )
}

suspend fun WrapperChatMessageEntity.asSocialChatMessage(
    getUser: suspend (userId: String) -> ChatUserEntity,
    getReplyMessage: suspend (replyId: String) -> SocialChatMessage?,
): SocialChatMessage {
    return chatMessageEntity.asSocialChatMessage(
        getUser, getReplyMessage, attachments
    )
}

suspend fun ChatMessageEntity.asSocialChatMessage(
    getUser: suspend (userId: String) -> ChatUserEntity,
    getReplyMessage: suspend (replyId: String) -> SocialChatMessage?,
    attachments: List<ChatAttachmentEntity>,
): SocialChatMessage {
    return SocialChatMessage(
        id = id, cid = cid, text = text,
        user = getUser(userId).asSocialChatUser(),
        replyTo = replyTo?.let { getReplyMessage(it) }

//        getReplyMessage(replyTo)?.asSocialChatMessage(
//            getUser, getReplyMessage, attachments
//        )

        ,
        attachments = attachments.map(ChatAttachmentEntity::asSocialAttachment)
    )
}

private fun SocialChatMessage.asChatMessageEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        cid = cid,
        userId = user.id,
        text = text,
        replyTo = replyTo?.id,
        createdAt = createdAt,
        syncStatus = syncStatus,
        updatedAt = updatedAt,
        createdLocallyAt = createdLocallyAt,
        deletedAt = deletedAt,
    )
}

