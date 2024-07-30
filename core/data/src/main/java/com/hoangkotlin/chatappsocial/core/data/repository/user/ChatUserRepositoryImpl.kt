package com.hoangkotlin.chatappsocial.core.data.repository.user

import com.hoangkotlin.chatappsocial.core.data.extension.asChatUserEntity
import com.hoangkotlin.chatappsocial.core.data.extension.asSocialChatUser
import com.hoangkotlin.chatappsocial.core.database.user.ChatUserDao
import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ChatUserRepositoryImpl @Inject constructor(
    private val userDao: ChatUserDao
) : ChatUserRepository {
    override suspend fun insert(users: Collection<SocialChatUser>) {
        userDao.insert(users.map(SocialChatUser::asChatUserEntity))
    }

    override suspend fun insert(user: SocialChatUser) {
        userDao.insert(user.asChatUserEntity())
    }

    override suspend fun insertCurrentUser(user: SocialChatUser) {
        TODO("Not yet implemented")
    }

    override suspend fun selectChatUser(uid: String): SocialChatUser? {
        return userDao.select(uid)?.asSocialChatUser()
    }

    override suspend fun selectChatUsers(uids: List<String>): List<SocialChatUser> {
        return userDao.select(uids).map(ChatUserEntity::asSocialChatUser)
    }

    override suspend fun selectAllUsers(limit: Int, offset: Int): List<SocialChatUser> {
        return userDao.selectAll(limit, offset).map(ChatUserEntity::asSocialChatUser)
    }

    override suspend fun observeChatUsersByName(
        queryName: String,
        limit: Int,
        offset: Int
    ): Flow<List<SocialChatUser>> {
        val searchKey = "%${queryName.replace(" ", "%")}%"

        return userDao.observeUserByName(searchKey, limit, offset)
            .map { it.map(ChatUserEntity::asSocialChatUser) }
    }
}