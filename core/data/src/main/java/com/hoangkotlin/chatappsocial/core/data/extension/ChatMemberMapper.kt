package com.hoangkotlin.chatappsocial.core.data.extension

import com.hoangkotlin.chatappsocial.core.database.model.ChatMemberEntity
import com.hoangkotlin.chatappsocial.core.model.SocialChatMember
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser

fun ChatMemberEntity.asSocialChatMember(): SocialChatMember {
    return SocialChatMember(
        id = id,
        user = SocialChatUser(id, name, image),
        nickname = nickname,
        createdAt = createdAt,
        updatedAt = updatedAt,
        channelRole = channelRole
    )
}

fun SocialChatMember.asChatMemberEntity(): ChatMemberEntity {
    return ChatMemberEntity(
        id = id,
        userId = user.id,
        channelRole = channelRole,
        name = user.name,
        image = user.image,
        nickname = nickname,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}