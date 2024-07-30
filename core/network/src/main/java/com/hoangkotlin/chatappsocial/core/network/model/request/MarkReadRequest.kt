package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class MarkReadRequest(
    val messageId: String
)
