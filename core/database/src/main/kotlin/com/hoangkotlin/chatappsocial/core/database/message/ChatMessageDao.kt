package com.hoangkotlin.chatappsocial.core.database.message

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.hoangkotlin.chatappsocial.core.database.model.CHAT_MESSAGE_ENTITY
import com.hoangkotlin.chatappsocial.core.database.model.ChatMessageEntity
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
import com.hoangkotlin.chatappsocial.core.database.model.CHAT_ATTACHMENT_ENTITY
import com.hoangkotlin.chatappsocial.core.database.model.ChatAttachmentEntity
import com.hoangkotlin.chatappsocial.core.database.model.WrapperChatMessageEntity
import java.util.Date

@Dao
interface ChatMessageDao {

    @Transaction
    suspend fun insert(messageEntities: List<WrapperChatMessageEntity>) {
        upsertMessageEntities(messageEntities.map(WrapperChatMessageEntity::chatMessageEntity))
        deleteAttachments(messageEntities.map { it.chatMessageEntity.id })
        insertAttachments(messageEntities.flatMap(WrapperChatMessageEntity::attachments))
    }

    @Transaction
    suspend fun upsertMessageEntities(messageInnerEntities: List<ChatMessageEntity>) {
        val rowIds = insertMessageEntities(messageInnerEntities)
        val entitiesToUpdate = rowIds.mapIndexedNotNull { index, rowId ->
            if (rowId == -1L) messageInnerEntities[index] else null
        }
        entitiesToUpdate.forEach { updateMessageEntity(it) }
    }

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messageEntity: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessageEntities(messageEntities: List<ChatMessageEntity>): List<Long>

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessageEntity(messageInnerEntity: ChatMessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAttachments(attachmentEntities: List<ChatAttachmentEntity>)

    @Query(
        "SELECT * from $CHAT_MESSAGE_ENTITY " +
                "WHERE cid = :cid " +
                "ORDER BY CASE WHEN createdAt " +
                "IS NULL THEN createdLocallyAt " +
                "ELSE createdAt " +
                "END DESC LIMIT :limit"
    )
    // https://developer.android.com/training/data-storage/room/relationships#one-to-one
    // add @Transaction because functions that return WrapperChatMessageEntity will call two queries
    @Transaction
    fun selectMessagesForChannel(
        cid: String, limit: Int = 100,
    ): List<WrapperChatMessageEntity>

    @Query(
        "SELECT * from $CHAT_MESSAGE_ENTITY " +
                "WHERE cid = :cid " +
                "AND (createdAt > :date || createdLocallyAt > :date) " +
                "ORDER BY CASE WHEN createdAt " +
                "IS NULL THEN createdLocallyAt " +
                "ELSE createdAt " +
                "END DESC LIMIT :limit"
    )
   @Transaction
    fun selectMessagesForChannelNewerThan(
        cid: String, limit: Int = 100,
        date: Date
    ): List<WrapperChatMessageEntity>

    @Query(
        "SELECT * from $CHAT_MESSAGE_ENTITY " +
                "WHERE cid = :cid " +
                "AND (createdAt >= :date || createdLocallyAt >= :date) " +
                "ORDER BY CASE WHEN createdAt " +
                "IS NULL THEN createdLocallyAt " +
                "ELSE createdAt " +
                "END DESC LIMIT :limit"
    )
    @Transaction
    fun selectMessagesForChannelEqualOrNewerThan(
        cid: String, limit: Int = 100,
        date: Date
    ): List<WrapperChatMessageEntity>

    @Query(
        "SELECT * from $CHAT_MESSAGE_ENTITY " +
                "WHERE cid = :cid " +
                "AND (createdAt < :date || createdLocallyAt < :date) " +
                "ORDER BY CASE WHEN createdAt " +
                "IS NULL THEN createdLocallyAt " +
                "ELSE createdAt " +
                "END DESC LIMIT :limit"
    )
    @Transaction
    fun selectMessagesForChannelOlderThan(
        cid: String, limit: Int = 100,
        date: Date
    ): List<WrapperChatMessageEntity>

    @Query(
        "SELECT * from $CHAT_MESSAGE_ENTITY " +
                "WHERE cid = :cid " +
                "AND (createdAt <= :date || createdLocallyAt <= :date) " +
                "ORDER BY CASE WHEN createdAt " +
                "IS NULL THEN createdLocallyAt " +
                "ELSE createdAt " +
                "END DESC LIMIT :limit"
    )
    @Transaction
    fun selectMessagesForChannelEqualOrOlderThan(
        cid: String, limit: Int = 100,
        date: Date
    ): List<WrapperChatMessageEntity>

    @Query(
        "DELETE from $CHAT_MESSAGE_ENTITY " +
                "WHERE cid = :cid " +
                "AND createdAt < :deleteMessagesBefore"
    )
    suspend fun deleteChannelMessagesBefore(cid: String, deleteMessagesBefore: Date)

    @Query(
        "DELETE from $CHAT_MESSAGE_ENTITY " +
                "WHERE id = :messageId "
    )
    suspend fun deleteMessage(messageId: String)

    @Transaction
    suspend fun select(ids: List<String>): List<WrapperChatMessageEntity> {
        return ids.chunked(SQLITE_MAX_VARIABLE_NUMBER)
            .flatMap { messageIds -> selectChunked(messageIds) }
    }

    @Query("SELECT * FROM $CHAT_MESSAGE_ENTITY WHERE id IN (:ids)")
    @Transaction
    suspend fun selectChunked(ids: List<String>): List<WrapperChatMessageEntity>

    @Query("SELECT * FROM $CHAT_MESSAGE_ENTITY WHERE id IN (:id)")
    @Transaction
    suspend fun select(id: String): WrapperChatMessageEntity?

    @Query(
        "SELECT * FROM $CHAT_MESSAGE_ENTITY " +
                "WHERE syncStatus = :syncStatus " +
                "ORDER BY CASE WHEN createdAt IS NULL THEN createdLocallyAt ELSE createdAt END ASC " +
                "LIMIT :limit"
    )
    @Transaction
    suspend fun selectBySyncStatus(
        syncStatus: SyncStatus,
        limit: Int = NO_LIMIT
    ): List<WrapperChatMessageEntity>

    @Transaction
    fun deleteAttachments(messageIds: List<String>) {
        messageIds.chunked(SQLITE_MAX_VARIABLE_NUMBER).forEach(::deleteAttachmentsChunked)
    }

    @Query("DELETE FROM $CHAT_ATTACHMENT_ENTITY  WHERE id in (:messageIds)")
    fun deleteAttachmentsChunked(messageIds: List<String>)

    @Query(
        "SELECT id FROM $CHAT_MESSAGE_ENTITY " +
                "WHERE syncStatus = :syncStatus " +
                "ORDER BY CASE WHEN createdAt IS NULL THEN createdLocallyAt ELSE createdAt END ASC " +
                "LIMIT :limit"
    )
    suspend fun selectIdsBySyncStatus(syncStatus: SyncStatus, limit: Int = NO_LIMIT): List<String>

    @Query("DELETE FROM $CHAT_MESSAGE_ENTITY")
    suspend fun deleteAll()

    private companion object {
        private const val SQLITE_MAX_VARIABLE_NUMBER: Int = 999
        private const val NO_LIMIT: Int = -1
    }

}