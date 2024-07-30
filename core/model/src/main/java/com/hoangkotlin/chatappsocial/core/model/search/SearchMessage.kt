package com.hoangkotlin.chatappsocial.core.model.search

import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage

data class SearchMessage(
    val channel: SocialChatChannel = SocialChatChannel(),
    val matchMessages: List<SocialChatMessage> = emptyList(),
    val matchCount: Int = 0
)
