package com.hoangkotlin.chatappsocial.core.model

import android.net.Uri
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import java.io.File

data class AttachmentMetaData(
    val uri: Uri?,
    val type: AttachmentType?,
    val mimeType: String?,
    val title: String?,
    val size: Long = 0L,
    val width: Int? = null,
    val height: Int? = null,
    val file: File? = null
)




