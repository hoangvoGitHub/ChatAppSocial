package com.hoangkotlin.chatappsocial.core.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.hoangkotlin.chatappsocial.core.database.attachment.ChatAttachmentDao
import com.hoangkotlin.chatappsocial.core.database.channel.ChatChannelDao
import com.hoangkotlin.chatappsocial.core.database.model.ChatChannelEntity
import com.hoangkotlin.chatappsocial.core.database.message.ChatMessageDao
import com.hoangkotlin.chatappsocial.core.database.model.ChatMessageEntity
import com.hoangkotlin.chatappsocial.core.database.model.WrapperChatMessageEntity
import com.hoangkotlin.chatappsocial.core.database.user.ChatUserDao
import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.util.Date
import kotlin.test.assertEquals

class EntityReadWriteTest {
    private lateinit var userDao: ChatUserDao
    private lateinit var messageDao: ChatMessageDao
    private lateinit var channelDao: ChatChannelDao
    private lateinit var attachmentDao: ChatAttachmentDao
    private lateinit var db: ChatAppDatabase

    @Before
    fun createDatabase() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, ChatAppDatabase::class.java
        ).allowMainThreadQueries().build()
        userDao = db.userDao
        messageDao = db.messageDao
        channelDao = db.channelDao
        attachmentDao = db.attachmentDao
    }

    @After
    @Throws(IOException::class)
    fun closeDatabase() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun testWriteAndReadUser() = runTest {
        val user: ChatUserEntity = TestUtil.createChatUser("test")
        userDao.insert(user)
        val byName = userDao.select("test")
        assertEquals(byName, user)
    }

    @Test
    @Throws(Exception::class)
    fun testWriteAndReadMessageById() = runTest {
        val message: ChatMessageEntity = TestUtil.createChatMessage("test")
        messageDao.insert(message)
        val byId = messageDao.select("test")?.chatMessageEntity
        assertEquals(byId, message)
    }

    @Test
    @Throws(Exception::class)
    fun testWriteAndReadMessageByChannel() = runTest {
        val messageList = mutableListOf<ChatMessageEntity>()
        val cid = "test_cid"
        val createdAt = Date()
        repeat(5) {
            val message: ChatMessageEntity = TestUtil.createChatMessage(
                id = "test$it",
                cid = cid,
                createdAt = createdAt
            )
            messageList.add(message)
            messageDao.insert(message)
        }
        val byChannel = messageDao.selectMessagesForChannelEqualOrOlderThan(
            cid,
            100,
            createdAt
        )
        println("TEST - testWriteAndReadMessageByChannel - EXPECTED  $messageList")
        println("TEST - testWriteAndReadMessageByChannel - ACTUAL  $byChannel")
        assertEquals(byChannel.map(WrapperChatMessageEntity::chatMessageEntity), messageList)
    }

    @Test
    @Throws(Exception::class)
    fun testWriteAndReadChanel() = runTest {
        val channel: ChatChannelEntity = TestUtil.createChatChannel(
            "test",
            members = TestUtil.createMember(5),
            extraData = TestUtil.createExtraData(0),
        )
        println("TEST - testWriteAndReadChanel - BEFORE  $channel()")
        channelDao.insert(channel)
        val byId = channelDao.getAll("test").first()
        println("TEST - testWriteAndReadChanel - AFTER $byId()")
        assertEquals(byId, channel)
    }


}