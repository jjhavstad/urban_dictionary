package com.solkismet.urbandictionary.ui.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.data.models.WordDetail

class WordDetailViewModel(
    val searchResultItem: MutableLiveData<WordDetail> = MutableLiveData()
) : ViewModel()
