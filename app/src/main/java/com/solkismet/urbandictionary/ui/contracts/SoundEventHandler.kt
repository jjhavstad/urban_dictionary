package com.solkismet.urbandictionary.ui.contracts

interface SoundEventHandler {
    interface OnPlaySound {
        fun playSound(url: String,
                      onPlaySoundStarted: () -> Unit,
                      onPlaySoundStopped: () -> Unit
        )
    }

    interface OnUpdateSoundListItem {
        fun selectSoundListItem()
        fun unselectSoundListItem()
    }
}
