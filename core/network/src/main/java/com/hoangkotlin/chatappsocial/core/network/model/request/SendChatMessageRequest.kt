package com.hoangkotlin.chatappsocial.core.network.model.request

import com.hoangkotlin.chatappsocial.core.network.model.dto.UpChatMessageDto
import kotlinx.serialization.Serializable

@Serializable
data class SendChatMessageRequest(
    val message: UpChatMessageDto
)
