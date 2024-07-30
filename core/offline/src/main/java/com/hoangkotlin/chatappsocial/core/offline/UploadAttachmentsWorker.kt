package com.hoangkotlin.chatappsocial.core.offline

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.hoangkotlin.chatappsocial.core.chat_client.extension.hasPendingAttachments
import com.hoangkotlin.chatappsocial.core.chat_client.extension.uploadId
import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.data.repository.message.ChatMessageRepository
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.network.utils.ProgressCallback
import com.hoangkotlin.chatappsocial.core.chat_client.uploader.AttachmentUploadHelper
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.UUID

@HiltWorker
class UploadAttachmentsWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    private val attachmentUploadHelper: AttachmentUploadHelper,
    private val messageRepository: ChatMessageRepository,
    private val stateHolder: ChatStateHolder,
) : CoroutineWorker(appContext, workerParams) {
    override suspend fun doWork(): Result {
        val channelId = inputData.getString(DATA_CHANNEL_ID)!!
        val messageId = inputData.getString(DATA_MESSAGE_ID)!!

        val message = messageRepository.getMessageById(messageId) ?: return Result.failure()

        return sendAttachments(channelId, message)
    }


    private suspend fun sendAttachments(channelId: String, message: SocialChatMessage): Result {
        if (!message.hasPendingAttachments) return Result.success()

        val attachmentAfterUploaded = uploadAttachments(channelId, message)
        updateMessage(
            message.copy(
                attachments = attachmentAfterUploaded
            )
        )
        return if (attachmentAfterUploaded.all { it.uploadState is UploadState.Success }) {
            Result.success()
        } else {
            Result.failure()
        }

    }

    private suspend fun updateMessage(message: SocialChatMessage) {
        messageRepository.deleteMessage(message.id)
        messageRepository.insert(message)
    }

    private suspend fun uploadAttachments(
        channelId: String,
        message: SocialChatMessage
    ): List<SocialChatAttachment> {
        return try {
            message.attachments.map { attachment ->
                var recoveredAttachment: SocialChatAttachment = attachment

                val progressCallback = ProgressCallbackImpl(
                    messageId = message.id,
                    uploadId = attachment.uploadId!!,
                    mutableState = stateHolder.mutableChannel(channelId)
                )

                val result = attachmentUploadHelper.uploadAttachment(
                    channelId, attachment, callback = progressCallback,
                    onRecoverFailedUpload = {
                        recoveredAttachment = it
                    }
                )
                if (result is DataResult.Success) {
                    result.data
                } else {
                    recoveredAttachment
                }
            }
        } catch (e: Exception) {
            message.attachments.map { attachment ->
                if (attachment.uploadState != UploadState.Success) {
                    attachment.copy(
                        uploadState = UploadState.Failed(
                            e.message ?: "Unknown message"
                        )
                    )
                } else attachment
            }
        }
    }

    private class ProgressCallbackImpl(
        private val messageId: String,
        private val uploadId: String,
        private val mutableState: com.hoangkotlin.chatappsocial.core.offline.state.messages.ChatChannelMutableState,
    ) : ProgressCallback {
        override fun onSuccess(url: String) {
            updateAttachmentUploadState(messageId, uploadId, UploadState.Success)
        }

        override fun onError(error: Throwable) {
            updateAttachmentUploadState(
                messageId,
                uploadId,
                UploadState.Failed(error.message ?: "Unknown error")
            )
        }

        override fun onProgress(bytesUploaded: Long, totalBytes: Long) {
            updateAttachmentUploadState(
                messageId,
                uploadId,
                UploadState.InProgress(bytesUploaded, totalBytes)
            )
        }

        private fun updateAttachmentUploadState(
            messageId: String,
            uploadId: String,
            newState: UploadState
        ) {
            val message = mutableState.messages.value.firstOrNull { it.id == messageId }
            if (message != null) {
                val newAttachments = message.attachments.map { attachment ->
                    if (attachment.uploadId == uploadId) {
                        attachment.copy(uploadState = newState)
                    } else {
                        attachment
                    }
                }
                val updatedMessage = message.copy(attachments = newAttachments.toMutableList())
                mutableState.upsertMessage(updatedMessage)
            }
        }
    }


    companion object {
        private const val DATA_MESSAGE_ID = "message_id"
        private const val DATA_CHANNEL_ID = "channel_id"


        fun start(
            context: Context,
            channelId: String,
            messageId: String
        ): UUID {
            val uploadAttachmentsWorkRequest = OneTimeWorkRequestBuilder<UploadAttachmentsWorker>()
                .setConstraints(
                    Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build()
                ).setInputData(
                    workDataOf(
                        DATA_MESSAGE_ID to messageId,
                        DATA_CHANNEL_ID to channelId
                    )
                ).build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "$channelId$messageId",
                ExistingWorkPolicy.KEEP,
                uploadAttachmentsWorkRequest
            )
            return uploadAttachmentsWorkRequest.id
        }
    }

}