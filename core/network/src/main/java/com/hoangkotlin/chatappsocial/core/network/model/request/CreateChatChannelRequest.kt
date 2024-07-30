package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateChatChannelRequest (
    val name: String? = null,
    val members: List<String> = emptyList(),
    val message: String? = null,
    val type: String
    )