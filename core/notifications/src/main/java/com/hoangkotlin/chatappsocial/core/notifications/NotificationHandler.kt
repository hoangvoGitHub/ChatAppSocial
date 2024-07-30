package com.hoangkotlin.chatappsocial.core.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.Person
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import com.hoangkotlin.chatappsocial.core.common.di.IOScope
import com.hoangkotlin.chatappsocial.core.model.SocialChatChannel
import com.hoangkotlin.chatappsocial.core.model.SocialChatMessage
import com.hoangkotlin.chatappsocial.core.model.SocialChatUser
import com.hoangkotlin.chatappsocial.core.model.events.NewMessageEvent
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date
import java.util.concurrent.ConcurrentHashMap
import javax.inject.Inject
import com.hoangkotlin.chatappsocial.core.common.R as commonR

interface NotificationHandler {

    fun onChatEvent(event: NewMessageEvent): Boolean {
        return true
    }

    fun onPushMessage(message: String): Boolean {
        return false
    }

    fun showNotification(channel: SocialChatChannel, message: SocialChatMessage)

    fun dismissNotification(channelId: String)

    fun dismissAllNotifications()

}

class DefaultNotificationHandler @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userIconBuilder: ChatUserIconBuilder,
    @IOScope private val userScope: CoroutineScope,
) : NotificationHandler {

    private val notificationChannel by lazy {
        NotificationChannel(
            context.getString(R.string.social_chat_notification_channel_id),
            context.getString(R.string.social_chat_notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT,
        )
    }

    private val notificationManager: NotificationManager by lazy {
        (context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
    }

    private val messages: ConcurrentHashMap<Int, Map<String, NotificationCompat.MessagingStyle.Message>> =
        ConcurrentHashMap()


    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Only create the notification channel on API 26+ (Oreo) because NotificationChannel class is not in the support library
        // Check if the channel already exists
        val notificationId = context.getString(R.string.social_chat_notification_channel_id)
        val existingChannel = notificationManager.getNotificationChannel(notificationId)
        if (existingChannel == null) {
            // Register the channel with the system
            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun showNotification(channel: SocialChatChannel, message: SocialChatMessage) {

        val destinationClass = Class.forName(MainActivityClassName)
        val deepLinkIntent = Intent(
            /* action = */ Intent.ACTION_VIEW,
            /* uri = */ channel.channelUri,
            /* packageContext = */ context,
            /* cls = */ destinationClass
        ).apply {
            action = Intent.ACTION_MAIN
            addCategory(Intent.CATEGORY_LAUNCHER)
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        }

        val contentPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            addNextIntentWithParentStack(deepLinkIntent)
            getPendingIntent(
                0, PendingIntent.FLAG_UPDATE_CURRENT
                        or PendingIntent.FLAG_IMMUTABLE
            )

        }


        userScope.launch {
            val notificationId = channel.id.hashCode()
            if (!messages.containsKey(notificationId)) {
                messages[notificationId] =
                    mutableMapOf<String, NotificationCompat.MessagingStyle.Message>().apply {
                        put(message.id, message.toMessagingStyleMessage(context))
                    }
            } else {
                val currentMap = messages[notificationId]
                messages[notificationId] = currentMap!!.toMutableMap().apply {
                    put(message.id, message.toMessagingStyleMessage(context))
                }
            }


            val messageStyle = createMessagingStyle(
                SocialChatUser(name = "TestUserName"),
                channel
            )

            messages[notificationId]?.values?.forEach {
                messageStyle.addMessage(it)
            }


            val notification = NotificationCompat.Builder(context, notificationChannel.id)
                .setSmallIcon(com.hoangkotlin.chatappsocial.core.ui.R.drawable.notification)
                .setStyle(messageStyle)
                .setContentIntent(contentPendingIntent)
                .setDeleteIntent(
                    SocialNotificationMessageReceiver.createDismissPendingIntent(
                        context, notificationId, channel
                    )
                )
//                .addAction(
//                    SocialNotificationMessageReceiver.createReadAction(
//                        context,
//                        notificationId,
//                        channel,
//                        message
//                    )
//                )
//                .addAction(
//                    SocialNotificationMessageReceiver.createReplyAction(
//                        context,
//                        notificationId,
//                        channel
//                    )
//                )
                .build()
            notificationManager.notify(notificationId, notification)
        }
    }

    override fun dismissNotification(channelId: String) {
        messages[channelId.hashCode()] = emptyMap()
    }

    override fun dismissAllNotifications() {
        TODO("Not yet implemented")
    }

    private val SocialChatChannel.channelUri: Uri
        get() = "https://com.hoangkotlin.socialchatapp/$id".toUri()


    private suspend fun createMessagingStyle(
        currentUser: SocialChatUser,
        channel: SocialChatChannel
    ): NotificationCompat.MessagingStyle =
        NotificationCompat.MessagingStyle(currentUser.toNotificationPerson(context))
            .setConversationTitle(channel.name)
            .setGroupConversation(channel.name.isNotBlank())


    private suspend fun SocialChatMessage.toMessagingStyleMessage(context: Context): NotificationCompat.MessagingStyle.Message =
        NotificationCompat.MessagingStyle.Message(text, timestamp, person(context))

    private suspend fun SocialChatMessage.person(context: Context): Person =
        user.toNotificationPerson(context)

    private suspend fun SocialChatUser.toNotificationPerson(context: Context): Person =
        Person.Builder()
            .setKey(id)
            .setName(personName(context))
            .setIcon(userIconBuilder.buildUserIcon(this))
            .build()

    private fun SocialChatUser.personName(context: Context): String =
        name.takeIf { it.isNotBlank() }
            ?: context.getString(commonR.string.chat_notification_empty_username)

    private val SocialChatMessage.timestamp: Long
        get() = (createdAt ?: createdLocallyAt ?: Date()).time

    companion object {
        private const val MainActivityClassName: String =
            "com.hoangkotlin.chatappsocial.ui.main.MainActivity"
    }
}