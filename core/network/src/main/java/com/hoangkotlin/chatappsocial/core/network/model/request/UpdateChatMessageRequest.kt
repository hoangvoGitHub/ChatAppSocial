package com.hoangkotlin.chatappsocial.core.network.model.request

import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatMessageDto

data class UpdateChatMessageRequest(
    val message: DownChatMessageDto
)
