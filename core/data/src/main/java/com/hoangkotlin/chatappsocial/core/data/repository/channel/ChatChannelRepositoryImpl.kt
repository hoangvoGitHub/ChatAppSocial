package com.hoangkotlin.chatappsocial.core.data.repository.channel

import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.database.channel.ChatChannelDao
import javax.inject.Inject

class ChatChannelRepositoryImpl @Inject constructor(
    private val channelDao: ChatChannelDao
) : ChatChannelRepository {
    override suspend fun insertChatChannel(channel: SocialChatChannel) {
        TODO("Not yet implemented")
    }

    override suspend fun insertChannels(channels: Collection<SocialChatChannel>) {
        TODO("Not yet implemented")
    }

    override suspend fun deleteChannel(cid: String) {
        TODO("Not yet implemented")
    }

    override suspend fun selectChannelWithoutMessage(cid: String): SocialChatChannel {
        TODO("Not yet implemented")
    }

    override suspend fun selectChannel(cid: String): SocialChatChannel {
        TODO("Not yet implemented")
    }

    override suspend fun selectAll(): List<SocialChatChannel> {
        TODO("Not yet implemented")
    }

    override suspend fun selectChannels(cids: List<String>): List<SocialChatChannel> {
        TODO("Not yet implemented")
    }

    override suspend fun selectChannelsBySyncStatus(syncStatus: SyncStatus, limit: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun selectChannelCidsBySyncStatus(limit: Int) {
        TODO("Not yet implemented")
    }

    override suspend fun selectMembersForChannel(cid: String): List<SocialChatMember> {
        TODO("Not yet implemented")
    }

    override suspend fun updateMembersForChannel(cid: String, members: List<SocialChatMember>) {
        TODO("Not yet implemented")
    }

    override suspend fun updateLastMessageForChannel(cid: String, lastMessage: SocialChatMessage) {
        TODO("Not yet implemented")
    }
}