package com.solkismet.urbandictionary.ui.extensions

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.solkismet.urbandictionary.ui.viewmodels.SearchViewModel

@BindingAdapter("setRefreshListener")
fun SwipeRefreshLayout.onRefresh(onRefreshListener: SearchViewModel.OnRefreshListener) {
    setOnRefreshListener {
        onRefreshListener.refreshSearch()
    }
}
