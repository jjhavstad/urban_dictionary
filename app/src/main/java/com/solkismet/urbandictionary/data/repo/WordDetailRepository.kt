package com.solkismet.urbandictionary.data.repo

import com.solkismet.urbandictionary.data.db.WordDetailDao
import com.solkismet.urbandictionary.data.models.SearchResult
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.data.network.SearchService
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.core.KoinComponent
import org.koin.core.inject

class WordDetailRepository : KoinComponent {
    private val searchService: SearchService by inject()
    private val wordDetailDao: WordDetailDao by inject()

    fun searchForWord(word: String, remote: Boolean): Flowable<SearchResult> {
        return when (remote) {
            true -> searchService.search(word)
            false -> wordDetailDao.searchForWord(word).map {
                SearchResult(it)
            }
        }
    }

    fun saveWord(wordDetail: WordDetail) {
        wordDetailDao.insert(wordDetail)
    }

    fun getWord(defId: Long): Flowable<WordDetail> {
        return wordDetailDao.getWordDetailById(defId)
    }
}
