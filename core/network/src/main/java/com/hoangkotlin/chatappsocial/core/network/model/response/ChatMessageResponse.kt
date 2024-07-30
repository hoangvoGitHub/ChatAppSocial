package com.hoangkotlin.chatappsocial.core.network.model.response

import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatMessageDto
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponse(
    @SerialName("chatMessage")
    val message: DownChatMessageDto
)
