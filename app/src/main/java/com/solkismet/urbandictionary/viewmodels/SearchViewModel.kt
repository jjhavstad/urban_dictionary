package com.solkismet.urbandictionary.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.data.db.WordDetailDao
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.data.network.SearchService
import io.reactivex.disposables.CompositeDisposable
import org.koin.core.KoinComponent
import org.koin.core.inject

class SearchViewModel: ViewModel(), KoinComponent {

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
        fun updateList(list: List<WordDetail>?)
        fun saveList(list: List<WordDetail>?)
        fun showStartSearch()
        fun showEmptySearchResults()
        fun hideEmptySearchResults()
    }

    private val searchResult = MutableLiveData<SearchResult>()
    var online: Boolean = true
    var onSearchAction: OnSearchAction? = null
    val disposables = CompositeDisposable()
    private val searchService: SearchService by inject()
    private val wordDetailDao: WordDetailDao by inject()
    private var currentSearchTerm: String? = null

    fun processSearchQuery(query: String) {
        currentSearchTerm = query
        if (query.isEmpty()) {
            onSearchAction?.clearSort()
            setSearchResult(null)
        } else {
            searchTerm(query)
        }
    }

    fun clearDisposables() {
        disposables.clear()
    }

    fun refreshSearch() {
        currentSearchTerm?.let {
            searchTerm(it)
        }
    }

    fun handleSearchResults(data: SearchResult?) {
        data?.let { _searchResult ->
            onSearchAction?.updateList(_searchResult.list)
            if (_searchResult.list.isEmpty()) {
                onSearchAction?.showEmptySearchResults()
            } else {
                onSearchAction?.hideEmptySearchResults()
            }
        } ?: run {
            onSearchAction?.updateList(null)
            onSearchAction?.showStartSearch()
        }
    }

    fun saveWord(wordDetail: WordDetail) {
        wordDetailDao.insert(wordDetail)
    }

    fun getSearchResult(): LiveData<SearchResult> {
        return searchResult
    }

    fun sortResultsByThumbsUp(): SearchResult? {
        return searchResult.value?.apply {
            list.sortByDescending { _wordDetail ->
                _wordDetail.thumbsUp
            }
        }
    }

    fun sortResultsByThumbsDown(): SearchResult? {
        return searchResult.value?.apply {
            list.sortByDescending { _wordDetail ->
                _wordDetail.thumbsDown
            }
        }
    }

    private fun searchTerm(term: String) {
        onSearchAction?.clearSort()
        onSearchAction?.setIsRefreshing(true)
        disposables.add(
            when (online) {
                true -> searchService.search(term).subscribe(
                    {
                        if (term == currentSearchTerm) {
                            onSearchAction?.saveList(it.list)
                            setSearchResult(it)
                        }
                        onSearchAction?.setIsRefreshing(false)
                    }, {
                        onSearchAction?.setIsRefreshing(false)
                        onSearchAction?.showError()
                    }
                )
                false -> wordDetailDao.searchForWord(term).subscribe(
                    {
                        if (term == currentSearchTerm) {
                            setSearchResult(SearchResult(it))
                        }
                        onSearchAction?.setIsRefreshing(false)
                    },
                    {
                        onSearchAction?.setIsRefreshing(false)
                        onSearchAction?.showError()
                    }
                )
            }
        )
    }

    private fun setSearchResult(data: SearchResult?) {
        searchResult.postValue(data)
    }
}
