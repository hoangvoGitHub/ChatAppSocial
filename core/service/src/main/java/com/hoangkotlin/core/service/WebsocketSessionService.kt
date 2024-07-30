package com.hoangkotlin.core.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.PowerManager
import com.hoangkotlin.chatappsocial.core.common.Dispatcher
import com.hoangkotlin.chatappsocial.core.common.SocialDispatchers.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class WebsocketSessionService(
    @Dispatcher(IO) ioDispatcher: CoroutineDispatcher,
    @Dispatcher(Main) mainDispatcher: CoroutineDispatcher,
) : Service() {

    private val TAG = "EndlessService"
    private var wakeLock: PowerManager.WakeLock? = null
    private var isServiceStarted = false
    private val mainScope =  CoroutineScope(SupervisorJob() + mainDispatcher)
    private val ioScope =  CoroutineScope(SupervisorJob() + ioDispatcher)

    override fun onBind(intent: Intent?): IBinder? {
        TODO("Not yet implemented")
    }



    companion object {
        //Notification channel ID's
        const val SERVICE_CHANNEL_ID = "SOCIAL_ENDLESS_SERVICE_CHANNEL"
        const val ERROR_CHANNEL_ID = "SOCIAL_ENDLESS_SERVICE_ERROR_CHANNEL"
        const val SERVICE_GROUP_ID = "SOCIAL_ENDLESS_SERVICE_GROUP_ID"
        const val PUSH_CHANNEL_ID = "SOCIAL_ENDLESS_SERVICE_PUSH_CHANNEL_ID"

        //Notification ID's
        const val SERVICE_NOTIFICATION_ID = 1
        const val ERROR_NOTIFICATION_ID = 2
        const val PUSH_NOTIFICATION_ID = 2
    }


}