package com.hoangkotlin.chatappsocial.core.notifications

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import javax.inject.Inject


internal class SocialNotificationMessageReceiver : BroadcastReceiver() {

    companion object {
        private const val ACTION_DISMISS = "com.hoangkotlin.chatappsocial.DISMISS"
        private const val ACTION_READ = "com.hoangkotlin.chatappsocial.READ"
        private const val ACTION_REPLY = "com.hoangkotlin.chatappsocial.REPLY"
        private const val KEY_MESSAGE_ID = "message_id"
        private const val KEY_CHANNEL_ID = "channel_id"

        private const val IMMUTABLE_PENDING_INTENT_FLAGS =
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE

        private val MUTABLE_PENDING_INTENT_FLAGS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }

        internal fun createDismissPendingIntent(
            context: Context,
            notificationId: Int,
            channel: SocialChatChannel,
        ): PendingIntent = PendingIntent.getBroadcast(
            context,
            notificationId,
            createNotifyIntent(context, channel, ACTION_DISMISS),
            IMMUTABLE_PENDING_INTENT_FLAGS,
        )

        private fun createNotifyIntent(
            context: Context,
            channel: SocialChatChannel,
            action: String
        ) =
            Intent(context, SocialNotificationMessageReceiver::class.java).apply {
                putExtra(KEY_CHANNEL_ID, channel.id)
                this.action = action
            }
    }

    @Inject
    lateinit var notificationHandler: NotificationHandler
    override fun onReceive(context: Context?, intent: Intent?) {
        val channelId = intent?.getStringExtra(KEY_CHANNEL_ID) ?: return
        when (intent.action) {
            ACTION_READ -> {}
            ACTION_REPLY -> {}
            else -> dismissNotification(channelId)
        }
    }

    private fun dismissNotification(channelId: String) {
        notificationHandler.dismissNotification(channelId)
    }
}
