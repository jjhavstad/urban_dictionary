package com.solkismet.urbandictionary.ui.extensions

import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.solkismet.urbandictionary.ui.contracts.SearchEventHandler

@BindingAdapter("setRefreshListener")
fun SwipeRefreshLayout.onRefresh(onRefreshListener: SearchEventHandler.OnRefreshListener) {
    setOnRefreshListener {
        onRefreshListener.refreshSearch()
    }
}
