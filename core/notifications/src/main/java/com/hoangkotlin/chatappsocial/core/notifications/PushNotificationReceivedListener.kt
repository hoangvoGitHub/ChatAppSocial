package com.hoangkotlin.chatappsocial.core.notifications

fun interface PushNotificationReceivedListener {
    fun onPushNotificationReceived(channelType: String, channelId: String)
}