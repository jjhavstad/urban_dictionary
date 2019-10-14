package com.solkismet.urbandictionary.data.models

import com.google.gson.annotations.SerializedName

data class SearchResult(@SerializedName("list") val list: MutableList<WordDetail>)
