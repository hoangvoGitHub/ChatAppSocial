package com.hoangkotlin.chatappsocial.core.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = CHAT_USER_ENTITY)
data class ChatUserEntity(
    @PrimaryKey
    val id: String,
    val username: String = "",
    val image: String? = null,
    val name: String = "",
    val createdAt: Date? = null,
    val updatedAt: Date? = null,
    val lastActive: Date? = null,
)

const val CHAT_USER_ENTITY = "social_chat_user"