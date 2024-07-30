package com.hoangkotlin.chatappsocial.core.chat_client.interceptor

import com.hoangkotlin.chatappsocial.core.data.model.Result
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage

interface SendMessageInterceptor : Interceptor {

    suspend fun interceptMessage(
        channelId: String,
        message: SocialChatMessage,
    ): Result<SocialChatMessage>
}