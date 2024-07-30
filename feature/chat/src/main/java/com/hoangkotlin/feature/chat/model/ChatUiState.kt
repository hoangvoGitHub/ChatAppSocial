package com.hoangkotlin.feature.chat.model

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel

sealed class ChatUiState() {
    data object Loading : ChatUiState()


    sealed class Success : ChatUiState() {
        abstract val channel: SocialChatChannel

        data class WithChannel(
            override val channel: SocialChatChannel
        ) : Success()

        data class WithUser(
            override val channel: SocialChatChannel
        ) : Success()
    }

    data class LoadFailed(val errorCode: DataResult.Error.Code) : ChatUiState()
}