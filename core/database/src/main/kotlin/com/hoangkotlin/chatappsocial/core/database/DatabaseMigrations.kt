package com.hoangkotlin.chatappsocial.core.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.hoangkotlin.chatappsocial.core.database.model.CHAT_ATTACHMENT_ENTITY

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        // Add the new column to the table
        db.execSQL("ALTER TABLE $CHAT_ATTACHMENT_ENTITY ADD COLUMN createdAt INTEGER")
    }
}