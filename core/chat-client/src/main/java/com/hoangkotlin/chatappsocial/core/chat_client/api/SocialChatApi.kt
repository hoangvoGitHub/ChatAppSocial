package com.hoangkotlin.chatappsocial.core.chat_client.api

import com.hoangkotlin.chatappsocial.core.data.model.DataResult
import com.hoangkotlin.chatappsocial.core.model.Device
import com.hoangkotlin.chatappsocial.core.model.FriendStatus
import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatFriend
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.attachment.SocialChatAttachment
import com.hoangkotlin.chatappsocial.core.model.attachment.UploadState
import com.hoangkotlin.chatappsocial.core.model.attachment.toAttachmentType
import com.hoangkotlin.chatappsocial.core.network.api.ChatChannelApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatFriendApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatMessageApi
import com.hoangkotlin.chatappsocial.core.network.api.ChatUserApi
import com.hoangkotlin.chatappsocial.core.network.api.DeviceApi
import com.hoangkotlin.chatappsocial.core.network.model.dto.ChatAttachmentDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.DeviceDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatMessageDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.DownChatUserDto
import com.hoangkotlin.chatappsocial.core.network.model.dto.DownMembershipDto
import com.hoangkotlin.chatappsocial.core.network.model.request.AddDeviceRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.ChatFriendRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.CreateChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.MarkReadRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryChatUsersRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.QueryManyChannelRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.SendChatMessageRequest
import com.hoangkotlin.chatappsocial.core.network.model.request.UpdateChatMessageRequest
import com.hoangkotlin.chatappsocial.core.network.model.response.ChatChannelResponse
import com.hoangkotlin.chatappsocial.core.network.model.response.ChatFriendDto
import kotlinx.coroutines.delay
import javax.inject.Inject

class SocialChatApi @Inject constructor(
    private val chatUserApi: ChatUserApi,
    private val chatChannelApi: ChatChannelApi,
    private val chatMessageApi: ChatMessageApi,
    private val chatFriendApi: ChatFriendApi,
    private val deviceApi: DeviceApi
) : ChatApi {


    override suspend fun sendMessage(
        channelId: String,
        request: SendChatMessageRequest
    ): DataResult<SocialChatMessage> {
        // Simulate network latency
        delay(500)
        try {
            val response = chatMessageApi.sendMessage(channelId, request)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.message.asSocialChatMessage()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            e.printStackTrace()
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun createChannel(
        name: String?,
        message: String?,
        memberIds: List<String>,
        type: String
    ): DataResult<SocialChatChannel> {
        try {
            val request = CreateChatChannelRequest(
                name = name,
                members = memberIds,
                type = type,
                message = message
            )
            val response = chatChannelApi.createChannel(request)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.flattened()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryMessage(messageId: String): DataResult<SocialChatMessage> {
        try {
            val response = chatMessageApi.queryMessage(messageId)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.message.asSocialChatMessage()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun updateMessage(
        messageId: String,
        request: UpdateChatMessageRequest
    ): DataResult<SocialChatMessage> {
        try {
            val response = chatMessageApi.updateMessage(messageId, request)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.message.asSocialChatMessage()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryChannels(queryRequest: QueryManyChannelRequest): DataResult<List<SocialChatChannel>> {
        try {
            val response = chatChannelApi.queryChannels(queryRequest)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.map(ChatChannelResponse::flattened)
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryChannel(
        channelId: String,
        queryRequest: QueryChatChannelRequest
    ): DataResult<SocialChatChannel> {
        try {
            val response = chatChannelApi.queryChannel(channelId, queryRequest)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.flattened()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun deleteChannel(channelId: String): DataResult<SocialChatChannel> {
        try {
            val response = chatChannelApi.deleteChannel(channelId)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.flattened()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryChatUsers(request: QueryChatUsersRequest): DataResult<List<SocialChatUser>> {
        try {
            val response = chatUserApi.queryChatUsers(request)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.map(DownChatUserDto::asSocialChatUser)
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryChatUser(userId: String): DataResult<SocialChatUser> {
        try {
            val response = chatUserApi.queryChatUser(userId)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.asSocialChatUser()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryChatUser(): DataResult<SocialChatUser> {
        try {
            val response = chatUserApi.queryChatUser()
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.asSocialChatUser()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryChatUserByUsername(username: String): DataResult<SocialChatUser> {
        try {
            val response = chatUserApi.queryChatUserByUsername(username)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.asSocialChatUser()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun queryFriend(
        name: String,
        limit: Int,
        offset: Int,
        status: String,
        sortBy: String?
    ): DataResult<List<SocialChatFriend>> {
        try {
            val response = chatFriendApi.queryFriends(name, limit, offset, status, sortBy)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.data?.map(ChatFriendDto::asSocialChatFriend) ?: emptyList()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun acceptFriend(friendUserId: String): DataResult<SocialChatFriend> {
        try {
            val response = chatFriendApi.acceptFriend(ChatFriendRequest(friendUserId))
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.data!!.asSocialChatFriend()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun removeFriend(friendUserId: String): DataResult<SocialChatFriend> {
        try {
            val response = chatFriendApi.removeFriend(ChatFriendRequest(friendUserId))
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(
                    response.body()!!.data!!.asSocialChatFriend()
                )
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun rejectFriend(friendUserId: String): DataResult<SocialChatFriend?> {
        try {
            val response = chatFriendApi.rejectFriend(ChatFriendRequest(friendUserId))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data?.asSocialChatFriend()
                return DataResult.Success.Network(data)
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun addFriend(friendUserId: String): DataResult<SocialChatFriend> {
        try {
            val response = chatFriendApi.addFriend(ChatFriendRequest(friendUserId))
            if (response.isSuccessful && response.body() != null) {
                val data = response.body()!!.data!!.asSocialChatFriend()
                return DataResult.Success.Network(data)
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun markRead(channelId: String, messageId: String): DataResult<Unit> {
        try {
            val response = chatChannelApi.markRead(channelId, MarkReadRequest(messageId))
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(Unit)
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun cleanConversationHistory(channelId: String): DataResult<Unit> {
        try {
            val response = chatChannelApi.deleteConversationHistory(channelId)
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(Unit)
            }
            return DataResult.Error(response.message())
        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun getDevices(): DataResult<List<Device>> {
        try {
            val response = deviceApi.getDevices()
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(response.body()!!.devices.map(DeviceDto::asDevice))
            }
            return DataResult.Error(response.message())

        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun addDevice(device: Device): DataResult<Unit> {
        try {
            val response = deviceApi.addDevice(
                request = AddDeviceRequest(device.deviceId)
            )
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(Unit)
            }
            return DataResult.Error(response.message())

        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }

    override suspend fun deleteDevice(device: Device): DataResult<Unit> {
        try {
            val response = deviceApi.deleteDevice(
                deviceId = device.deviceId
            )
            if (response.isSuccessful && response.body() != null) {
                return DataResult.Success.Network(Unit)
            }
            return DataResult.Error(response.message())

        } catch (e: Exception) {
            return DataResult.Error(e.message ?: "Unknown Message")
        }
    }
}

fun DeviceDto.asDevice(): Device {
    return Device(deviceId)
}

fun DownChatMessageDto.asSocialChatMessage(): SocialChatMessage {
    return SocialChatMessage(
        id = id,
        cid = cid,
        text = text ?: "",
        replyTo = replyTo?.asSocialChatMessage(),
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        deletedAt = this.deletedAt,
        user = sentByUser.asSocialChatUser(),
        attachments = this.attachments.map(ChatAttachmentDto::asSocialChatAttachment)

    )
}

fun DownChatUserDto.asSocialChatUser(): SocialChatUser {
    return SocialChatUser(
        id = id,
        name = name,
        image = imageUrl ?: "",
        isOnline = isOnline ?: true,
        isInvisible = isInvisible ?: false,
        lastActiveAt = lastActiveAt,
        createdAt = createdAt
    )
}

fun ChatAttachmentDto.asSocialChatAttachment(): SocialChatAttachment {
    return SocialChatAttachment(
        name = this.name,
        url = this.url,
        mimeType = this.mimeType,
        imageUrl = this.imageUrl,
        thumbnailUrl = this.thumbnailUrl,
        videoLength = this.videoLength,
        originalHeight = this.originalHeight,
        originalWidth = this.originalWidth,
        upload = null, // As there's no corresponding field in ChatAttachmentDto
        uploadState = UploadState.Success, // As there's no corresponding field in ChatAttachmentDto
        type = this.type.toAttachmentType(), // As there's no corresponding field in ChatAttachmentDto
        fileSize = this.fileSize,
        extraData = this.extraData.toMutableMap(),
        createdAt = this.createdAt
    )
}

fun ChatChannelResponse.flattened(): SocialChatChannel {
    return SocialChatChannel(
        id = channel.id,
        name = channel.name ?: "",
        type = channel.type ?: "",
        image = channel.imageUrl ?: "",
        lastMessage = messages.maxByOrNull {
            it.createdAt
        }?.asSocialChatMessage(),
        createdAt = channel.createdAt,
//        messages = messages.sortedByDescending { it.createdAt }
//            .map(DownChatMessageDto::asSocialChatMessage),
        messages = messages.map(DownChatMessageDto::asSocialChatMessage),
        members = members.map(DownMembershipDto::asSocialChatMember),
        membership = membership.asSocialChatMember(),
        unreadCount = unreadCount,
        reads = members.map(DownMembershipDto::asSocialChannelRead)

    )


}

fun DownMembershipDto.asSocialChatMember(): SocialChatMember {
    return SocialChatMember(
        user = user.asSocialChatUser(),
        nickname = nickname,
        channelRole = channelRole
    )
}

fun DownMembershipDto.asSocialChannelRead(): SocialChannelRead {
    return SocialChannelRead(
        user = user.asSocialChatUser(),
        lastReadMessageId = lastReadMessageId,
        lastReadAt = lastReadAt
    )
}

fun ChatFriendDto.asSocialChatFriend(): SocialChatFriend {
    return SocialChatFriend(
        id = id,
        user = user.asSocialChatUser(),
        status = FriendStatus.valueOf(status),
        cid = channelId,
        createdAt = createdAt
    )
}

