package com.solkismet.urbandictionary.data.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.solkismet.urbandictionary.data.models.SearchResult
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchService {
    private val searchApi: SearchApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://mashape-community-urban-dictionary.p.rapidapi.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        searchApi = retrofit.create(SearchApi::class.java)
    }

    fun search(term: String): Flowable<SearchResult> {
        return searchApi.search(API_HOST, API_KEY, term)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .toFlowable()
    }

    private companion object {
        private const val API_HOST = "mashape-community-urban-dictionary.p.rapidapi.com"
        private const val API_KEY = "7YsNrzorWbmshFb30Ui089ZRuZOBp1Fza0sjsnErip2tsiazxt"
    }
}
