package com.hoangkotlin.chatappsocial.core.network.model.response

import kotlinx.serialization.Serializable
import retrofit2.http.HTTP

@Serializable
data class AuthenticationResponse(
    val status: Int,
    val message: String,
    val username: String,
    val token: String
)
