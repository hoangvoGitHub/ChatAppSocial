package com.hoangkotlin.chatappsocial.core.network.model.dto

import com.hoangkotlin.chatappsocial.core.network.utils.DateSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class DownChatChannelDto(
    val id: String,
    val type: String?,
    val name: String?,
    @SerialName("createdByUser")
    val createdByUserId: String,
    @Serializable(with = DateSerializer::class)
    val createdAt: Date,
    @Serializable(with = DateSerializer::class)
    val updatedAt: Date?,
//    @SerialName("chatMessages")
//    val messages: List<DownChatMessageDto> = emptyList(),
    val imageUrl: String?,

//    val memberships: List<DownMembershipDto> = emptyList(),
)