package com.hoangkotlin.chatappsocial.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import java.util.Date

@Entity(
    tableName = CHAT_MESSAGE_ENTITY
)
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val cid: String,
    val userId: String,
    val text: String,
    val replyTo: String? = null,
    val type: String? = null,
    val mimeType: String? = null,
    val syncStatus: SyncStatus = SyncStatus.COMPLETED,
    val createdAt: Date? = null,
    val createdLocallyAt: Date? = null,
    val updatedAt: Date? = null,
    val updatedLocallyAt: Date? = null,
    val deletedAt: Date? = null,
)


data class WrapperChatMessageEntity(
    @Embedded val chatMessageEntity: ChatMessageEntity,
    @Relation(entity = ChatAttachmentEntity::class, parentColumn = "id", entityColumn = "messageId")
    val attachments: List<ChatAttachmentEntity>,
)

const val CHAT_MESSAGE_ENTITY = "social_chat_message"