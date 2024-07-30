package com.hoangkotlin.chatappsocial.core.database

import com.hoangkotlin.chatappsocial.core.database.model.ChatChannelEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatMemberEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatMessageEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import java.util.Date

object TestUtil {
    fun createChatUser(id: String): ChatUserEntity {
        return ChatUserEntity(id = id, username = id)
    }

    fun createChatMessage(
        id: String,
        cid: String = "test",
        createdAt: Date? = null,
        syncStatus: SyncStatus = SyncStatus.COMPLETED,
    ): ChatMessageEntity {
        return ChatMessageEntity(
            id = id,
            cid = cid,
            createdAt = createdAt,
            syncStatus = syncStatus,
            text = "Test Message",
            userId = id,
            createdLocallyAt = createdAt
        )
    }

    fun createChatChannel(
        id: String,
        syncStatus: SyncStatus = SyncStatus.COMPLETED,
        members: Map<String, ChatMemberEntity> = emptyMap(),
        extraData: Map<String, Any> = emptyMap()
    ): ChatChannelEntity {
        return ChatChannelEntity(
            id = id,
            createdByUserId = id,
            syncStatus = syncStatus,
            members = members,
            extraData = extraData,
            createdAt = Date(),
            membership = null
        )
    }

    fun createMember(numOfMembers: Int = 0): Map<String, ChatMemberEntity> =
        mutableMapOf<String, ChatMemberEntity>().apply {
            for (i in 0 until numOfMembers) {
                this["member$i"] = ChatMemberEntity(
                    userId = "member$i",
                )
            }
        }

    fun createExtraData(numOfFields: Int = 0): Map<String, Any> =
        mutableMapOf<String, Any>().apply {
            for (i in 0 until numOfFields) {
                this["member$i"] = if (i % 2 == 0) "1" else 1
            }
        }
}