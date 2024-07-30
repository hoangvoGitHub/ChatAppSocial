package com.hoangkotlin.chatappsocial.core.database.model

import com.hoangkotlin.chatappsocial.core.database.converters.NullableDateSerializer
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ChatMemberEntity(
    val id: String,
    val userId: String,
    val channelRole: String? = null,
    val name: String,
    val image: String,
    val nickname: String? = null,
    val lastReadMessage: String? = null,
    @Serializable(with = NullableDateSerializer::class)
    val lastReadAt: Date? = null,
    val unreadMessages: Int = 0,
    val isInvited: Boolean = false,
    val isMute: Boolean = false,
    @Serializable(with = NullableDateSerializer::class)
    val createdAt: Date? = null,
    @Serializable(with = NullableDateSerializer::class)
    val updatedAt: Date? = null,
    @Serializable(with = NullableDateSerializer::class)
    val deletedAt: Date? = null,
) {
    override fun toString(): String {
        return "MemberEntity(userId='$userId', role='$channelRole', nickname=$nickname, " +
                "lastReadMessage=$lastReadMessage, lastReadAt=$lastReadAt, " +
                "unreadMessages=$unreadMessages, createdAt=$createdAt, " +
                "updatedAt=$updatedAt, deletedAt=$deletedAt)"
    }
}
