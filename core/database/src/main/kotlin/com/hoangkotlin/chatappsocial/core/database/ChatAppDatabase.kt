package com.hoangkotlin.chatappsocial.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.hoangkotlin.chatappsocial.core.database.attachment.ChatAttachmentDao
import com.hoangkotlin.chatappsocial.core.database.channel.ChatChannelDao
import com.hoangkotlin.chatappsocial.core.database.converters.DateConverter
import com.hoangkotlin.chatappsocial.core.database.converters.ExtraDataConverter
import com.hoangkotlin.chatappsocial.core.database.converters.MapConverter
import com.hoangkotlin.chatappsocial.core.database.converters.MemberConverter
import com.hoangkotlin.chatappsocial.core.database.converters.SyncStatusConverter
import com.hoangkotlin.chatappsocial.core.database.message.ChatMessageDao
import com.hoangkotlin.chatappsocial.core.database.model.ChatAttachmentEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatChannelEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatMessageEntity
import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import com.hoangkotlin.chatappsocial.core.database.user.ChatUserDao

@Database(
    entities = [
        ChatUserEntity::class,
        ChatMessageEntity::class,
        ChatChannelEntity::class,
        ChatAttachmentEntity::class
    ],
    exportSchema = false,
    version = 4,
)
@TypeConverters(
    value = [
        DateConverter::class,
        ExtraDataConverter::class,
        MapConverter::class,
        MemberConverter::class,
        SyncStatusConverter::class
    ]
)
abstract class ChatAppDatabase : RoomDatabase() {
    abstract val channelDao: ChatChannelDao
    abstract val userDao: ChatUserDao
    abstract val attachmentDao: ChatAttachmentDao
    abstract val messageDao: ChatMessageDao
}