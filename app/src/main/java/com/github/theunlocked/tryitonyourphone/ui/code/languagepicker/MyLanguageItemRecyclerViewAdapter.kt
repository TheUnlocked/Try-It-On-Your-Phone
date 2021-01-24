package com.github.theunlocked.tryitonyourphone.ui.code.languagepicker

import android.content.SharedPreferences
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import com.github.theunlocked.tryitonyourphone.R
import com.github.theunlocked.tryitonyourphone.model.Language
import com.github.theunlocked.tryitonyourphone.model.LanguageCategory
import java.util.*

/**
 * [RecyclerView.Adapter] that can display a [Language].
 */
class MyLanguageItemRecyclerViewAdapter(
    var values: List<Language>,
    var showPractical: Boolean,
    var showRecreational: Boolean,
    private val itemClickListener: (item: Language) -> Unit
) : RecyclerView.Adapter<MyLanguageItemRecyclerViewAdapter.ViewHolder>() {

    var filter = ""
        set(value) {
            field = value.toLowerCase(Locale.getDefault())
            notifyDataSetChanged()
        }

    private val items get() = values.asSequence()
        .filter { (it.categories.contains(LanguageCategory.PRACTICAL) && showPractical) ||
                (it.categories.contains(LanguageCategory.RECREATIONAL) && showRecreational) }
        .map { Pair(it, it.name.toLowerCase(Locale.getDefault()).indexOf(filter)) }
        .filter { (_, index) -> index > -1 }
        .sortedBy { (_, index) -> index }
        .map { (it, _) -> it }
        .toList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.langauge_picker_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.idView.text = item.name
        holder.itemView.setOnClickListener { itemClickListener(item) }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val idView: TextView = view.findViewById(R.id.language_name)

        override fun toString(): String {
            return super.toString() + " '" + idView.text + "'"
        }
    }
}