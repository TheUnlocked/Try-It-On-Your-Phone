package com.github.theunlocked.tryitonyourphone.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.github.theunlocked.tryitonyourphone.database.entities.History
import com.github.theunlocked.tryitonyourphone.database.entities.HistoryKind

@Dao
abstract class HistoryDao {
    @Query("SELECT * FROM history ORDER BY created_at DESC")
    abstract suspend fun getAll(): List<History>

    @Query("SELECT * FROM history WHERE id = :id LIMIT 1")
    abstract suspend fun getById(id: Int): History?

    @Query("SELECT * FROM history ORDER BY created_at DESC LIMIT 1")
    abstract suspend fun getLatestHistory(): History?

    @Insert
    protected abstract suspend fun insertRaw(history: History)

    /**
     * Inserts a History into the database. Timestamps are automatically generated.
     */
    suspend fun insert(history: History) =
            insertRaw(history.copy(createdAt = System.currentTimeMillis()))

    @Delete
    abstract suspend fun delete(history: History)

    @Query("UPDATE history SET kind = :to WHERE kind = :from")
    protected abstract suspend fun changeKindToKind(from: HistoryKind, to: HistoryKind)

    suspend fun saveUnsaved() = changeKindToKind(HistoryKind.UNSAVED, HistoryKind.NORMAL)

    @Query("DELETE FROM history WHERE kind = :kind")
    protected abstract suspend fun deleteOfKind(kind: HistoryKind)

    suspend fun deleteUnsaved() = deleteOfKind(HistoryKind.UNSAVED)
}