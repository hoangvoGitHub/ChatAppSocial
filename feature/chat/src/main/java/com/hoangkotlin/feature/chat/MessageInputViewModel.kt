package com.hoangkotlin.feature.chat

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.feature.chat.navigation.channelArg
import com.hoangkotlin.feature.chat.utils.ChatAttachmentHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject


@HiltViewModel
class MessageInputViewModel @Inject constructor(
    private val chatClient: ChatClient,
    private val attachmentHelper: ChatAttachmentHelper,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val currentUser = chatClient.clientState.user

    private val _messageInputState = MutableStateFlow(
        MessageInputState(
            currentUser = currentUser.value
        )
    )
    val messageInputState = _messageInputState.asStateFlow()

    private val channelId: String? = savedStateHandle.get<String>(channelArg);

    fun onMessageChange(value: String) {
        _messageInputState.update {
            it.copy(inputValue = value)
        }
    }

    fun sendMessage() {
        if (_messageInputState.value.inputValue.isEmpty()) return
        viewModelScope.launch(SupervisorJob()) {

            val message = buildMessage()
            clearState()
            chatClient.sendMessage(channelId!!, message)

        }
    }

    fun setInputAction(action: MessageAction?) {
        if (action != null && action is Reply &&
            _messageInputState.value.action is Reply
        ) {
            return
        }
        _messageInputState.update { currentState ->
            currentState.copy(action = action)
        }
    }

    fun addSelectedAttachments(attachmentUris: List<Uri>) {
        val attachments = attachmentHelper.getAttachmentsFromUris(attachmentUris)
        _messageInputState.update { currentState ->
            currentState.copy(attachments = currentState.attachments.toMutableList().apply {
                addAll(attachments)
            })
        }
    }


    private fun buildMessage(): SocialChatMessage {
        val currentInputState = _messageInputState.value
        val trimmedMessage = currentInputState.inputValue.trim()
        val replyMessage = (currentInputState.action as? Reply)?.message
        val attachments = currentInputState.attachments

        return SocialChatMessage(
            id = UUID.randomUUID().toString(),
            text = trimmedMessage,
            cid = channelId!!,
            createdAt = Date(),
            replyTo = replyMessage,
            replyToId = replyMessage?.id,
            syncStatus = SyncStatus.IN_PROGRESS,
            attachments = attachments
        )
    }

    private fun clearState() {
        _messageInputState.update {
            it.copy(
                inputValue = "",
                action = null,
                attachments = emptyList()
            )
        }
    }

    fun removeAttachment(socialAttachment: SocialChatAttachment) {
        _messageInputState.update { currentState ->
            currentState.copy(attachments = currentState.attachments.toMutableList().apply {
                remove(socialAttachment)
            })
        }
    }

}

data class MessageInputState(
    val inputValue: String = "",
    val action: MessageAction? = null,
    val currentUser: SocialChatUser? = null,
    val attachments: List<SocialChatAttachment> = emptyList()
)

sealed class MessageAction {
    abstract val message: SocialChatMessage
}

class Reply(
    override val message: SocialChatMessage
) : MessageAction()