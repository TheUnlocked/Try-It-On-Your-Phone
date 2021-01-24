package com.github.theunlocked.tryitonyourphone.network

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Klaxon
import com.github.theunlocked.tryitonyourphone.model.Language
import com.github.theunlocked.tryitonyourphone.model.LanguageCategory
import com.github.theunlocked.tryitonyourphone.model.RunRequest

data class LanguageData(val languages: List<Language>, val languageMap: Map<String, Language>) {
    companion object {
        private const val languagesUrl = "https://raw.githubusercontent.com/TryItOnline/tryitonline/master/usr/share/tio.run/languages.json"

        private var cache: LanguageData? = null

        fun get(callback: (data: LanguageData) -> Unit) {
            if (cache != null) {
                callback(cache!!)
                return
            }
            Network.download(languagesUrl) { response ->
                val items = ArrayList<Language>()
                if (response.code == 200) {
                    val text = response.body!!.charStream()
                    val json = Klaxon().parseJsonObject(text)

                    json.keys.forEach { langId ->
                        val obj = json.obj(langId)!!
                        val name = obj.string("name")!!
                        val encoding = obj.string("encoding")!!

                        val categories = obj.array<String>("categories")!!.mapNotNull(LanguageCategory::fromString)
                        if (categories.isEmpty()) return@forEach

                        var hasOptions = false
                        var hasCflags = false
                        obj.array<String>("unmask")?.forEach {
                            when (it) {
                                "options" -> hasOptions = true
                                "cflags" -> hasCflags = true
                            }
                        }

                        val lang = Language(langId, name, categories, hasOptions, hasCflags, encoding)

                        val helloWorld = obj.obj("tests")?.obj("helloWorld")?.array<JsonObject>("request")?.let { arr ->
                            var code = ""
                            var input = ""
                            var args = listOf<String>()
                            var options = listOf<String>()
                            var cflags = listOf<String>()
                            arr.forEach {
                                val payload = it.obj("payload")
                                when (payload?.keys?.first()) {
                                    ".code.tio" -> code = payload.string(".code.tio")!!
                                    ".input.tio" -> input = payload.string(".input.tio")!!
                                    "args" -> args = payload.array<String>("args")!!.toList()
                                    "TIO_CFLAGS" -> cflags = payload.array<String>("TIO_CFLAGS")!!.toList()
                                    "TIO_OPTIONS" -> options = payload.array<String>("TIO_OPTIONS")!!.toList()
                                }
                            }
                            RunRequest(lang, code, input, cflags, options, args)
                        }

                        lang.helloWorld = helloWorld
                        items.add(lang)
                    }
                }
                cache = LanguageData(items, items.map { Pair(it.id, it) }.toMap())
                callback(cache!!)
            }
        }
    }
}