package com.hoangkotlin.chatappsocial.core.chat_client.utils

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import com.hoangkotlin.chatappsocial.core.model.AttachmentMetaData
import com.hoangkotlin.chatappsocial.core.model.attachment.AttachmentType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class AttachmentHelper {
    private val dateFormat = SimpleDateFormat(TIME_FORMAT, Locale.US)

    fun getCachedFileFromUri(
        context: Context,
        attachmentMetaData: AttachmentMetaData,
    ): File {
        if (attachmentMetaData.file == null && attachmentMetaData.uri == null) {
            throw IllegalStateException(
                "Either file or URI cannot be null."
            )
        }
        if (attachmentMetaData.file != null) {
            return attachmentMetaData.file!!
        }
        val cachedFile =
            File(getUniqueCacheFolder(context), attachmentMetaData.getTitleWithExtension())
        context.contentResolver.openInputStream(attachmentMetaData.uri!!)?.use { inputStream ->
            cachedFile.outputStream().use {
                inputStream.copyTo(it)
            }
        }

        return cachedFile
    }

    fun getAttachmentsFromUriList(context: Context, uris: List<Uri>): List<AttachmentMetaData> {
        return uris.mapNotNull { uri ->
            val columns = arrayOf(
                MediaStore.Files.FileColumns.DISPLAY_NAME,
                MediaStore.Files.FileColumns.MIME_TYPE,
                MediaStore.Files.FileColumns.SIZE,
            )
            context.contentResolver.query(
                /* uri = */  uri,
                /* projection = */ columns,
                /* selection = */ null,
                /* selectionArgs = */ null,
                /* sortOrder = */ null
            )
                ?.use { cursor ->
                    if (cursor.moveToFirst()) {
                        val displayNameIndex =
                            cursor.getColumnIndex(MediaStore.Files.FileColumns.DISPLAY_NAME)

                        val fileSizeIndex = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)

                        val mimeTypeIndex =
                            cursor.getColumnIndex(MediaStore.Files.FileColumns.MIME_TYPE)

                        val displayName =
                            if (displayNameIndex != -1 && !cursor.isNull(displayNameIndex)) {
                                cursor.getString(displayNameIndex)
                            } else null

                        val fileSize = if (fileSizeIndex != -1 && !cursor.isNull(fileSizeIndex)) {
                            cursor.getLong(fileSizeIndex)
                        } else 0L

                        val mimeType = if (mimeTypeIndex != -1 && !cursor.isNull(mimeTypeIndex)) {
                            cursor.getString(mimeTypeIndex)
                        } else {
                            context.contentResolver.getType(uri)
                        }

                        AttachmentMetaData(
                            uri = uri,
                            type = getModelType(mimeType),
                            mimeType = mimeType,
                            title = displayName,
                            size = fileSize,
                        )
                    } else {
                        null
                    }
                }
        }
    }

    private fun getUniqueCacheFolder(context: Context): File =
        File(context.cacheDir, "$FILE_NAME_PREFIX${dateFormat.format(Date().time)}").also {
            it.mkdirs()
        }


    private fun getModelType(mimeType: String?): AttachmentType {
        return when {
            isImage(mimeType) -> AttachmentType.Image
            isVideo(mimeType) -> AttachmentType.Video
            else -> AttachmentType.File
        }
    }

    private fun isImage(mimeType: String?): Boolean {
        return mimeType?.startsWith("image") ?: false
    }

    private fun isVideo(mimeType: String?): Boolean {
        return mimeType?.startsWith("video") ?: false
    }

    companion object {
        const val TIME_FORMAT: String = "HHmmssSSS"
        const val FILE_NAME_PREFIX: String = "SOCIAL_"
        private const val MILLISECOND_IN_A_SECOND = 1000
    }

}

private fun AttachmentMetaData.getTitleWithExtension(): String {
    val extension = title?.substringAfterLast('.')
    val newTitle = title
        ?.replace(" ", "_")
        ?.replace("(", "_")
        ?.replace(")", "_")
    return if (extension.isNullOrEmpty() && !mimeType.isNullOrEmpty()) {
        "$newTitle.${MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)}"
    } else {
        newTitle ?: ""
    }
}