package com.solkismet.urbandictionary.ui.activities

import android.content.Context
import android.net.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.solkismet.urbandictionary.R
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.databinding.ActivityMainBinding
import com.solkismet.urbandictionary.ui.adapters.SearchListAdapter
import com.solkismet.urbandictionary.ui.extensions.withColor
import com.solkismet.urbandictionary.ui.utils.NetworkListenerHelper
import com.solkismet.urbandictionary.viewmodels.SearchViewModel
import io.reactivex.Completable
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*

class SearchActivity : AppCompatActivity(),
    SearchViewModel.OnItemClicked,
    SearchViewModel.OnRefreshListener,
    SearchViewModel.OnSortByThumbsUpClicked,
    SearchViewModel.OnSortByThumbsDownClicked,
    SearchViewModel.OnSearchAction {

    private var binding: ActivityMainBinding? = null
    private var viewModel: SearchViewModel? = null
    private var networkCallback: ConnectivityManager.NetworkCallback? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViewModel()
        initDataBinding()
        initSearchBar()
        registerNetworkConnectivityListener()
    }

    override fun onStop() {
        super.onStop()
        viewModel?.clearDisposables()
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterNetworkConnectivityListener()
        viewModel?.onSearchAction = null
    }

    override fun click(wordDetail: WordDetail) {
        startActivity(WordDetailActivity.getIntent(this, wordDetail))
    }

    override fun sortByThumbsUp() {
        viewModel?.sortResultsByThumbsUp()?.let { _sortedSearchResult ->
            setSearchResult(_sortedSearchResult)
            setThumbsUpSelected()
        }
    }

    override fun sortByThumbsDown() {
        viewModel?.sortResultsByThumbsDown()?.let { _sortedSearchResult ->
            setSearchResult(_sortedSearchResult)
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

    override fun updateList(list: List<WordDetail>?) {
        (binding?.searchListView?.adapter as SearchListAdapter).apply {
            submitList(
                mutableListOf<WordDetail>().apply {
                    list?.forEach {
                        add(it)
                    }
                }
            )
        }
    }

    override fun saveList(list: List<WordDetail>?) {
        viewModel?.disposables?.add(
            Completable.fromRunnable {
                list?.forEach { _wordDetail ->
                    viewModel?.saveWord(_wordDetail)
                }
            }.subscribeOn(Schedulers.io()).subscribe()
        )
    }

    override fun showStartSearch() {
        empty_search_results.visibility = View.VISIBLE
        empty_search_results.text = getString(R.string.search_input_for_results)
    }

    override fun showEmptySearchResults() {
        empty_search_results.visibility = View.VISIBLE
        empty_search_results.text = getString(R.string.search_no_results)
    }

    override fun hideEmptySearchResults() {
        empty_search_results.visibility = View.GONE
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this).get(SearchViewModel::class.java)

        viewModel?.onSearchAction = this

        viewModel?.getSearchResult()?.observe(this, Observer { _data ->
            setSearchResult(_data)
        })
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding?.viewModel = viewModel
        binding?.onRefreshListener = this
        binding?.onSortByThumbsUpListener = this
        binding?.onSortByThumbsDownListener = this
        binding?.searchListView?.adapter = SearchListAdapter(this)
        (binding?.searchListView?.adapter as SearchListAdapter).registerAdapterDataObserver(object: RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                binding?.searchListView?.scrollToPosition(0)
            }

            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) {
                binding?.searchListView?.scrollToPosition(0)
            }

            override fun onItemRangeMoved(fromPosition: Int, toPosition: Int, itemCount: Int) {
                binding?.searchListView?.scrollToPosition(0)
            }

            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                binding?.searchListView?.scrollToPosition(0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int) {
                binding?.searchListView?.scrollToPosition(0)
            }

            override fun onItemRangeChanged(positionStart: Int, itemCount: Int, payload: Any?) {
                binding?.searchListView?.scrollToPosition(0)
            }
        })
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

    private fun registerNetworkConnectivityListener() {
        val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        networkCallback = NetworkListenerHelper.registerNetworkStateChangeListener(
            connectivityManager,
            { viewModel?.online = true },
            { viewModel?.online = false }
        )
    }

    private fun unregisterNetworkConnectivityListener() {
        networkCallback?.let { _networkCallback ->
            val connectivityManager: ConnectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            NetworkListenerHelper.unregisterNetworkStateChangeListener(connectivityManager, _networkCallback)
        }
    }

    private fun setSearchResult(data: SearchResult?) {
        viewModel?.handleSearchResults(data)
    }
}
