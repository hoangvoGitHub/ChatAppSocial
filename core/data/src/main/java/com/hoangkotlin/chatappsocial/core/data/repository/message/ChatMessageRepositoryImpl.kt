package com.hoangkotlin.chatappsocial.core.data.repository.message

import android.util.Log
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.data.extension.asSocialChatMessage
import com.hoangkotlin.chatappsocial.core.data.extension.asWrapperChatMessageEntity
import com.hoangkotlin.chatappsocial.core.database.message.ChatMessageDao
import com.hoangkotlin.chatappsocial.core.database.model.ChatMessageEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import com.hoangkotlin.chatappsocial.core.database.user.ChatUserDao
import java.util.Date
import javax.inject.Inject

class ChatMessageRepositoryImpl @Inject constructor(
    private val messageDao: ChatMessageDao,
    private val userDao: ChatUserDao,
) : ChatMessageRepository {

    private val getUser: suspend (userId: String) -> ChatUserEntity = {
        requireNotNull(userDao.select(it))
    }


    override suspend fun insert(messages: List<SocialChatMessage>) {
        if (messages.isEmpty()) return
        val messagesToInsert = messages.flatMap(Companion::retrieveAllMessage)
        return messageDao.insert(
            messagesToInsert.map(SocialChatMessage::asWrapperChatMessageEntity)
        )
    }

    override suspend fun insert(message: SocialChatMessage) {
        insert(listOf(message))
    }

    override suspend fun updateMessage(message: SocialChatMessage) {
        messageDao.updateMessageEntity(message.asWrapperChatMessageEntity().chatMessageEntity)
    }


    override suspend fun getMessagesForChannel(cid: String, limit: Int): List<SocialChatMessage> {
        return messageDao.selectMessagesForChannel(cid, limit).map { wrapper ->
            wrapper.asSocialChatMessage(
                getUser = getUser,
                getReplyMessage = ::getMessageById
            )
        }
    }

    override suspend fun getMessagesForChannelNewerThan(
        cid: String,
        limit: Int,
        date: Date
    ): List<SocialChatMessage> {
        return messageDao.selectMessagesForChannelNewerThan(cid, limit, date).map { wrapper ->
            wrapper.asSocialChatMessage(
                getUser = getUser,
                getReplyMessage = ::getMessageById,

                )
        }
    }

    override suspend fun getMessagesForChannelEqualOrNewerThan(
        cid: String,
        limit: Int,
        date: Date
    ): List<SocialChatMessage> {
        return messageDao.selectMessagesForChannelEqualOrNewerThan(cid, limit, date)
            .map { wrapper ->
                wrapper.asSocialChatMessage(
                    getUser = getUser,
                    getReplyMessage = ::getMessageById,
                )
            }
    }

    override suspend fun getMessagesForChannelOlderThan(
        cid: String,
        limit: Int,
        date: Date
    ): List<SocialChatMessage> {
        return messageDao.selectMessagesForChannelOlderThan(cid, limit, date).map { wrapper ->
            wrapper.asSocialChatMessage(
                getUser = getUser,
                getReplyMessage = ::getMessageById,
            )
        }
    }

    override suspend fun getMessagesForChannelEqualOrOlderThan(
        cid: String,
        limit: Int,
        date: Date
    ): List<SocialChatMessage> {
        return messageDao.selectMessagesForChannelEqualOrOlderThan(cid, limit, date)
            .map { wrapper ->
                wrapper.asSocialChatMessage(
                    getUser = getUser,
                    getReplyMessage = ::getMessageById,
                )
            }
    }

    override suspend fun getMessagesByIds(ids: List<String>): List<SocialChatMessage> {
        return messageDao.select(ids).map { wrapper ->
            wrapper.asSocialChatMessage(
                getUser = getUser,
                getReplyMessage = ::getMessageById,

                )
        }
    }

    override suspend fun getMessageById(id: String): SocialChatMessage? {
        return messageDao.select(id)?.asSocialChatMessage(getUser, ::getMessageById)
    }


    override suspend fun getMessagesBySyncStatus(
        syncStatus: SyncStatus,
        limit: Int
    ): List<SocialChatMessage> {
        return messageDao.selectBySyncStatus(syncStatus, limit)
            .map { wrapper ->
                wrapper.asSocialChatMessage(
                    getUser = getUser,
                    getReplyMessage = ::getMessageById,
                )
            }
    }

    override suspend fun deleteMessage(messageId: String) {
        messageDao.deleteMessage(messageId)
    }

    override suspend fun deleteAll() {
        messageDao.deleteAll()
    }

    private companion object {
        private const val DEFAULT_MESSAGE_LIMIT = 100

        private const val TAG = "ChatMessageRepositoryIm"

        private fun retrieveAllMessage(message: SocialChatMessage): List<SocialChatMessage> =
            listOf(message) + (message.replyTo?.let(Companion::retrieveAllMessage).orEmpty())
    }
}