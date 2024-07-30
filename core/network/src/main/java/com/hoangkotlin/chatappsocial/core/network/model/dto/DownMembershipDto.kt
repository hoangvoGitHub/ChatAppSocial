package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date


@Serializable
data class UpMembershipDto(
    val id: String,
    val user: UpChatUserDto,
    val channelRole: String,
    val createdAt: String,
    val lastReadAt: String?
)

@Serializable
data class DownMembershipDto(
    @SerialName("chatUser")
    val user: DownChatUserDto,
    val nickname: String?,
    val channelRole: String,
    @Serializable(with = DateSerializer::class)
    val lastReadAt: Date?,
    @SerialName("lastReadMessage")
    val lastReadMessageId: String?,
    @Serializable(with = DateSerializer::class)
    val createdAt: Date,
    @Serializable(with = DateSerializer::class)
    val updatedAt: Date?
)