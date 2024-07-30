package com.hoangkotlin.chatappsocial.core.firebase_cloud_messaging

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val TAG = "SocialFirebaseMessaging"
class SocialFirebaseMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage) {
//        super.onMessageReceived(message)
        Log.d(TAG, "onMessageReceived: $message")
    }

    override fun onNewToken(token: String) {
//        super.onNewToken(token)
        Log.d(TAG, "onNewToken: $token")

    }

}