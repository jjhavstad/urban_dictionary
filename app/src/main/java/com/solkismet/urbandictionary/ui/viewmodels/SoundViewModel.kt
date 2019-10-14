package com.solkismet.urbandictionary.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.ui.contracts.SoundEventHandler

class SoundViewModel(val onPlaySound: SoundEventHandler.OnPlaySound,
    val onUpdateSoundListItem: SoundEventHandler.OnUpdateSoundListItem) : ViewModel() {
    val soundFileText =
        MutableLiveData<String>("")
    var soundUrl: String? = null

    fun setSoundFileText(text: String) {
        soundFileText.value = text
    }

    fun playSound() {
        soundUrl?.let {
            onPlaySound.playSound(it, {
                onUpdateSoundListItem.selectSoundListItem()
            }, {
                onUpdateSoundListItem.unselectSoundListItem()
            })
        }
    }
}
