package com.hoangkotlin.chatappsocial.core.model

import java.util.Date

data class SocialChatMember(
    val id: String = "",
    val user: SocialChatUser,
    val nickname: String? = null,
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val inviteAcceptedAt: Date? = null,
    val inviteRejectedAt: Date? = null,
    val shadowBanned: Boolean = false,
    val channelRole: String? = null,
    val mute: Boolean = false
)
