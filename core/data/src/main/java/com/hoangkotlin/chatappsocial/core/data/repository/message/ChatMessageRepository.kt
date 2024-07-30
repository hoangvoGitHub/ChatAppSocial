package com.hoangkotlin.chatappsocial.core.data.repository.message

import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import java.util.Date

interface ChatMessageRepository {

    suspend fun insert(messages: List<SocialChatMessage>)

    suspend fun insert(message: SocialChatMessage)

    suspend fun updateMessage(message: SocialChatMessage)

    suspend fun getMessagesForChannel(
        cid: String, limit: Int
    ): List<SocialChatMessage>

    suspend fun getMessagesForChannelNewerThan(
        cid: String, limit: Int, date: Date
    ): List<SocialChatMessage>


    suspend fun getMessagesForChannelEqualOrNewerThan(
        cid: String, limit: Int, date: Date
    ): List<SocialChatMessage>

    suspend fun getMessagesForChannelOlderThan(
        cid: String, limit: Int, date: Date
    ): List<SocialChatMessage>

    suspend fun getMessagesForChannelEqualOrOlderThan(
        cid: String, limit: Int, date: Date
    ): List<SocialChatMessage>


    suspend fun getMessagesByIds(ids: List<String>): List<SocialChatMessage>

    suspend fun getMessageById(id: String): SocialChatMessage?

    suspend fun getMessagesBySyncStatus(
        syncStatus: SyncStatus,
        limit: Int
    ): List<SocialChatMessage>

    suspend fun deleteMessage(messageId: String)

    suspend fun deleteAll()


}