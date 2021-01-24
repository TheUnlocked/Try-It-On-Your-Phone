package com.github.theunlocked.tryitonyourphone.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.github.theunlocked.tryitonyourphone.database.entities.History

@Database(entities = [History::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}