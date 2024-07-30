package com.hoangkotlin.chatappsocial.core.chat_client.api

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.Device
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatFriend
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatUsersRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SendChatMessageRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.UpdateChatMessageRequest

interface ChatApi {

//    suspend fun uploadProfileImage(
//        file: File,
//        callback: ProgressCallback
//    ): DataResult<UploadedImage>

//    suspend fun sendFile(
//        channelId: String,
//        file: File,
//        callback: ProgressCallback? = null,
//    ): DataResult<UploadedImage>
//
//    suspend fun sendImage(
//        channelId: String,
//        file: File,
//        callback: ProgressCallback? = null,
//    ): DataResult<UploadedImage>

    suspend fun sendMessage(
        channelId: String,
        request: SendChatMessageRequest
    ): DataResult<SocialChatMessage>

    suspend fun createChannel(
        name: String? = null,
        message: String? = null,
        memberIds: List<String>,
        type: String,
    ): DataResult<SocialChatChannel>

    suspend fun queryMessage(messageId: String): DataResult<SocialChatMessage>

    suspend fun updateMessage(
        messageId: String,
        request: UpdateChatMessageRequest
    ): DataResult<SocialChatMessage>

    suspend fun queryChannels(
        queryRequest: QueryManyChannelRequest
    ): DataResult<List<SocialChatChannel>>

    suspend fun queryChannel(
        channelId: String,
        queryRequest: QueryChatChannelRequest = QueryChatChannelRequest()
    ): DataResult<SocialChatChannel>

    suspend fun deleteChannel(
        channelId: String
    ): DataResult<SocialChatChannel>

    suspend fun queryChatUsers(
        request: QueryChatUsersRequest
    ): DataResult<List<SocialChatUser>>

    suspend fun queryChatUser(
        userId: String
    ): DataResult<SocialChatUser>

    suspend fun queryChatUser(): DataResult<SocialChatUser>

    suspend fun queryChatUserByUsername(username: String): DataResult<SocialChatUser>

    suspend fun queryFriend(
        name: String = "",
        limit: Int = 20,
        offset: Int = 0,
        status: String,
        sortBy: String? = null,
    ): DataResult<List<SocialChatFriend>>

    suspend fun acceptFriend(
        friendUserId: String
    ): DataResult<SocialChatFriend>

    suspend fun removeFriend(
        friendUserId: String
    ): DataResult<SocialChatFriend>

    suspend fun addFriend(
        friendUserId: String
    ): DataResult<SocialChatFriend>

    suspend fun rejectFriend(
        friendUserId: String
    ): DataResult<SocialChatFriend?>


    suspend fun markRead(
        channelId: String,
        messageId: String,
    ): DataResult<Unit>

    suspend fun cleanConversationHistory(
        channelId: String
    ): DataResult<Unit>


    suspend fun getDevices(): DataResult<List<Device>>

    suspend fun addDevice(device: Device): DataResult<Unit>

    suspend fun deleteDevice(device: Device): DataResult<Unit>


}