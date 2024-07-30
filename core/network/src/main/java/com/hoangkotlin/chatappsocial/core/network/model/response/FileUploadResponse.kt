package com.hoangkotlin.chatappsocial.core.network.model.response

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class FileUploadResponse(
    val file: String,
    @Serializable(with = DateSerializer::class)
    val createdAt: Date,
    val thumbnailUrl: String?,
)