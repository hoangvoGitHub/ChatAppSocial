package com.hoangkotlin.chatappsocial.core.database.di

import android.content.Context
import androidx.room.Room
import com.hoangkotlin.chatappsocial.core.database.ChatAppDatabase
import com.hoangkotlin.chatappsocial.core.database.MIGRATION_3_4
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideChatAppDatabase(
        @ApplicationContext context: Context,
    // TODO:  inject a datastore reference here to get userId (for multiple users app)
    ): ChatAppDatabase {
        return Room.databaseBuilder(
            context = context,
            ChatAppDatabase::class.java,
            "chat-app-database"
        )
            .addMigrations(MIGRATION_3_4)
            .build()
    }


    @Provides
    fun providesChatChannelDao(
        database: ChatAppDatabase
    ) = database.channelDao

    @Provides
    fun providesChatUserDao(
        database: ChatAppDatabase
    ) = database.userDao

    @Provides
    fun providesChatMessageDao(
        database: ChatAppDatabase
    ) = database.messageDao

    @Provides
    fun providesChatAttachmentDao(
        database: ChatAppDatabase
    ) = database.attachmentDao
}

