package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.util.Date


@Serializable
data class DownChatMessageDto(
    val id: String,
    @SerialName("chatChannel")
    val cid: String,
    val text: String?,
    @Serializable(with = DateSerializer::class)
    val createdAt: Date,
    @Serializable(with = DateSerializer::class)
    val updatedAt: Date? = null,
    @Serializable(with = DateSerializer::class)
    val deletedAt: Date? = null,
    val replyTo: DownChatMessageDto? = null,
    @SerialName("sentBy")
    val sentByUser: DownChatUserDto,
    val type: String?,
    val attachments: List<ChatAttachmentDto> = emptyList()
)

@Serializable
data class UpChatMessageDto(
    val id: String,
    val cid: String,
    val receiptId: String? = null,
    val text: String,
    val replyTo: String?,
    val attachments: List<ChatAttachmentDto> = emptyList()
)


fun UpChatMessageDto.toJson(): String {
    return Json.encodeToString(UpChatMessageDto.serializer(), this)
}

