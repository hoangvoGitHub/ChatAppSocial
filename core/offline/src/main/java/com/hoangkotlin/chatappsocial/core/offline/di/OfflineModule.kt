package com.hoangkotlin.chatappsocial.core.offline.di

import com.hoangkotlin.chatappsocial.core.chat_client.ChatClient
import com.hoangkotlin.chatappsocial.core.chat_client.client.SocialClientState
import com.hoangkotlin.chatappsocial.core.common.di.IOScope
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineScope
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object OfflineModule {

    @Provides
    @Singleton
    fun providesChatStateHolder(
        chatClient: ChatClient,
        @IOScope userScope: CoroutineScope,
    ): ChatStateHolder = ChatStateHolder(chatClient, userScope)

}