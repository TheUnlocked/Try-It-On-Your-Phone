package com.github.theunlocked.tryitonyourphone.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.theunlocked.tryitonyourphone.database.entities.History

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY created_at DESC")
    suspend fun getAll(): List<History>

    @Query("SELECT * FROM history WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): History?

    @Query("SELECT * FROM history ORDER BY created_at DESC LIMIT 1")
    suspend fun getLatestHistory(): History?

    @Insert
    suspend fun insert(history: History)

    suspend fun insertWithTimestamp(history: History) {
        insert(history.copy(createdAt = System.currentTimeMillis()))
    }

    @Delete
    suspend fun delete(history: History)
}