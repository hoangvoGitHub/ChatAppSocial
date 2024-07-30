package com.hoangkotlin.chatappsocial.core.database.model

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import java.util.Date

/**
 * An entity to store Chat Channel in Room database.
 *
 * @param id the unique id of [ChatChannelEntity] in remote server.
 * @param type the type id of [ChatChannelEntity] in remote server.
 * @param name the name of [ChatChannelEntity], which can be null.
 * @param image the image source url of [ChatChannelEntity], which can be null.
 * @param createdByUserId the id of the the one who created a [ChatChannelEntity].
 * @param createdAt the date when [ChatChannelEntity] is created.
 * @param updatedAt the date when [ChatChannelEntity] is updated, which can be null.
 * @param deletedAt the date when [ChatChannelEntity] is deleted, which can be null.
 * @param syncStatus the sync status of [ChatChannelEntity], which is a [SyncStatus] enum.
 * @param lastMessageAt the sending date of [ChatChannelEntity]'s last message.
 * @param lastMessageId the id of [ChatChannelEntity]'s last message.
 * @param lastMessageText the text of [ChatChannelEntity]'s last message.
 * @param members a map that stores the members in [ChatChannelEntity].
 * @param extraData [ChatChannelEntity]'s extra data.
 * @param membership Represents relationship of the current user to this channel.
 */
@Entity(tableName = CHAT_CHANNEL_TABLE)
data class ChatChannelEntity(
    val id: String,
    val type: String = "",
    val name: String? = null,
    val image: String? = null,
    val createdByUserId: String,
    val unreadCount: Int = 0,
    val createdAt: Date,
    val updatedAt: Date? = null,
    val deletedAt: Date? = null,
    val syncStatus: SyncStatus = SyncStatus.COMPLETED,
    val lastMessageAt: Date? = null,
    val lastMessageId: String? = null,
    val lastMessageText: String? = null,
    val members: Map<String, ChatMemberEntity> = emptyMap(),
    val extraData: Map<String, String> = emptyMap(),
    val membership: ChatMemberEntity?,

    ) {
    @PrimaryKey
    var cid: String = "%s:%s".format(type, id)
}

//data class WrapperChatChannelEntity(
//    @Embedded val chatChannelEntity: ChatChannelEntity,
//    @Relation(entity = ChatMessageEntity)
//)

const val CHAT_CHANNEL_TABLE = "social_chat_channel"