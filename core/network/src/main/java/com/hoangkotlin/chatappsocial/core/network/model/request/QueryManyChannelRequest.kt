package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class QueryManyChannelRequest(
    val filterCondition: Map<String, String> = emptyMap(),
    val sort: List<Map<String, String>> = emptyList(),
    val messageLimit: Int = 50,
    val memberLimit: Int = 50,
    val watch: Boolean = false,
    val offset: Int = 0,
    val limit: Int = 20,
)