package com.hoangkotlin.chatappsocial.core.model

import java.util.Date

data class SocialChatUser(
    val id: String = "",
    val name: String = "",
    val image: String = "",
    val isOnline: Boolean = false,
    val lastActiveAt: Date? = null,
    val isInvisible:Boolean = false,
    val createdAt: Date? = null,
    val channelMute: List<ChannelMute> = emptyList()
)

