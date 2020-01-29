package com.solkismet.urbandictionary.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.databinding.ListItemSearchBinding
import com.solkismet.urbandictionary.viewmodels.SearchViewModel
import com.solkismet.urbandictionary.viewmodels.WordDetailViewModel

class SearchListAdapter(private val onItemClicked: SearchViewModel.OnItemClicked) :
    ListAdapter<WordDetail, SearchListAdapter.ViewHolder>(WordDetailDiff()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: ListItemSearchBinding =
            DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                R.layout.list_item_search,
                parent,
                false)
        return ViewHolder(binding.root, binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        getItem(position).let { _wordDetail ->
            holder.binding.viewModel = WordDetailViewModel()
            holder.binding.viewModel?.setResultItem(_wordDetail)
            holder.binding.root.setOnClickListener {
                onItemClicked.click(_wordDetail)
            }
            holder.binding.executePendingBindings()
        }
    }

    class ViewHolder(itemView: View, val binding: ListItemSearchBinding) : RecyclerView.ViewHolder(itemView)

    class WordDetailDiff : DiffUtil.ItemCallback<WordDetail>() {
        override fun areItemsTheSame(oldItem: WordDetail, newItem: WordDetail): Boolean {
            return oldItem.defId == newItem.defId
        }

        override fun areContentsTheSame(oldItem: WordDetail, newItem: WordDetail): Boolean {
            return oldItem == newItem
        }
    }
}
