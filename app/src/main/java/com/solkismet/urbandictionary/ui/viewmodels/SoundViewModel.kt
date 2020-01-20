package com.solkismet.urbandictionary.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SoundViewModel(
    private val onPlaySound: OnPlaySound,
    private val onUpdateSoundListItem: OnUpdateSoundListItem
) : ViewModel() {

    interface OnPlaySound {
        fun playSound(
            url: String,
            onPlaySoundStarted: () -> Unit,
            onPlaySoundStopped: () -> Unit
        )
    }

    interface OnUpdateSoundListItem {
        fun selectSoundListItem()
        fun unSelectSoundListItem()
    }

    val soundFileText = MutableLiveData<String>("")
    var soundUrl: String? = null

    fun setSoundFileText(text: String) {
        soundFileText.value = text
    }

    fun playSound() {
        soundUrl?.let { _soundUrl ->
            onPlaySound.playSound(
                _soundUrl,
                {
                    onUpdateSoundListItem.selectSoundListItem()
                },
                {
                    onUpdateSoundListItem.unSelectSoundListItem()
                }
            )
        }
    }
}
