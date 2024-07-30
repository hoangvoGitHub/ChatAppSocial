package com.hoangkotlin.chatappsocial.core.database.converters

import androidx.room.TypeConverter
import com.hoangkotlin.chatappsocial.core.common.utils.SyncStatus

class SyncStatusConverter {
    @TypeConverter
    fun stringToSyncStatus(data: Int): SyncStatus {
        return SyncStatus.fromInt(data)!!
    }

    @TypeConverter
    fun syncStatusToString(syncStatus: SyncStatus): Int {
        return syncStatus.status
    }
}