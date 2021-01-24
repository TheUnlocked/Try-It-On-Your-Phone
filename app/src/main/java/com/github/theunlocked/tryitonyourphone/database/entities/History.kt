package com.github.theunlocked.tryitonyourphone.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(tableName = "history")
data class History(
    @ColumnInfo(name = "language_id") val languageId: String,
    @ColumnInfo(name = "code") val code: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "created_at") val createdAt: Long = 0
) {
    fun isSimilarTo(other: History): Boolean =
        languageId == other.languageId && code == other.code
}