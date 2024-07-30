package com.hoangkotlin.chatappsocial.core.notifications

import com.hoangkotlin.chatappsocial.core.model.Device
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.events.NewMessageEvent
import javax.inject.Inject

interface SocialChatNotifications {
    fun onSetUser()

    fun setDevice(device: Device)

    fun onPushMessage(
        message: String,
        pushNotificationReceivedListener: PushNotificationReceivedListener
    )

    fun onNewMessage(newMessageEvent: NewMessageEvent)

    suspend fun onLogout(flushPersistence: Boolean)

    fun displayNotification(channel: SocialChatChannel, message: SocialChatMessage)

    fun dismissChannelNotifications(channelType: String, channelId: String)
}

class DefaultChatNotifications @Inject constructor(
    private val notificationHandler: NotificationHandler,
) : SocialChatNotifications {

    private val showedMessageIds = mutableSetOf<String>()

    override fun onSetUser() {
        TODO("Not yet implemented")
    }

    override fun setDevice(device: Device) {
        TODO("Not yet implemented")
    }

    override fun onPushMessage(
        message: String,
        pushNotificationReceivedListener: PushNotificationReceivedListener
    ) {
        TODO("Not yet implemented")
    }

    override fun onNewMessage(newMessageEvent: NewMessageEvent) {
        // do when the app is in background, then need a worker to get data from chat client to display data
    }

    override suspend fun onLogout(flushPersistence: Boolean) {
        TODO("Not yet implemented")
    }

    private fun wasAlreadyNotificationDisplayed(messageId: String) =
        showedMessageIds.contains(messageId)

    override fun displayNotification(channel: SocialChatChannel, message: SocialChatMessage) {
        if (wasAlreadyNotificationDisplayed(message.id)) return
        showedMessageIds.add(message.id)
        notificationHandler.showNotification(channel, message)
    }

    override fun dismissChannelNotifications(channelType: String, channelId: String) {
        TODO("Not yet implemented")
    }

    companion object {
        private const val TAG = "DefaultChatNotifications"
    }
}