package com.hoangkotlin.chatappsocial.core.data.repository.channel

import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus

interface ChatChannelRepository {

    suspend fun insertChatChannel(channel:SocialChatChannel)

    suspend fun insertChannels(channels: Collection<SocialChatChannel>)

    suspend fun deleteChannel(cid: String)

    suspend fun selectChannelWithoutMessage(cid: String): SocialChatChannel

    suspend fun selectChannel(cid: String): SocialChatChannel

    suspend fun selectAll(): List<SocialChatChannel>

    suspend fun selectChannels(cids: List<String>): List<SocialChatChannel>

    suspend fun selectChannelsBySyncStatus(syncStatus: SyncStatus, limit: Int = NO_LIMIT)


    suspend fun selectChannelCidsBySyncStatus(limit: Int = NO_LIMIT)

    suspend fun selectMembersForChannel(cid: String): List<SocialChatMember>

    suspend fun updateMembersForChannel(cid: String, members: List<SocialChatMember>)

    suspend fun updateLastMessageForChannel(cid: String, lastMessage: SocialChatMessage)

    private companion object {
        private const val NO_LIMIT: Int = -1
    }
}