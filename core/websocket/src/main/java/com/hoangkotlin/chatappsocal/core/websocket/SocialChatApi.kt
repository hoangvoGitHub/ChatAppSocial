//package com.hoangkotlin.chatappsocial.core.websocket
//
//import android.os.Build
//import androidx.annotation.RequiresApi
//import com.hoangkotlin.chatappsocial.core.data.model.DataResult
//import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
//import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
//import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
//import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
//import com.hoangkotlin.chatappsocial.core.network.api.ChatChannelApi
//import com.hoangkotlin.chatappsocial.core.network.api.ChatMessageApi
//import com.hoangkotlin.chatappsocial.core.network.api.ChatUserApi
//import com.hoangkotlin.chatappsocial.core.network.model.response.ChatChannelResponse
//import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatMessageDto
//import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatUserDto
//import com.hoangkotlin.chatappsocial.core.network.model.dto.DownMembershipDto
//import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
//import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatUsersRequest
//import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
//import com.hoangkotlin.chatappsocial.core.network.model.request.SendChatMessageRequest
//import com.hoangkotlin.chatappsocial.core.network.model.request.UpdateChatMessageRequest
//import com.hoangkotlin.chatappsocial.core.common.utils.DateParser
//import java.io.IOException
//import javax.inject.Inject
//
//@RequiresApi(Build.VERSION_CODES.O)
//class SocialChatApi @Inject constructor(
//    private val chatUserApi: ChatUserApi,
//    private val chatChannelApi: ChatChannelApi,
//    private val chatMessageApi: ChatMessageApi
//) : ChatApi {
//    override suspend fun sendMessage(
//        channelId: String,
//        request: SendChatMessageRequest
//    ): DataResult<SocialChatMessage> {
//        try {
//            val response = chatMessageApi.sendMessage(channelId, request)
//            if (response.isSuccessful && response.body() != null) {
//                return DataResult.Success(
//                    response.body()!!.message.asSocialChatMessage()
//                )
//            }
//            return DataResult.Error(response.message())
//        } catch (e: IOException) {
//            return DataResult.Error(e.message ?: "Unknown Message")
//        }
//    }
//
//    override suspend fun getMessage(messageId: String): DataResult<SocialChatMessage> {
//        try {
//            val response = chatMessageApi.getMessage(messageId)
//            if (response.isSuccessful && response.body() != null) {
//                return DataResult.Success(
//                    response.body()!!.message.asSocialChatMessage()
//                )
//            }
//            return DataResult.Error(response.message())
//        } catch (e: IOException) {
//            return DataResult.Error(e.message ?: "Unknown Message")
//        }
//    }
//
//    override suspend fun updateMessage(
//        messageId: String,
//        request: UpdateChatMessageRequest
//    ): DataResult<SocialChatMessage> {
//        try {
//            val response = chatMessageApi.updateMessage(messageId, request)
//            if (response.isSuccessful && response.body() != null) {
//                return DataResult.Success(
//                    response.body()!!.message.asSocialChatMessage()
//                )
//            }
//            return DataResult.Error(response.message())
//        } catch (e: IOException) {
//            return DataResult.Error(e.message ?: "Unknown Message")
//        }
//    }
//
//    override suspend fun queryChannels(queryRequest: QueryManyChannelRequest): DataResult<List<SocialChatChannel>> {
//        try {
//            val response = chatChannelApi.queryChannels(queryRequest)
//            if (response.isSuccessful && response.body() != null) {
//                return DataResult.Success(
//                    response.body()!!.map {
//                        it.flattened()
//                    }
//                )
//            }
//            return DataResult.Error(response.message())
//        } catch (e: IOException) {
//            return DataResult.Error(e.message ?: "Unknown Message")
//        }
//    }
//
//    override suspend fun queryChannel(
//        channelId: String,
//        queryRequest: QueryChatChannelRequest
//    ): DataResult<SocialChatChannel> {
//        try {
//            val response = chatChannelApi.queryChannel(channelId, queryRequest)
//            if (response.isSuccessful && response.body() != null) {
//                return DataResult.Success(
//                    response.body()!!.flattened()
//                )
//            }
//            return DataResult.Error(response.message())
//        } catch (e: IOException) {
//            return DataResult.Error(e.message ?: "Unknown Message")
//        }
//    }
//
//    override suspend fun deleteChannel(channelId: String): DataResult<SocialChatChannel> {
//        try {
//            val response = chatChannelApi.deleteChannel(channelId)
//            if (response.isSuccessful && response.body() != null) {
//                return DataResult.Success(
//                    response.body()!!.flattened()
//                )
//            }
//            return DataResult.Error(response.message())
//        } catch (e: IOException) {
//            return DataResult.Error(e.message ?: "Unknown Message")
//        }
//    }
//
//    override suspend fun queryChatUsers(request: QueryChatUsersRequest): DataResult<List<SocialChatUser>> {
//        try {
//            val response = chatUserApi.queryChatUsers(request)
//            if (response.isSuccessful && response.body() != null) {
//                return DataResult.Success(
//                    response.body()!!.map {
//                        it.asSocialChatUser()
//                    }
//                )
//            }
//            return DataResult.Error(response.message())
//        } catch (e: IOException) {
//            return DataResult.Error(e.message ?: "Unknown Message")
//        }
//    }
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//fun DownChatMessageDto.asSocialChatMessage(): SocialChatMessage {
//    return SocialChatMessage(
//        id = id,
//        cid = cid,
//        text = text ?: "",
//        replyTo = replyTo?.asSocialChatMessage(),
//        createdAt = this.createdAt,
//        updatedAt = this.updatedAt,
//        deletedAt = this.deletedAt,
//        user = sentByUser.asSocialChatUser()
//
//    )
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//fun DownChatUserDto.asSocialChatUser(): SocialChatUser {
//    return SocialChatUser(
//        id = id,
//        name = "$firstName $lastName",
//        image = imageUrl ?: "",
//        isOnline = isOnline ?: true,
//        isInvisible = isInvisible ?: false,
//        lastActiveAt = lastActiveAt
//    )
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//fun ChatChannelResponse.flattened(): SocialChatChannel {
//    return SocialChatChannel(
//        id = channel.id,
//        name = channel.name ?: "",
//        type = channel.type ?: "",
//        image = channel.imageUrl ?: "",
//        lastMessage = channel.messages.minByOrNull {
//            it.createdAt
//        }!!.asSocialChatMessage(),
//        createdAt = channel.createdAt,
//        messages = channel.messages.map { it.asSocialChatMessage() },
//        members = members.map { it.asSocialChatMember() },
//        membership = membership.asSocialChatMember()
//
//
//    )
//}
//
//@RequiresApi(Build.VERSION_CODES.O)
//fun DownMembershipDto.asSocialChatMember(): SocialChatMember {
//    return SocialChatMember(
//        id = id,
//        user = user.asSocialChatUser(),
//        nickname = nickname
//    )
//}
//
