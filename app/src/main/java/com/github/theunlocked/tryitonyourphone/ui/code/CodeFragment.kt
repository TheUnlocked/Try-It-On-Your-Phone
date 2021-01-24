package com.github.theunlocked.tryitonyourphone.ui.code

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.github.theunlocked.tryitonyourphone.MainActivity
import com.github.theunlocked.tryitonyourphone.R
import kotlin.math.max

class CodeFragment : Fragment() {

    private lateinit var model: CodeViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_code, container, false)
        model = ViewModelProvider(activity as MainActivity).get(CodeViewModel::class.java)

        val editor = root.findViewById<EditText>(R.id.code_editor)
        model.language.observe(viewLifecycleOwner) { editor.isEnabled = it != null }
        model.code.observe(viewLifecycleOwner) { if (editor.text.toString() != it) editor.setText(it) }

        var isRestoring = true

        editor.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (isRestoring) {
                    isRestoring = false
                    editor.setText(model.code.value)
                    return
                }
                if (model.code.value != s.toString()) {
                    if (s != null &&
                        s.toString().length > model.code.value?.length ?: 0 &&
                        editor.selectionStart == editor.selectionEnd &&
                        s.length >= editor.selectionStart &&
                        editor.selectionStart > 0 &&
                        s[editor.selectionStart - 1] == '\n') {

                        val prevLine = editor.layout.getLineForOffset(editor.selectionStart - 2)
                        val prevLineStart = editor.layout.getLineStart(prevLine)
                        var i = 0
                        while (s[prevLineStart + i] == ' ') i++
                        s.replace(editor.selectionStart, editor.selectionStart, " ".repeat(i))
                    }
                    model.code.value = s.toString()
                }
            }
        })

        root.findViewById<Button>(R.id.editor_dedent_button).setOnClickListener {
            val selectionStart = editor.selectionStart
            val selectionEnd = editor.selectionEnd

            var finalSelectionStart = selectionStart
            var finalSelectionEnd = selectionEnd

            val startLine = editor.layout.getLineForOffset(selectionStart)
            val endLine = editor.layout.getLineForOffset(selectionEnd)
            for (line in startLine..endLine) {
                val firstChar = editor.layout.getLineStart(line)
                val text = editor.text
                var endOfIndent = firstChar
                // 2-wide indents
                for (i in 0..1) {
                    if (text[endOfIndent] == ' ') {
                        endOfIndent++
                        if (line == startLine) finalSelectionStart--
                        finalSelectionEnd--
                    }
                    else break
                }
                text.delete(firstChar, endOfIndent)
            }

            editor.setSelection(
                max(editor.layout.getLineStart(startLine), finalSelectionStart),
                max(editor.layout.getLineStart(startLine), finalSelectionEnd)
            )
        }

        root.findViewById<Button>(R.id.editor_indent_button).setOnClickListener {
            val selectionStart = editor.selectionStart
            val selectionEnd = editor.selectionEnd

            val startLine = editor.layout.getLineForOffset(selectionStart)
            val endLine = editor.layout.getLineForOffset(selectionEnd)
            for (line in startLine..endLine) {
                val firstChar = editor.layout.getLineStart(line)
                editor.text.insert(firstChar, "  ")
            }

            editor.setSelection(selectionStart + 2, selectionEnd + (endLine - startLine + 1) * 2)
        }

        fun putTextAtCursor(text: CharSequence) {
            editor.text.replace(editor.selectionStart, editor.selectionEnd, text)
        }

        root.findViewById<Button>(R.id.editor_left_brace_button).setOnClickListener { putTextAtCursor("{") }
        root.findViewById<Button>(R.id.editor_right_brace_button).setOnClickListener { putTextAtCursor("}") }
        root.findViewById<Button>(R.id.editor_semicolon_button).setOnClickListener { putTextAtCursor(";") }

        return root
    }
}