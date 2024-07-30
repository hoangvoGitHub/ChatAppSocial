package com.hoangkotlin.chatappsocial.core.data.repository.user

import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import kotlinx.coroutines.flow.Flow

interface ChatUserRepository {

    suspend fun insert(users: Collection<SocialChatUser>)

    suspend fun insert(user: SocialChatUser)

    suspend fun insertCurrentUser(user: SocialChatUser)

    suspend fun selectChatUser(uid: String): SocialChatUser?

    suspend fun selectChatUsers(uids: List<String>): List<SocialChatUser>

    suspend fun selectAllUsers(limit: Int, offset: Int): List<SocialChatUser>

    suspend fun observeChatUsersByName(
        queryName: String,
        limit: Int,
        offset: Int
    ): Flow<List<SocialChatUser>>

}