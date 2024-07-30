package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class ChatFriendRequest(
    val recipientId: String
)
