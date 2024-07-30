package com.hoangkotlin.chatappsocial.core.notifications

import androidx.core.graphics.drawable.IconCompat
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser

interface ChatUserIconBuilder {

    suspend fun buildUserIcon(user: SocialChatUser): IconCompat?
}