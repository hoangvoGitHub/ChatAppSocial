package com.hoangkotlin.chatappsocial.core.chat_client.uploader

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadedImage
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import java.io.File

interface FileUploader {

    suspend fun uploadProfileImage(
        file: File,
        callback: ProgressCallback,
    ): DataResult<UploadedImage>

    suspend fun sendImage(
        channelId: String,
        uploadId: String,
        file: File,
        callback: ProgressCallback?,
    ): DataResult<UploadedImage>


    suspend fun sendImage(
        channelId: String,
        uploadId: String,
        file: File,
    ): DataResult<UploadedImage>
}