package com.hoangkotlin.chatappsocial.core.offline.event

import com.hoangkotlin.chatappsocial.core.model.SocialChannelRead
import com.hoangkotlin.chatappsocial.core.model.events.MessageReadEvent

fun MessageReadEvent.asSocialChannelRead(): SocialChannelRead {
    return SocialChannelRead(
        user = user,
        lastReadMessageId = message.id,
        lastReadAt = createdAt
    )
}