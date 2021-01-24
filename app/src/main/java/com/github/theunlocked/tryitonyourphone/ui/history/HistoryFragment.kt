package com.github.theunlocked.tryitonyourphone.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.theunlocked.tryitonyourphone.MainActivity
import com.github.theunlocked.tryitonyourphone.R
import com.github.theunlocked.tryitonyourphone.database.entities.History
import com.github.theunlocked.tryitonyourphone.network.LanguageData
import com.github.theunlocked.tryitonyourphone.ui.code.CodeViewModel
import kotlinx.coroutines.launch

class HistoryFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onStop() {
        (activity as MainActivity?)?.supportActionBar?.setDisplayShowCustomEnabled(false)
        super.onStop()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val recyclerView = view.findViewById<RecyclerView>(R.id.history_list)
        val loadingBar = view.findViewById<ProgressBar>(R.id.loading_bar)

        lifecycleScope.launch {
            val items = ArrayList((activity as MainActivity).database.historyDao().getAll())

            val adapter = MyHistoryItemRecyclerViewAdapter(
                items,
                null,
                {
                    val codeModel = ViewModelProvider(activity as MainActivity).get(CodeViewModel::class.java)
                    if (codeModel.language.value != null &&
                        codeModel.code.value != null &&
                        items.none { it.isSimilarTo(History(codeModel.language.value!!.id, codeModel.code.value!!)) }) {
                        (activity as MainActivity).saveHistoryState()
                    }
                    LanguageData.get { languageData ->
                        codeModel.language.value = languageData.languageMap[it.languageId]
                        codeModel.code.value = it.code
                        findNavController().navigate(R.id.navigation_code)
                    }
                },
                {
                    items.remove(it)
                    recyclerView.adapter?.notifyDataSetChanged()
                    lifecycleScope.launch { (activity as MainActivity).database.historyDao().delete(it) }
                }
            )

            recyclerView.let {
                it.adapter = adapter
                it.layoutManager = LinearLayoutManager(context)
            }

            LanguageData.get {
                activity?.runOnUiThread {
                    adapter.languageData = it
                    loadingBar.visibility = View.GONE
                    recyclerView.visibility = View.VISIBLE
                }
            }
        }

        return view
    }
}