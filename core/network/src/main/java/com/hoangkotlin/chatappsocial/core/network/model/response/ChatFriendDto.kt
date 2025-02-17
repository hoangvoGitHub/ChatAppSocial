package com.hoangkotlin.chatappsocial.core.network.model.response

import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatUserDto
import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ChatFriendDto(
    val id: String,
    val user: DownChatUserDto,
    val status: String,
    val channelId: String?,
    @Serializable(with = DateSerializer::class)
    val createdAt: Date
)
