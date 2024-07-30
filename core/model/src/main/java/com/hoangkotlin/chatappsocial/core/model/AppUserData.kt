package com.hoangkotlin.chatappsocial.core.model

data class AppUserData(
    val currentUser: ChatAppUser? = null,
    val appUsers: Map<String,ChatAppUser> = emptyMap()
)

data class ChatAppUser(
    val username: String,
    val password: String? = null,
    val imagePath: String? = null,
    val token: String? = null,
    val chatUserId: String? =null
)
