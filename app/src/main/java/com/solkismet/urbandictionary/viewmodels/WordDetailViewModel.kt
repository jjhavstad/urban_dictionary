package com.solkismet.urbandictionary.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.data.models.WordDetail
import com.solkismet.urbandictionary.data.repo.WordDetailRepository
import io.reactivex.Flowable
import org.koin.core.KoinComponent
import org.koin.core.inject

class WordDetailViewModel : ViewModel(), KoinComponent {
    private val searchResultItem: MutableLiveData<WordDetail> = MutableLiveData()

    var online: Boolean = true

    private val wordDetailRepository: WordDetailRepository by inject()

    interface OnSoundAction {
        fun playSound(soundUrl: String, position: Int)
        fun stopAll()
    }

    var onSoundAction: OnSoundAction? = null

    fun setResultItem(wordDetail: WordDetail?) {
        wordDetail?.let {
            searchResultItem.value = it
        }
    }

    fun getWordDetailById(defId: Long): Flowable<WordDetail> {
        return wordDetailRepository.getWord(defId)
    }

    fun getResultItem(): LiveData<WordDetail> {
        return searchResultItem
    }

    fun playSound(soundUrl: String, position: Int) {
        if (online) {
            onSoundAction?.let { _onSoundAction ->
                _onSoundAction.stopAll()
                _onSoundAction.playSound(soundUrl, position)
            }
        }
    }
}
