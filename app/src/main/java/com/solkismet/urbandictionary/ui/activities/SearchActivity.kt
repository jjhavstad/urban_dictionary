package com.solkismet.urbandictionary.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.data.network.Service
import com.solkismet.urbandictionary.databinding.ActivityMainBinding
import com.solkismet.urbandictionary.ui.adapters.SearchListAdapter
import com.solkismet.urbandictionary.ui.contracts.SearchEventHandler
import com.solkismet.urbandictionary.ui.extensions.withColor
import com.solkismet.urbandictionary.ui.viewmodels.SearchViewModel

class SearchActivity : AppCompatActivity(),
    SearchEventHandler.OnItemClicked,
    SearchEventHandler.OnRefreshListener,
    SearchEventHandler.OnSortByThumbsUpClicked,
    SearchEventHandler.OnSortByThumbsDownClicked,
    SearchEventHandler.SearchView {
    override fun setThumbsUpSelected() {
        binding.sortSearchThumbsDownImageview.setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent))
        binding.sortSearchThumbsUpImageview.setColorFilter(ContextCompat.getColor(this,
            R.color.colorPrimary))    }

    override fun setThumbsDownSelected() {
        binding.sortSearchThumbsDownImageview.setColorFilter(ContextCompat.getColor(this,
            R.color.colorAccent))
        binding.sortSearchThumbsUpImageview.setColorFilter(ContextCompat.getColor(this.applicationContext,
            R.color.colorPrimary))    }

    override fun clearSort() {
        binding.sortSearchThumbsUpImageview.setColorFilter(ContextCompat.getColor(this.applicationContext,
            R.color.colorPrimary))
        binding.sortSearchThumbsDownImageview.setColorFilter(ContextCompat.getColor(this.applicationContext,
            R.color.colorPrimary))
    }

    override fun showError() {
        Snackbar.make(binding.root,
            R.string.search_api_error,
            Snackbar.LENGTH_SHORT).apply {
            setAction(R.string.search_api_close_error, null)
            withColor(getResources().getColor(android.R.color.holo_red_dark))
            show()
        }
    }

    override fun setIsRefreshing(refreshing: Boolean) {
        binding.refreshSearchListView.isRefreshing = refreshing
    }

    override fun setSearchResult(data: SearchResult?) {
        viewModel.searchResult.value = data
        val adapter = SearchListAdapter(this)
        binding.searchListView.adapter = adapter
        adapter.data = data?.list
    }

    private lateinit var binding: ActivityMainBinding

    private lateinit var viewModel: SearchViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProviders.of(this,
            SearchViewModel.Factory(this, Service.getInstance()))
            .get(SearchViewModel::class.java)
        binding.viewModel = viewModel
        binding.onRefreshListener = this
        binding.onSortByThumbsUpListener = this
        binding.onSortByThumbsDownListener = this
        setSearchResult(null)
        binding.searchView.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel.processSearchQuery(it)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel.processSearchQuery(it)
                    return true
                }
                return false
            }
        })
    }

    override fun onStop() {
        super.onStop()
        viewModel.clearDisposables()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArray(SEARCH_RESULT_KEY,
            viewModel.searchResult.value?.list?.toTypedArray())
        super.onSaveInstanceState(outState)
    }

    override fun click(wordDetail: WordDetail) {
        startActivity(WordDetailActivity.getIntent(this, wordDetail))
    }

    override fun sortByThumbsUp() {
        viewModel.searchResult.value?.let {
            it.list.sortByDescending { it.thumbsUp }
            setSearchResult(it)
            setThumbsUpSelected()
        }
    }

    override fun sortByThumbsDown() {
        viewModel.searchResult.value?.let {
            it.list.sortByDescending { it.thumbsDown }
            setSearchResult(it)
            setThumbsDownSelected()
        }
    }

    override fun refreshSearch() {
        viewModel.refreshSearch()
    }

    private companion object {
        const val SEARCH_RESULT_KEY = "search_result_key"
    }
}
