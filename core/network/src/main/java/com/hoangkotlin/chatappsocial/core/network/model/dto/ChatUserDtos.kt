package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class UpChatUserDto(
    val id: String,
    val firstName: String,
    val lastName: String,
    val imageUrl: String?,
    val isInvisible: Boolean?,
)

@Serializable
data class DownChatUserDto(
    val id: String,
    val name: String,
    val imageUrl: String?,
    val isOnline: Boolean?,
    val isInvisible: Boolean?,
    @Serializable(with = DateSerializer::class)
    val lastActiveAt: Date?,
    @Serializable(with = DateSerializer::class)
    val createdAt: Date?,
    @Serializable(with = DateSerializer::class)
    val updatedAt: Date? = null
)
