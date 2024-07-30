package com.hoangkotlin.chatappsocial.core.model

import java.util.Date

data class ChannelMute (
    val channel: SocialChatChannel,
    val createdAt: Date,
    val expiredAt: Date,
    val updatedAt: Date
)