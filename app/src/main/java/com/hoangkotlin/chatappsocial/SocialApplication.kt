package com.hoangkotlin.chatappsocial

import android.app.Application
import android.util.Log
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import coil.Coil
import com.hoangkotlin.chatappsocial.coil.SocialImageLoaderFactory
import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import dagger.hilt.android.HiltAndroidApp
import io.reactivex.exceptions.UndeliverableException
import io.reactivex.plugins.RxJavaPlugins
import javax.inject.Inject

/**
 * [Application] class for ChatAppSocial
 */
private const val TAG = "SocialApplication"

@HiltAndroidApp
class SocialApplication : Application(), Configuration.Provider {

    @Inject
    lateinit var chatClient: ChatClient

    @Inject
    lateinit var workerFactory: HiltWorkerFactory

    @Inject
    lateinit var imageLoaderFactory: SocialImageLoaderFactory

    override val workManagerConfiguration: Configuration
        get() = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()

    override fun onCreate() {
        super.onCreate()
        Coil.setImageLoader(imageLoaderFactory)
        RxJavaPlugins.setErrorHandler { e ->
            if (e is UndeliverableException) {
                // Merely log undeliverable exceptions
                Log.e(TAG, "onCreate: ${e.message}")
            } else {
                // Forward all others to current thread's uncaught exception handler
                Thread.currentThread().also { thread ->
                    thread.uncaughtExceptionHandler?.uncaughtException(thread, e)
                }
            }
        }
    }
//
//    hoang2017607349@gmail.com
//    hoang-1387538611@gmail.com
}

