package com.solkismet.urbandictionary.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.databinding.ActivityMainBinding
import com.solkismet.urbandictionary.ui.adapters.SearchListAdapter
import com.solkismet.urbandictionary.ui.extensions.withColor
import com.solkismet.urbandictionary.ui.viewmodels.SearchViewModel

class SearchActivity : AppCompatActivity(),
    SearchViewModel.OnItemClicked,
    SearchViewModel.OnRefreshListener,
    SearchViewModel.OnSortByThumbsUpClicked,
    SearchViewModel.OnSortByThumbsDownClicked,
    SearchViewModel.OnSearchAction {

    private var binding: ActivityMainBinding? = null
    private var viewModel: SearchViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initDataBinding()
        initSearchBar()
    }

    override fun onStop() {
        super.onStop()
        viewModel?.clearDisposables()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelableArray(
            SEARCH_RESULT_KEY,
            viewModel?.searchResult?.value?.list?.toTypedArray()
        )
        super.onSaveInstanceState(outState)
    }

    override fun click(wordDetail: WordDetail) {
        startActivity(WordDetailActivity.getIntent(this, wordDetail))
    }

    override fun sortByThumbsUp() {
        viewModel?.searchResult?.value?.let { _searchResult ->
            _searchResult.list.sortByDescending { _wordDetail ->
                _wordDetail.thumbsUp
            }
            setSearchResult(_searchResult)
            setThumbsUpSelected()
        }
    }

    override fun sortByThumbsDown() {
        viewModel?.searchResult?.value?.let { _searchResult ->
            _searchResult.list.sortByDescending { _wordDetail ->
                _wordDetail.thumbsDown
            }
            setSearchResult(_searchResult)
            setThumbsDownSelected()
        }
    }

    override fun refreshSearch() {
        viewModel?.refreshSearch()
    }


    override fun setThumbsUpSelected() {
        binding?.sortSearchThumbsDownImageview?.setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimary)
        )

        binding?.sortSearchThumbsUpImageview?.setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent)
        )
    }

    override fun setThumbsDownSelected() {
        binding?.sortSearchThumbsDownImageview?.setColorFilter(
            ContextCompat.getColor(this, R.color.colorAccent)
        )

        binding?.sortSearchThumbsUpImageview?.setColorFilter(
            ContextCompat.getColor(this, R.color.colorPrimary)
        )
    }

    override fun clearSort() {
        binding?.sortSearchThumbsUpImageview?.setColorFilter(
            ContextCompat.getColor(this.applicationContext, R.color.colorPrimary)
        )

        binding?.sortSearchThumbsDownImageview?.setColorFilter(
            ContextCompat.getColor(this.applicationContext, R.color.colorPrimary)
        )
    }

    override fun showError() {
        binding?.root?.let { _rootView ->
            Snackbar.make(
                _rootView,
                R.string.search_api_error,
                Snackbar.LENGTH_SHORT
            ).apply {
                setAction(R.string.search_api_close_error, null)
                withColor(ContextCompat.getColor(this@SearchActivity, android.R.color.holo_red_dark))
                show()
            }
        }
    }

    override fun setIsRefreshing(refreshing: Boolean) {
        binding?.refreshSearchListView?.isRefreshing = refreshing
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(
            this,
            SearchViewModel.Factory(this)
        ).get(SearchViewModel::class.java)

        viewModel?.searchResult?.observe(this, Observer { _data ->
            setSearchResult(_data)
        })
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.viewModel = viewModel
        binding?.onRefreshListener = this
        binding?.onSortByThumbsUpListener = this
        binding?.onSortByThumbsDownListener = this
    }

    private fun initSearchBar() {
        setSearchResult(null)

        binding?.searchView?.setOnQueryTextListener(object: SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    viewModel?.processSearchQuery(it)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    viewModel?.processSearchQuery(it)
                    return true
                }
                return false
            }
        })

        binding?.searchView?.setOnClickListener {
            (it as SearchView).isIconified = false
        }
    }

    private fun setSearchResult(data: SearchResult?) {
        val adapter = SearchListAdapter(this)
        binding?.searchListView?.adapter = adapter
        adapter.data = data?.list
    }

    private companion object {
        const val SEARCH_RESULT_KEY = "search_result_key"
    }
}
