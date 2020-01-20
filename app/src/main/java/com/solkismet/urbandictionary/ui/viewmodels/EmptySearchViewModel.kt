package com.solkismet.urbandictionary.ui.viewmodels

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.solkismet.urbandictionary.R

class EmptySearchViewModel(
    context: Context,
    val emptyResultText: MutableLiveData<String> = MutableLiveData(context.getString(R.string.search_input_for_results))
) : ViewModel()
