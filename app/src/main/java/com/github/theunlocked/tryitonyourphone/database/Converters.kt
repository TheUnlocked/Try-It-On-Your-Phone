package com.github.theunlocked.tryitonyourphone.database

import androidx.room.TypeConverter
import com.github.theunlocked.tryitonyourphone.database.entities.HistoryKind

class Converters {
    @TypeConverter
    fun fromHistoryKind(kind: HistoryKind): Int {
        return kind.ordinal
    }

    @TypeConverter
    fun toHistoryKind(kind: Int): HistoryKind {
        return HistoryKind.values[kind]
    }
}