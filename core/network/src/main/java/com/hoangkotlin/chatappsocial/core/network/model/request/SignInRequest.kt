package com.hoangkotlin.chatappsocial.core.network.model.request

import kotlinx.serialization.Serializable

@Serializable
data class SignInRequest(
    val username: String,
    val password: String
)
