package com.hoangkotlin.chatappsocial.core.database.attachment

import androidx.room.Dao
import androidx.room.Query
import com.hoangkotlin.chatappsocial.core.database.model.CHAT_ATTACHMENT_ENTITY
import com.hoangkotlin.chatappsocial.core.database.model.ChatAttachmentEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatAttachmentDao {

    @Query("SELECT * FROM $CHAT_ATTACHMENT_ENTITY WHERE messageId == :messageId")
    fun observeAttachmentsForMessage(messageId: String): Flow<List<ChatAttachmentEntity>>

    @Query("DELETE FROM $CHAT_ATTACHMENT_ENTITY")
    suspend fun deleteAll()
}