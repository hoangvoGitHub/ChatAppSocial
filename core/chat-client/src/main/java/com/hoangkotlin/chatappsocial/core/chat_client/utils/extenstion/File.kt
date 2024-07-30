package com.hoangkotlin.chatappsocial.core.chat_client.utils.extenstion

import android.webkit.MimeTypeMap
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import java.io.File

internal val File.getMimeType: String
    get() = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        ?: "application/octet-stream"

internal val File.mediaType: MediaType get() = getMimeType.toMediaType()