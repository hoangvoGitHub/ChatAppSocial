package com.hoangkotlin.chatappsocial.core.offline.interceptor

import android.content.Context
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.chat_client.extension.hasPendingAttachments
import com.hoangkotlin.chatappsocial.core.chat_client.extension.uploadId
import com.hoangkotlin.chatappsocial.core.chat_client.interceptor.SendMessageInterceptor
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.data.model.Result
import com.hoangkotlin.chatappsocial.core.data.repository.attachment.ChatAttachmentRepository
import com.hoangkotlin.chatappsocial.core.data.repository.message.ChatMessageRepository
import com.hoangkotlin.chatappsocial.core.data.repository.user.ChatUserRepository
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.offline.UploadAttachmentsWorker
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.filterNot
import java.util.Date
import java.util.UUID
import javax.inject.Inject


class DefaultSendMessageInterceptor @Inject constructor(
    private val chatClient: ChatClient,
    private val messageRepository: ChatMessageRepository,
    private val userRepository: ChatUserRepository,
    private val attachmentRepository: ChatAttachmentRepository,
    private val stateHolder: ChatStateHolder,
    @ApplicationContext private val context: Context,
) : SendMessageInterceptor {
    private val clientState = chatClient.clientState
    private var jobsMap: Map<String, Job> = emptyMap()
    private val uploadIds = mutableMapOf<String, UUID>()


    override suspend fun interceptMessage(
        channelId: String,
        message: SocialChatMessage
    ): Result<SocialChatMessage> {
        val preparedMessage = prepareMessage(channelId, message).let { result ->
            if (result is Result.Success) result.data else return Result.Error()
        }

        stateHolder.mutableChannel(channelId).upsertMessage(preparedMessage)

        messageRepository.insert(preparedMessage)
        userRepository.insert(preparedMessage.user)
        return if (preparedMessage.hasPendingAttachments) {
            uploadAttachments(preparedMessage, channelId)
        } else {
            Result.Success(preparedMessage)
        }
    }

    private suspend fun uploadAttachments(
        message: SocialChatMessage,
        channelId: String
    ): Result<SocialChatMessage> {
        return if (clientState.isNetworkAvailable) {
            waitForAttachmentsToBeSent(message, channelId)
        } else {
            enqueueAttachmentUpload(message, channelId)
            Result.Error(message = "Unavailable network")
        }
    }

    private fun enqueueAttachmentUpload(message: SocialChatMessage, channelId: String) {
        val uploadId = UploadAttachmentsWorker.start(context, channelId, message.id)
        uploadIds[message.id] = uploadId
    }

    private suspend fun waitForAttachmentsToBeSent(
        message: SocialChatMessage,
        channelId: String
    ): Result<SocialChatMessage> {
        var messageToBeSent = message
        var allAttachmentsUploaded = true
        jobsMap[message.id]?.cancel()

        jobsMap = jobsMap + (message.id to chatClient.launch {
            attachmentRepository.observeAttachmentsForMessage(message.id)
                .filterNot(List<SocialChatAttachment>::isEmpty)
                .collect { attachments ->
                    when {
                        attachments.all { it.uploadState == UploadState.Success } -> {
                            messageToBeSent =
                                messageRepository.getMessageById(message.id) ?: message.copy(
                                    attachments = attachments.toMutableList()
                                )
                            allAttachmentsUploaded = true
                            jobsMap[message.id]?.cancel()
                        }

                        attachments.any { it.uploadState is UploadState.Failed } -> {
                            jobsMap[message.id]?.cancel()
                        }

                        else -> Unit
                    }
                }
        }
                )
        enqueueAttachmentUpload(message, channelId)
        jobsMap[message.id]?.join()
        return if (allAttachmentsUploaded) {
            Result.Success(messageToBeSent)
        } else {
            Result.Error()
        }.also {
            uploadIds.remove(message.id)
        }
    }


    private fun prepareMessage(
        channelId: String,
        message: SocialChatMessage
    ): Result<SocialChatMessage> {
        val id = message.id.takeIf(String::isNotEmpty) ?: generateMessageId(message.user.id)

        val (attachmentsToUpload, nonFileAttachments)
                = message.attachments.partition { it.upload != null }


        val preparedAttachmentsToUpload = attachmentsToUpload.map { attachment ->
            if (attachment.uploadId == null) {
                attachment.uploadId = generateUploadId()
            }
            attachment.copy(
                uploadState = UploadState.Idle
            )

        }
        val preparedNonFileAttachments = nonFileAttachments.map { attachment ->
            attachment.copy(uploadState = UploadState.Success)
        }

        val createdLocallyAt = message.createdAt ?: message.createdLocallyAt ?: Date()

        val syncStatus = when {
            attachmentsToUpload.isNotEmpty() -> SyncStatus.AWAITING_ATTACHMENTS
            clientState.isNetworkAvailable -> SyncStatus.IN_PROGRESS
            else -> SyncStatus.SYNC_NEEDED
        }
        val user =
            clientState.user.value ?: return Result.Error(message = "User is not initialized")
        return Result.Success(
            message.copy(
                id = id,
                attachments = preparedAttachmentsToUpload + preparedNonFileAttachments,
                createdLocallyAt = createdLocallyAt,
                syncStatus = syncStatus,
                user = user
            )
        )
    }

    private fun generateMessageId(userId: String): String {
        return "$userId-${UUID.randomUUID()}"
    }

    private fun generateUploadId(): String {
        return "attachment_upload_id_${UUID.randomUUID()}"
    }
}