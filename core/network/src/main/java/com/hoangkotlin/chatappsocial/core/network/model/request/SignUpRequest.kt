package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SignUpRequest(
    val username: String,
    val password: String,
    val firstName: String,
    val lastName: String
)
