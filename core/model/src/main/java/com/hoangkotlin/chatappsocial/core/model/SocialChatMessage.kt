package com.hoangkotlin.chatappsocial.core.model

import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import java.util.Date

data class SocialChatMessage(
    val id: String = "",
    val cid: String = "",
    val text: String = "",
    val attachments: List<SocialChatAttachment> = emptyList(),
    val replyTo: SocialChatMessage? = null,
    val replyToId: String? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val deletedAt: Date? = null,
    val updatedLocallyAt: Date? = null,
    val createdLocallyAt: Date? = null,
    val user: SocialChatUser = SocialChatUser(),
    val isPinned: Boolean = false,
    val pinnedAt: Date? = null,
    val pinnedBy: SocialChatUser? = null,
    val syncStatus: SyncStatus = SyncStatus.COMPLETED
)

fun SocialChatMessage.getCreatedAtOrThrow(): Date {
    val created = createdAt ?: createdLocallyAt
    return checkNotNull(created) { "a message needs to have a non null value for either createdAt or createdLocallyAt" }
}

/**
 * @return when the message was created or null.
 */
fun SocialChatMessage.getCreatedAtOrNull(): Date? {
    return createdAt ?: createdLocallyAt
}
