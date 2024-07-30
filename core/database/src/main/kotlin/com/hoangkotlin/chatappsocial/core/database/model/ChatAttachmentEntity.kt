package com.hoangkotlin.chatappsocial.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.util.Date

@Entity(
    tableName = CHAT_ATTACHMENT_ENTITY,
    foreignKeys = [
        ForeignKey(
            entity = ChatMessageEntity::class,
            parentColumns = ["id"],
            childColumns = ["messageId"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [Index("messageId")]
)
data class ChatAttachmentEntity(
    @PrimaryKey
    val id: String,
    val messageId: String,
    val url: String?,
    val imageUrl: String?,
    val thumbnailUrl: String?,
    val type: String?,
    val mimeType: String?,
    val fileSize: Int?,
    val name: String?,
    val uploadFilePath: String?,
    val originalHeight: Int?,
    val originalWidth: Int?,
    val createdAt: Date? = null,
    @Embedded
    val uploadState: UploadStateEntity? = null,
    val extraData: Map<String, String>,
) {
    companion object {
        const val EXTRA_DATA_ID_KEY = "id"
        fun generateId(messageId: String, index: Int): String {
            return "$messageId:$index"
        }
    }


}


data class UploadStateEntity(val statusCode: Int, val errorMessage: String?) {
    companion object {
        const val UPLOAD_STATE_SUCCESS = 1
        const val UPLOAD_STATE_IN_PROGRESS = 2
        const val UPLOAD_STATE_FAILED = 3
    }
}

const val CHAT_ATTACHMENT_ENTITY = "social_chat_attachment"