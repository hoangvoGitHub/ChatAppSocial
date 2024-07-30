package com.hoangkotlin.chatappsocial.core.model

import java.util.Date

data class SocialChatFriend(
    val id: String = "",
    val user: SocialChatUser = SocialChatUser(),
    val status: FriendStatus,
    val cid: String? = null,
    val createdAt: Date = Date()
)

enum class FriendStatus {
    REQUEST_FROM_ME,
    REQUEST_FROM_OTHER,
    FRIEND;

    companion object {
        fun valueOfNullable( value: String):FriendStatus?{
            return try {
                FriendStatus.valueOf(value)
            }catch (e: IllegalArgumentException ){
                null
            }
        }
    }
}

