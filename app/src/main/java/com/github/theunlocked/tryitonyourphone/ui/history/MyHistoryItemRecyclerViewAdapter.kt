package com.github.theunlocked.tryitonyourphone.ui.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.theunlocked.tryitonyourphone.R
import com.github.theunlocked.tryitonyourphone.database.entities.History
import com.github.theunlocked.tryitonyourphone.network.LanguageData
import java.text.DateFormat
import java.util.*

data class MyHistoryItemRecyclerViewAdapter (
    var items: List<History>,
    var languageData: LanguageData?,
    private val itemClickListener: (item: History) -> Unit,
    private val itemDeleteListener: (item: History) -> Unit
) : RecyclerView.Adapter<MyHistoryItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.history_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]

        holder.langView.text = languageData?.languageMap?.get(item.languageId)?.name ?: item.languageId

        val dateFormatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault())
        holder.timestampView.text = dateFormatter.format(Date(item.createdAt))

        holder.itemView.setOnClickListener { itemClickListener(item) }
        holder.deleteBtn.setOnClickListener { itemDeleteListener(item) }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val langView: TextView = view.findViewById(R.id.history_item_language)
        val timestampView: TextView = view.findViewById(R.id.history_item_timestamp)
        val deleteBtn: Button = view.findViewById(R.id.history_item_delete_button)
    }
}