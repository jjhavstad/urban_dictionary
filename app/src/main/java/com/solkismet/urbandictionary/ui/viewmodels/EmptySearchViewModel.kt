package com.solkismet.urbandictionary.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.R

class EmptySearchViewModel(context: Context) : ViewModel() {
    val emptyResultText =
        MutableLiveData<String>(context.getString(R.string.search_input_for_results))
}
