package com.hoangkotlin.chatappsocial.core.data.extension

import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser


fun SocialChatUser.asChatUserEntity(): ChatUserEntity {
    return ChatUserEntity(
        id = id,
        username = name,
        name = name,
        image = image,
        createdAt = createdAt,
        lastActive = lastActiveAt
    )
}

fun ChatUserEntity.asSocialChatUser(): SocialChatUser {
    return SocialChatUser(
        id = id,
        name = name,
        image = image ?: "",
        lastActiveAt = lastActive,
        createdAt = createdAt
    )
}