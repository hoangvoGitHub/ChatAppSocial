package com.hoangkotlin.chatappsocial.core.model

import java.util.Date

data class SocialChannelRead(
    val user: SocialChatUser = SocialChatUser(),
    val lastReadMessageId: String? = null,
    val lastReadAt: Date? = null,
    val unreadMessages: Int = 0
)
