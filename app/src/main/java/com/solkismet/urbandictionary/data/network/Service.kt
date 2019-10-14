package com.solkismet.urbandictionary.data.network

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import com.solkismet.urbandictionary.data.models.SearchResult
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class Service private constructor() {
    companion object {
        private const val API_HOST = "mashape-community-urban-dictionary.p.rapidapi.com"
        private const val API_KEY = "7YsNrzorWbmshFb30Ui089ZRuZOBp1Fza0sjsnErip2tsiazxt"

        private val instance = Service()

        fun getInstance(): Service {
            return instance
        }
    }

    private val api: Api
    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://mashape-community-urban-dictionary.p.rapidapi.com")
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(Api::class.java)
    }

    fun search(term: String): Single<SearchResult> {
        return api.search(API_HOST, API_KEY, term)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
    }
}
