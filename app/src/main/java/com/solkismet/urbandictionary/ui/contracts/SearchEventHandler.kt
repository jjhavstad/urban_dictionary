package com.solkismet.urbandictionary.ui.contracts

import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail

interface SearchEventHandler {
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

    interface SearchView {
        fun setSearchResult(data: SearchResult?)
        fun setThumbsUpSelected()
        fun setThumbsDownSelected()
        fun clearSort()
        fun showError()
        fun setIsRefreshing(refreshing: Boolean)
    }
}
