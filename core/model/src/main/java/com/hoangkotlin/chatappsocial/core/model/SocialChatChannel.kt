package com.hoangkotlin.chatappsocial.core.model

import android.content.Context
import android.util.Log
import androidx.annotation.StringRes
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.model.R
import java.util.Date
import kotlin.math.log

private const val TAG = "SocialChatChannelExtension"

data class SocialChatChannel(
    val id: String = "",
    val type: String = "",
    val name: String = "",
    val image: String = "",
    val createdByUserId: String = "",
    val unreadCount: Int = 0,
    val createdAt: Date = Date(),
    val deletedAt: Date? = null,
    val updatedAt: Date? = null,
    val messages: List<SocialChatMessage> = emptyList(),
    val lastMessage: SocialChatMessage? = messages.maxByOrNull { it.createdAt!! },
    val pinnedMessages: List<SocialChatMessage> = emptyList(),
    val syncStatus: SyncStatus = SyncStatus.COMPLETED,
    val reads: List<SocialChannelRead> = emptyList(),
    val members: List<SocialChatMember> = emptyList(),
    val membership: SocialChatMember? = null,
    val isMuted: Boolean = false
)

fun SocialChatChannel.withoutMessages(): SocialChatChannel = this.copy(messages = emptyList())

fun SocialChatChannel.getDisplayName(
    context: Context,
    currentUser: SocialChatUser? = null,
    maxMember: Int = 5,
    @StringRes fallback: Int = R.string.untitled_channel,
): String {
    return name.takeIf { it.isNotEmpty() } ?: nameFromMembers(currentUser, maxMember)
    ?: context.getString(fallback)
}

private fun SocialChatChannel.nameFromMembers(
    currentUser: SocialChatUser?, maxMembers: Int
): String? {
    val users = getUsersExcludingCurrent(currentUser)

    return when {
        users.isNotEmpty() -> users.joinToString(limit = maxMembers, transform = { it.name })
            .takeIf { it.isNotEmpty() }

        // This channel has only the current user or only one user
        members.size == 1 -> members.first().user.name

        else -> null
    }
}

fun SocialChatChannel.getUsersExcludingCurrent(
    currentUser: SocialChatUser?,
): List<SocialChatUser> {
    val users = members.map(SocialChatMember::user)
    val currentUserId = currentUser?.id
    return if (currentUserId != null) {
        users.filterNot { it.id == currentUserId }
    } else {
        users
    }
}

fun SocialChatChannel.getLastMessageText(
    currentUser: SocialChatUser?,
): String? {
    if (lastMessage == null) {
        return null
    }
    val isLastMessageMine = lastMessage.user.id == currentUser?.id
    if (this.type == "MUTUAL") {
        if (isLastMessageMine) {
            return "You: ${lastMessage.text}"
        }
        return lastMessage.text
    }
    if (isLastMessageMine) {
        return "You: ${lastMessage.text}"
    }
    return "${lastMessage.user.name}: ${lastMessage.text}"

}

val SocialChatChannel.isGroup: Boolean
    get() = type == "GROUP"


