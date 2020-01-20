package com.solkismet.urbandictionary.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.data.network.SearchService
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchViewModel(private val onSearchAction: OnSearchAction) : ViewModel(), KoinComponent {

    interface OnItemClicked {
        fun click(wordDetail: WordDetail)
    }

    interface OnRefreshListener {
        fun refreshSearch()
    }

    interface OnSortByThumbsUpClicked {
        fun sortByThumbsUp()
    }

    interface OnSortByThumbsDownClicked {
        fun sortByThumbsDown()
    }

    interface OnSearchAction {
        fun setThumbsUpSelected()
        fun setThumbsDownSelected()
        fun clearSort()
        fun showError()
        fun setIsRefreshing(refreshing: Boolean)
    }

    val searchResult = MutableLiveData<SearchResult>()
    private val disposables = CompositeDisposable()
    private val searchService: SearchService by inject()
    private var currentSearchTerm: String? = null

    class Factory(private val onSearchAction: OnSearchAction) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(onSearchAction) as T
        }
    }

    fun processSearchQuery(query: String) {
        currentSearchTerm = query
        if (query.isEmpty()) {
            onSearchAction.clearSort()
            setSearchResult(null)
        } else {
            searchTerm(query)
        }
    }

    private fun searchTerm(term: String) {
        onSearchAction.clearSort()
        onSearchAction.setIsRefreshing(true)
        disposables.add(
            searchService.search(term).subscribe(
                {
                    if (term == currentSearchTerm) {
                        setSearchResult(it)
                    }
                    onSearchAction.setIsRefreshing(false)
                }, {
                    onSearchAction.setIsRefreshing(false)
                    onSearchAction.showError()
                }
            )
        )
    }

    fun clearDisposables() {
        disposables.clear()
    }

    fun refreshSearch() {
        currentSearchTerm?.let {
            searchTerm(it)
        }
    }

    fun setSearchResult(data: SearchResult?) {
        searchResult.value = data
    }
}