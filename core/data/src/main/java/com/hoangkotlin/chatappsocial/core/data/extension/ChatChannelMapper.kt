package com.hoangkotlin.chatappsocial.core.data.extension

import com.hoangkotlin.chatappsocial.core.database.model.ChatChannelEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatMemberEntity
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage

fun ChatChannelEntity.asSocialChatChannel(): SocialChatChannel {
    return SocialChatChannel(
        id = id,
        type = type,
        name = name ?: "",
        image = image ?: "",
        createdByUserId = createdByUserId,
        unreadCount = unreadCount,
        createdAt = createdAt,
        deletedAt = deletedAt,
        updatedAt = updatedAt,
        lastMessage = lastMessageId?.let { id ->
            SocialChatMessage(
                id = id,
                cid = this.id,
                createdAt = lastMessageAt,
                text = lastMessageText ?: ""
            )
        },
        members = members.values.map(ChatMemberEntity::asSocialChatMember),
        membership = membership?.asSocialChatMember()
    )
}

fun SocialChatChannel.asChatChannelEntity(): ChatChannelEntity {
    return ChatChannelEntity(
        id = id,
        type = type,
        name = name,
        image = image,
        createdByUserId = createdByUserId,
        unreadCount = unreadCount,
        createdAt = createdAt,
        updatedAt = updatedAt,
        deletedAt = deletedAt,
        lastMessageId = lastMessage?.id,
        lastMessageText = lastMessage?.text,
        lastMessageAt = lastMessage?.createdAt,
        members = members.associateBy(SocialChatMember::id, SocialChatMember::asChatMemberEntity),
        membership = membership?.asChatMemberEntity()

    )
}