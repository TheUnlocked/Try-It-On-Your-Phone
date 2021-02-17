package com.github.theunlocked.tryitonyourphone.database.entities

enum class HistoryKind {
    NORMAL,
    UNSAVED;

    companion object {
        val values = values()
    }
}