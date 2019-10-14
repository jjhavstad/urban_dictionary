package com.solkismet.urbandictionary.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.network.Service
import com.solkismet.urbandictionary.ui.contracts.SearchEventHandler
import io.reactivex.disposables.CompositeDisposable

class SearchViewModel(val searchView: SearchEventHandler.SearchView,
                      val service: Service) : ViewModel() {
    val searchResult = MutableLiveData<SearchResult>()
    private val disposables = CompositeDisposable()
    private var currentSearchTerm: String? = null

    class Factory(val searchView: SearchEventHandler.SearchView,
                  val service: Service) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            return SearchViewModel(searchView, service) as T
        }
    }

    fun processSearchQuery(query: String) {
        currentSearchTerm = query
        if (query.isEmpty()) {
            searchView.clearSort()
            searchView.setSearchResult(null)
        } else {
            searchTerm(query)
        }
    }

    fun searchTerm(term: String) {
        searchView.clearSort()
        searchView.setIsRefreshing(true)
        disposables.add(service.search(term)
            .subscribe({
            if (term == currentSearchTerm) {
                searchView.setSearchResult(it)
            }
            searchView.setIsRefreshing(false)
        }, {
            searchView.setIsRefreshing(false)
            searchView.showError()
        }))
    }

    fun clearDisposables() {
        disposables.clear()
    }

    fun refreshSearch() {
        currentSearchTerm?.let {
            searchTerm(it)
        }
    }
}
