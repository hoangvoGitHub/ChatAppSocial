package com.hoangkotlin.chatappsocial.core.chat_client.uploader

import com.hoangkotlin.chatappsocial.core.chat_client.utils.extenstion.mediaType
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadedImage
import com.hoangkotlin.chatappsocial.core.network.api.ChatFileApi
import com.hoangkotlin.chatappsocial.core.network.model.response.FileUploadResponse
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

class SocialFileUploader @Inject constructor(
    private val fileApi: ChatFileApi
) : FileUploader {
    override suspend fun uploadProfileImage(
        file: File,
        callback: ProgressCallback
    ): DataResult<UploadedImage> {
        return try {
            val body = file.asRequestBody(file.mediaType)
            val part = MultipartBody.Part.createFormData("file", file.name, body)
            val response = fileApi.uploadProfileImage(part, callback)
            if (response.isSuccessful && response.body() != null) {
                DataResult.Success.Network(response.body()!!.asUploadedImage())
                    .also { callback.onSuccess(it.data.file) }
            } else {
                DataResult.Error("Unknown Error").also {
                    callback.onError(Throwable("Unknown Error"))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            callback.onError(e)
            DataResult.Error("Unknown Message")
        }
    }

    override suspend fun sendImage(
        channelId: String,
        uploadId: String,
        file: File,
        callback: ProgressCallback?
    ): DataResult<UploadedImage> {
        if (callback == null) {
            return sendImage(channelId, uploadId, file)
        }
        return try {
            val body = file.asRequestBody(file.mediaType)
            val part = MultipartBody.Part.createFormData("file", file.name, body)
            val response = fileApi.sendImage(channelId, uploadId, part, callback)
            if (response.isSuccessful && response.body() != null) {
                DataResult.Success.Network(response.body()!!.asUploadedImage())
            } else {
                DataResult.Error("Unknown Message")
            }
        } catch (e: Exception) {
            DataResult.Error("Unknown Message")
        }
    }


    override suspend fun sendImage(
        channelId: String,
        uploadId: String,
        file: File
    ): DataResult<UploadedImage> {
        return try {
            val body = file.asRequestBody(file.mediaType)
            val part = MultipartBody.Part.createFormData("file", file.name, body)
            val response = fileApi.sendImage(channelId, uploadId, part)
            if (response.isSuccessful && response.body() != null) {
                DataResult.Success.Network(response.body()!!.asUploadedImage())
            } else {
                DataResult.Error("Unknown Message")
            }
        } catch (e: Exception) {
            DataResult.Error("Unknown Message")
        }
    }
}

fun FileUploadResponse.asUploadedImage(): UploadedImage {
    return UploadedImage(
        file = file, thumbUrl = thumbnailUrl,
        createdAt = createdAt
    )
}