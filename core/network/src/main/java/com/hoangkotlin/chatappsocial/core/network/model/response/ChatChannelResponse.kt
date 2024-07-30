package com.hoangkotlin.chatappsocial.core.network.model.response

import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatMemberReadDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatChannelDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatMessageDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.DownMembershipDto
import kotlinx.serialization.Serializable

@Serializable
data class ChatChannelResponse(
    val channel: DownChatChannelDto,
    val messages: List<DownChatMessageDto> = emptyList(),
    val members: List<DownMembershipDto> = emptyList(),
    val membership: DownMembershipDto,
    val unreadCount: Int = 0,
//    val reads: List<ChatMemberReadDto> = emptyList()
    // ///
)
