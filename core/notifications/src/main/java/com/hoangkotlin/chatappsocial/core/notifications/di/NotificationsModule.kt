package com.hoangkotlin.chatappsocial.core.notifications.di

import com.hoangkotlin.chatappsocial.core.notifications.ChatUserIconBuilder
import com.hoangkotlin.chatappsocial.core.notifications.CoilChatUserIconBuilder
import com.hoangkotlin.chatappsocial.core.notifications.DefaultChatNotifications
import com.hoangkotlin.chatappsocial.core.notifications.DefaultNotificationHandler
import com.hoangkotlin.chatappsocial.core.notifications.NotificationHandler
import com.hoangkotlin.chatappsocial.core.notifications.SocialChatNotifications
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface NotificationsModule {

    @Binds
    fun bindsNotificationHandler(
        notificationHandler: DefaultNotificationHandler
    ): NotificationHandler

    @Binds
    fun bindsSocialChatNotifications(
        chatNotifications: DefaultChatNotifications
    ): SocialChatNotifications

    @Binds
    fun bindsIconBuilder(
        iconBuilder: CoilChatUserIconBuilder
    ): ChatUserIconBuilder



}