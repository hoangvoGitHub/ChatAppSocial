package com.hoangkotlin.chatappsocial.core.database.user

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hoangkotlin.chatappsocial.core.database.model.CHAT_USER_ENTITY
import com.hoangkotlin.chatappsocial.core.database.model.ChatUserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatUserEntity: ChatUserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatUserEntities: List<ChatUserEntity>)

    @Query("SELECT * FROM $CHAT_USER_ENTITY WHERE id LIKE :id")
    suspend fun select(id: String): ChatUserEntity?

    @Query("SELECT * FROM $CHAT_USER_ENTITY WHERE id IN (:ids)")
    suspend fun select(ids: List<String>): List<ChatUserEntity>

    @Query(
        "SELECT * FROM $CHAT_USER_ENTITY " +
                "ORDER BY name ASC " +
                "LIMIT :limit OFFSET :offset"
    )
    suspend fun selectAll(limit: Int, offset: Int): List<ChatUserEntity>

    @Query(
        "SELECT * FROM $CHAT_USER_ENTITY " +
                "WHERE lower(name) LIKE :queryKey " +
                "ORDER BY name ASC " +
                "LIMIT :limit OFFSET :offset"
    )
    fun observeUserByName(
        queryKey: String,
        limit: Int,
        offset: Int
    ): Flow<List<ChatUserEntity>>




}