package com.hoangkotlin.chatappsocial.core.network.model.request

data class QueryChatMessagesRequest(
    val baseMessageId: String,
    val messageLimit: Int,
)