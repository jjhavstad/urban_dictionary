package com.solkismet.urbandictionary.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.data.repo.WordDetailRepository
import com.solkismet.urbandictionary.ui.extensions.postToQueue
import io.reactivex.Completable
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

    enum class OnSearchAction {
        CLEAR_SORT,
        SHOW_ERROR,
        SET_IS_REFRESHING,
        SET_IS_NOT_REFRESHING,
        SAVE_LIST,
        SHOW_START_SEARCH,
        SHOW_EMPTY_SEARCH_RESULTS,
        HIDE_EMPTY_SEARCH_RESULTS
    }

    private val searchResult = MutableLiveData<SearchResult>()
    private val searchActionEvent = MutableLiveData<OnSearchAction>()
    var online: Boolean = true
    val disposables = CompositeDisposable()
    private val wordDetailRepository: WordDetailRepository by inject()
    private var currentSearchTerm: String? = null

    fun processSearchQuery(query: String) {
        currentSearchTerm = query
        if (query.isEmpty()) {
            searchActionEvent.value = OnSearchAction.CLEAR_SORT
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

    fun handleSearchResults() {
        searchResult.value?.let { _searchResult ->
            if (_searchResult.list.isEmpty()) {
                searchActionEvent.value = OnSearchAction.SHOW_EMPTY_SEARCH_RESULTS
            } else {
                searchActionEvent.value = OnSearchAction.HIDE_EMPTY_SEARCH_RESULTS
            }
        } ?: run {
            searchActionEvent.value = OnSearchAction.SHOW_START_SEARCH
        }
    }

    fun getSearchResult(): LiveData<SearchResult> {
        return searchResult
    }

    fun getSearchActionEvent(): LiveData<OnSearchAction> {
        return searchActionEvent
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

    fun saveList(): Completable {
        return Completable.fromRunnable {
            searchResult.value?.list?.forEach { _wordDetail ->
                saveWord(_wordDetail)
            }
        }
    }

    private fun searchTerm(term: String) {
        searchActionEvent.value = OnSearchAction.CLEAR_SORT
        searchActionEvent.value = OnSearchAction.SET_IS_REFRESHING
        disposables.add(
            wordDetailRepository.searchForWord(term, online).subscribe(
                {
                    if (term == currentSearchTerm) {
                        setSearchResult(it)
                        if (online) {
                            searchActionEvent.postToQueue(OnSearchAction.SAVE_LIST)
                        }
                    }
                    searchActionEvent.postToQueue(OnSearchAction.SET_IS_NOT_REFRESHING)
                    searchActionEvent.postToQueue(OnSearchAction.SHOW_ERROR)
                }, {
                    searchActionEvent.postToQueue(OnSearchAction.SET_IS_NOT_REFRESHING)
                    searchActionEvent.postToQueue(OnSearchAction.SHOW_ERROR)
                }
            )
        )
    }

    private fun setSearchResult(data: SearchResult?) {
        searchResult.postValue(data)
    }

    private fun saveWord(wordDetail: WordDetail) {
        wordDetailRepository.saveWord(wordDetail)
    }
}
