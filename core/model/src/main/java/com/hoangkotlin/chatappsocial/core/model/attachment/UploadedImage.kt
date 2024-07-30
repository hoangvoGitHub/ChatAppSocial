package com.hoangkotlin.chatappsocial.core.model.attachment

import java.util.Date

data class UploadedImage(
    val file: String,
    val createdAt: Date,
    val thumbUrl: String? = null,
)
