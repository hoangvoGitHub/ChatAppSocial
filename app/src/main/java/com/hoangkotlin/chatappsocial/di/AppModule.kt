package com.hoangkotlin.chatappsocial.di

import com.hoangkotlin.chatappsocial.core.offline.interceptor.DefaultSendMessageInterceptor
import com.hoangkotlin.chatappsocial.core.offline.state.ChatStateHolder
import com.hoangkotlin.chatappsocial.ui.main.SocialPluginsProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import javax.inject.Singleton

@Module
@InstallIn(ViewModelComponent::class)
object AppModule {

    @Provides
    fun providesSocialPluginsProvider(
        stateHolder: ChatStateHolder,
        interceptor: DefaultSendMessageInterceptor
    ): SocialPluginsProvider {
        return SocialPluginsProvider(
            stateHolder, interceptor
        )
    }
}