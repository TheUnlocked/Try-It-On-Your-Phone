package com.github.theunlocked.tryitonyourphone.model

import java.util.*

data class Language(
    val id: String,
    val name: String,
    val categories: List<LanguageCategory>,
    val hasOptions: Boolean,
    val hasCflags: Boolean,
    val encoding: String,
    var helloWorld: RunRequest? = null
) {
    override fun toString(): String = id

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Language

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }
}

enum class LanguageCategory {
    RECREATIONAL,
    PRACTICAL;

    companion object {
        fun fromString(name: String) =
            when (name.toLowerCase(Locale.getDefault())) {
                "recreational" -> RECREATIONAL
                "practical" -> PRACTICAL
                else -> null
            }
    }
}