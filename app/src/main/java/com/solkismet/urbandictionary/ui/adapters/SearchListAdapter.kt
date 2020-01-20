package com.solkismet.urbandictionary.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.databinding.ListItemSearchBinding
import com.solkismet.urbandictionary.databinding.ListItemSearchEmptyBinding
import com.solkismet.urbandictionary.ui.viewmodels.EmptySearchViewModel
import com.solkismet.urbandictionary.ui.viewmodels.SearchViewModel
import com.solkismet.urbandictionary.ui.viewmodels.WordDetailViewModel

class SearchListAdapter(private val onItemClicked: SearchViewModel.OnItemClicked) :
    RecyclerView.Adapter<SearchListAdapter.BaseViewHolder>() {

    var data: List<WordDetail>? = null
        set (value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return when(viewType) {
            EMPTY_TYPE -> {
                val binding: ListItemSearchEmptyBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.list_item_search_empty,
                        parent,
                        false)
                EmptyViewHolder(binding.root, binding)
            }
            else -> {
                val binding: ListItemSearchBinding =
                    DataBindingUtil.inflate(LayoutInflater.from(parent.context),
                        R.layout.list_item_search,
                        parent,
                        false)
                ViewHolder(binding.root, binding)
            }
        }
    }

    override fun getItemCount(): Int {
        data?.let {
            if (it.isNotEmpty()) {
                return it.size
            }
        }
        return 1
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        if (holder is ViewHolder) {
            data?.let {
                val wordDetail = it.get(position)
                holder.binding.viewModel = WordDetailViewModel()
                holder.binding.viewModel?.searchResultItem?.value = wordDetail
                holder.binding.root.setOnClickListener {
                    onItemClicked.click(wordDetail)
                }
                holder.binding.executePendingBindings()
            }
        } else if (holder is EmptyViewHolder) {
            holder.binding.viewModel = EmptySearchViewModel(holder.binding.root.context)
            if (data == null) {
                holder.binding.viewModel?.emptyResultText?.value =
                    holder.binding.root.context.getString(R.string.search_input_for_results)
            } else {
                holder.binding.viewModel?.emptyResultText?.value =
                    holder.binding.root.context.getString(R.string.search_no_results)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (data == null || data?.size == 0) {
            return EMPTY_TYPE
        }
        return STANDARD_TYPE
    }

    open class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    class ViewHolder(itemView: View, val binding: ListItemSearchBinding) : BaseViewHolder(itemView)

    class EmptyViewHolder(itemView: View, val binding: ListItemSearchEmptyBinding) :
        BaseViewHolder(itemView)

    private companion object {
        const val EMPTY_TYPE = 0
        const val STANDARD_TYPE = 1
    }
}
