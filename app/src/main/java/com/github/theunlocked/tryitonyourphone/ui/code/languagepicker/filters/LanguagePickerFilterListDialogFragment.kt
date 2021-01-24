package com.github.theunlocked.tryitonyourphone.ui.code.languagepicker.filters

import android.content.Context
import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.theunlocked.tryitonyourphone.R

/**
 *
 * A fragment that shows a list of items as a modal bottom sheet.
 *
 * You can show this modal bottom sheet from your activity like this:
 * <pre>
 *    LanguagePickerFilterListDialogFragment.newInstance(30).show(supportFragmentManager, "dialog")
 * </pre>
 */
class LanguagePickerFilterListDialogFragment : BottomSheetDialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_language_picker_filter_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<RecyclerView>(R.id.language_filter_list)?.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = LanguagePickerFilterAdapter()
        }
    }

    private inner class ViewHolder constructor(inflater: LayoutInflater, parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.language_picker_filter_dialog_item, parent, false)) {

        val checkbox: CheckBox = itemView.findViewById(R.id.language_filter_checkbox)
    }

    private inner class LanguagePickerFilterAdapter : RecyclerView.Adapter<ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(LayoutInflater.from(parent.context), parent)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            activity?.let { activity ->
                val prefs = activity.getPreferences(Context.MODE_PRIVATE)
                when (position) {
                    0 -> {
                        with (holder.checkbox) {
                            text = getString(R.string.filter_show_practical)
                            isChecked = prefs.getBoolean(getString(R.string.config_show_practical_languages), true)
                            setOnCheckedChangeListener { _, checked ->
                                prefs.edit().apply {
                                    putBoolean(getString(R.string.config_show_practical_languages), checked)
                                    apply()
                                }
                            }
                        }
                    }
                    1 -> {
                        with (holder.checkbox) {
                            text = resources.getString(R.string.filter_show_recreational)
                            isChecked = prefs.getBoolean(getString(R.string.config_show_recreational_languages), true)
                            setOnCheckedChangeListener { _, checked ->
                                prefs.edit().apply {
                                    putBoolean(getString(R.string.config_show_recreational_languages), checked)
                                    apply()
                                }
                            }
                        }
                    }
                }
            }
        }

        override fun getItemCount() = 2
    }

//    companion object {
//
//        fun newInstance(showPractical: Boolean, showRecreational: Boolean): LanguagePickerFilterListDialogFragment =
//                LanguagePickerFilterListDialogFragment().apply {
//                    arguments = Bundle().apply {
//                        putBoolean(SHOW_PRACTICAL, showPractical)
//                        putBoolean(SHOW_RECREATIONAL, showRecreational)
//                    }
//                }
//
//    }
}