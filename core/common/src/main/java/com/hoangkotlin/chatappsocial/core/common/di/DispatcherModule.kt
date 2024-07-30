package com.hoangkotlin.chatappsocial.core.common.di

import com.hoangkotlin.chatappsocial.core.common.Dispatcher
import com.hoangkotlin.chatappsocial.core.common.SocialDispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {
    @Provides
    @Dispatcher(SocialDispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    @Provides
    @Dispatcher(SocialDispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default

    @Provides
    @Dispatcher(SocialDispatchers.Main)
    fun providesMainDispatcher(): CoroutineDispatcher = Dispatchers.Default
}