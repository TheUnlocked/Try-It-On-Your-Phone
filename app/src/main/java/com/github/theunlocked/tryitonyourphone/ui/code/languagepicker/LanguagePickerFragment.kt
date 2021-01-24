package com.github.theunlocked.tryitonyourphone.ui.code.languagepicker

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.github.theunlocked.tryitonyourphone.MainActivity
import com.github.theunlocked.tryitonyourphone.R
import com.github.theunlocked.tryitonyourphone.network.LanguageData
import com.github.theunlocked.tryitonyourphone.ui.code.CodeViewModel

/**
 * A fragment representing a list of Items.
 */
class LanguagePickerFragment : Fragment() {

    lateinit var adapter: MyLanguageItemRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        (activity as MainActivity?)?.supportActionBar?.let {
            it.setDisplayShowCustomEnabled(true)
            it.setCustomView(R.layout.action_bar_language_picker)
        }

        activity?.findViewById<TextView>(R.id.language_search_box)?.let {
            val imm = activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            it.requestFocus()
            imm.showSoftInput(it, InputMethodManager.SHOW_IMPLICIT)

            it.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

                override fun afterTextChanged(s: Editable?) {
                    adapter.filter = s?.toString() ?: ""
                }
            })
        }
    }

    override fun onStop() {
        (activity as MainActivity?)?.supportActionBar?.setDisplayShowCustomEnabled(false)
        super.onStop()
    }

    private lateinit var prefsListener: SharedPreferences.OnSharedPreferenceChangeListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_langauge_picker, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.language_list)

        activity?.getPreferences(Context.MODE_PRIVATE).let {
            adapter = MyLanguageItemRecyclerViewAdapter(
                listOf(),
                it?.getBoolean(getString(R.string.config_show_practical_languages), true) ?: true,
                it?.getBoolean(getString(R.string.config_show_recreational_languages), true) ?: true
            ) { selectedLanguage ->
                val model = ViewModelProvider(activity as MainActivity).get(CodeViewModel::class.java)
                (activity as MainActivity).saveHistoryState()
                model.language.value = selectedLanguage
                model.code.value = ""
                findNavController().navigateUp()
            }

            prefsListener = SharedPreferences.OnSharedPreferenceChangeListener { prefs, key ->
                when (key) {
                    getString(R.string.config_show_practical_languages) -> {
                        adapter.showPractical = prefs.getBoolean(getString(R.string.config_show_practical_languages), true)
                        adapter.notifyDataSetChanged()
                    }
                    getString(R.string.config_show_recreational_languages) -> {
                        adapter.showRecreational = prefs.getBoolean(getString(R.string.config_show_recreational_languages), true)
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            it?.registerOnSharedPreferenceChangeListener(prefsListener)
        }

        // Set the adapter
        recyclerView.let {
            it.layoutManager = LinearLayoutManager(context)
            it.adapter = adapter
            it.visibility = View.GONE
        }

        val loadingBar = view.findViewById<ProgressBar>(R.id.loading_bar)

        LanguageData.get {
            adapter.values = it.languages

            activity?.runOnUiThread {
                recyclerView.adapter?.notifyDataSetChanged()
                recyclerView.visibility = View.VISIBLE
                loadingBar.visibility = View.GONE
            }
        }

        return view
    }
}