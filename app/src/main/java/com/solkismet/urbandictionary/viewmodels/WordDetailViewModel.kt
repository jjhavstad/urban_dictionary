package com.solkismet.urbandictionary.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.data.models.WordDetail

class WordDetailViewModel(
    val searchResultItem: MutableLiveData<WordDetail> = MutableLiveData()
) : ViewModel() {

    interface OnSoundAction {
        fun playSound(soundUrl: String, position: Int)
        fun stopAll()
    }

    var onSoundAction: OnSoundAction? = null

    fun playSound(soundUrl: String, position: Int) {
        onSoundAction?.let { _onSoundAction ->
            _onSoundAction.stopAll()
            _onSoundAction.playSound(soundUrl, position)
        }
    }
}
