package com.hoangkotlin.chatappsocial.core.model.attachment

import java.io.File
import java.util.Date

data class SocialChatAttachment(
    val name: String? = null,
    val url: String? = null,
    val mimeType: String? = null,
    val imageUrl: String? = null,
    val thumbnailUrl: String? = null,
    val videoLength: Int? = null,
    val originalHeight: Int? = null,
    val originalWidth: Int? = null,
    val upload: File? = null,
    val uploadState: UploadState? = null,
    val type: AttachmentType? = null,
    val fileSize: Int = 0,
    val createdAt: Date? = null,
    val extraData: MutableMap<String, String> = mutableMapOf(),
)



