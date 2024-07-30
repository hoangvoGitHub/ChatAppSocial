package com.hoangkotlin.chatappsocial.core.chat_client.di

import android.content.Context
import android.net.ConnectivityManager
import com.hoangkotlin.chatappsocial.core.chat_client.socket.DefaultSocketManager
import com.hoangkotlin.chatappsocial.core.chat_client.socket.SocketManager
import com.hoangkotlin.chatappsocial.core.chat_client.api.ChatApi
import com.hoangkotlin.chatappsocial.core.chat_client.api.SocialChatApi
import com.hoangkotlin.chatappsocial.core.chat_client.downloader.FileDownloader
import com.hoangkotlin.chatappsocial.core.chat_client.downloader.SocialFileDownloader
import com.hoangkotlin.chatappsocial.core.chat_client.handler.DefaultSentEventHandler
import com.hoangkotlin.chatappsocial.core.chat_client.handler.SentEventHandler
import com.hoangkotlin.chatappsocial.core.chat_client.uploader.FileUploader
import com.hoangkotlin.chatappsocial.core.chat_client.uploader.SocialFileUploader
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ChatClientModule {

    @Singleton
    @Provides
    fun providesConnectivityManager(
        @ApplicationContext context: Context
    ): ConnectivityManager {
        return context
            .getSystemService(Context.CONNECTIVITY_SERVICE)
                as ConnectivityManager
    }


    @Module
    @InstallIn(SingletonComponent::class)
    interface ChatClientBindModule {
        @Binds
        fun bindsChatApi(
            chatApi: SocialChatApi
        ): ChatApi

        @Binds
        fun bindsFileUploader(
            fileUploader: SocialFileUploader
        ): FileUploader

        @Binds
        fun bindsFileDownloader(
            fileDownloader: SocialFileDownloader
        ): FileDownloader

        @Binds
        fun bindsSocketManager(
            defaultSocketManager: DefaultSocketManager
        ): SocketManager

        @Binds
        fun bindsSentEventHandler(
            defaultSentSocketEventHandler: DefaultSentEventHandler
        ): SentEventHandler

    }

}