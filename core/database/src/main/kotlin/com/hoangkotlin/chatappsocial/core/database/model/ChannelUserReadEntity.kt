package com.hoangkotlin.chatappsocial.core.database.model

import java.util.Date


data class ChannelUserReadEntity(
    val userId: String,
    val lastRead: Date?,
    val unreadMessages: Int,
    val lastSeenAt: Date?,
)
