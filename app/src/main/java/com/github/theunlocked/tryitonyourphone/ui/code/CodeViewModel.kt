package com.github.theunlocked.tryitonyourphone.ui.code

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.theunlocked.tryitonyourphone.model.Language
import java.util.*
import kotlin.collections.ArrayList

class CodeViewModel : ViewModel() {
    var language: MutableLiveData<Language?> = MutableLiveData(null)

    var code: MutableLiveData<String> = MutableLiveData("")
    var argv: MutableLiveData<List<String>> = MutableLiveData(ArrayList())
    var cflags: MutableLiveData<List<String>> = MutableLiveData(ArrayList())
    var options: MutableLiveData<List<String>> = MutableLiveData(ArrayList())

    var undoHistory: MutableLiveData<Stack<String>> = MutableLiveData(Stack())
    var redoHistory: MutableLiveData<Stack<String>> = MutableLiveData(Stack())
}