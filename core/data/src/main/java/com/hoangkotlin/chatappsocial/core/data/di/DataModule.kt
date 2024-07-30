package com.hoangkotlin.chatappsocal.core.data.di

import com.hoangkotlin.chatappsocial.core.data.repository.app.AppDataRepository
import com.hoangkotlin.chatappsocial.core.data.repository.auth.AuthRepository
import com.hoangkotlin.chatappsocial.core.data.repository.channel.ChatChannelRepository
import com.hoangkotlin.chatappsocial.core.data.repository.channel.ChatChannelRepositoryImpl
import com.hoangkotlin.chatappsocial.core.data.repository.message.ChatMessageRepository
import com.hoangkotlin.chatappsocial.core.data.repository.message.ChatMessageRepositoryImpl
import com.hoangkotlin.chatappsocial.core.data.repository.user.ChatUserRepository
import com.hoangkotlin.chatappsocial.core.data.repository.user.ChatUserRepositoryImpl
import com.hoangkotlin.chatappsocial.core.data.repository.app.DefaultAppDataRepository
import com.hoangkotlin.chatappsocial.core.data.repository.DefaultSearchRepository
import com.hoangkotlin.chatappsocial.core.data.repository.SearchRepository
import com.hoangkotlin.chatappsocial.core.data.repository.attachment.ChatAttachmentRepository
import com.hoangkotlin.chatappsocial.core.data.repository.attachment.ChatAttachmentRepositoryImpl
import com.hoangkotlin.chatappsocial.core.data.repository.auth.SocialAuthRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    fun bindsAuthRepository(
        authRepository: SocialAuthRepository
    ): AuthRepository


    @Binds
    fun bindsChatUserRepository(
        chatUserRepository: ChatUserRepositoryImpl
    ): ChatUserRepository

    @Binds
    fun bindsChatMessageRepository(
        chatMessageRepository: ChatMessageRepositoryImpl
    ): ChatMessageRepository

    @Binds
    fun bindsChatChannelRepository(
        chatChannelRepository: ChatChannelRepositoryImpl
    ): ChatChannelRepository


    @Binds
    fun bindsChatAttachmentRepository(
        chatAttachmentRepository: ChatAttachmentRepositoryImpl
    ): ChatAttachmentRepository

    @Binds
    fun bindsSearchRepository(
        searchRepository: DefaultSearchRepository
    ):SearchRepository

    @Binds
    fun bindsAppDataRepository(
        searchRepository: DefaultAppDataRepository
    ): AppDataRepository


}