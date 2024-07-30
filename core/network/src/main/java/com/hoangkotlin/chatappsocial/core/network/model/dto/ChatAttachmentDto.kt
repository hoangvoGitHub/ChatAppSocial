package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ChatAttachmentDto(
    val name: String? = null,
    val url: String? = null,
    val mimeType: String? = null,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val videoLength: Int? = null,
    val originalHeight: Int? = null,
    val originalWidth: Int? = null,
    val type: String? = null,
    val fileSize: Int = 0,
    @Serializable(with = DateSerializer::class)
    val createdAt: Date,
    val extraData: Map<String, String> = mutableMapOf(),
)
