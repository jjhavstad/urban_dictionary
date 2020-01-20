package com.solkismet.urbandictionary.viewmodels

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.data.models.WordDetail

class WordDetailViewModel(
    val searchResultItem: MutableLiveData<WordDetail> = MutableLiveData()
) : ViewModel()
