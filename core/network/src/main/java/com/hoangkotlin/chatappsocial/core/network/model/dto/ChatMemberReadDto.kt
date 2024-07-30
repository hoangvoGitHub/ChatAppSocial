package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class ChatMemberReadDto (
    private val user: DownChatUserDto? = null,
    private val lastReadMessageId: String? = null,
    @Serializable(with = DateSerializer::class)
    private val lastReadAt: Date? = null,
    private val unreadCount: Int = 0
)

