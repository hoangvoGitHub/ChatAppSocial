package com.hoangkotlin.chatappsocial.core.database.channel

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.hoangkotlin.chatappsocial.core.database.model.CHAT_CHANNEL_TABLE
import com.hoangkotlin.chatappsocial.core.database.model.ChatChannelEntity
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus
//import com.hoangkotlin.chatappsocial.core.database.channel.model.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatChannelDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatChannelEntity: ChatChannelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(chatChannelEntities: List<ChatChannelEntity>)

    @Query("SELECT id FROM $CHAT_CHANNEL_TABLE")
    suspend fun getAllCids(): List<String>

    @Query(
        "SELECT id FROM $CHAT_CHANNEL_TABLE " +
                "WHERE syncStatus = :syncStatus " +
                "ORDER BY syncStatus ASC " +
                "LIMIT :limit"
    )
    suspend fun getCidsBySyncNeeded(
        syncStatus: SyncStatus = SyncStatus.SYNC_NEEDED,
        limit: Int = NO_LIMIT
    ): List<String>
     
    @Query(
        "SELECT * FROM $CHAT_CHANNEL_TABLE " +
                "WHERE syncStatus = :syncStatus " +
                "ORDER BY syncStatus ASC " +
                "LIMIT :limit"
    )
    suspend fun getAllBySyncNeeded(
        syncStatus: SyncStatus = SyncStatus.SYNC_NEEDED,
        limit: Int = NO_LIMIT
    ): List<ChatChannelEntity>

    @Query(
        "SELECT * FROM $CHAT_CHANNEL_TABLE " +
                "WHERE $CHAT_CHANNEL_TABLE.id IN (:cids)"
    )
    suspend fun getAll(cids: List<String>): ChatChannelEntity

    @Query(
        "SELECT * FROM $CHAT_CHANNEL_TABLE " +
                "WHERE $CHAT_CHANNEL_TABLE.id IN (:cids)"
    )
    fun getAll(cids: String): Flow<ChatChannelEntity?>

    @Query("DELETE from $CHAT_CHANNEL_TABLE WHERE id = :cid")
    suspend fun delete(cid: String)
    
    

    private companion object {
        private const val NO_LIMIT: Int = -1
    }
}