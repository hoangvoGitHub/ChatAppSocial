package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class QueryChatUsersRequest(
    val filterCondition: Map<String, String>,
    val sort: List<Map<String, String>>,
    val offset: Int = 0,
    val limit: Int = 20,
)
