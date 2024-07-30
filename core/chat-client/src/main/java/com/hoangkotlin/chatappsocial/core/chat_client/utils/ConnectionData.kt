package com.hoangkotlin.chatappsocial.core.chat_client.utils

import com.hoangkotlin.chatappsocial.core.model.SocialChatUser

data class ConnectionData(
    val user: SocialChatUser,
    val token: String = ""
)
