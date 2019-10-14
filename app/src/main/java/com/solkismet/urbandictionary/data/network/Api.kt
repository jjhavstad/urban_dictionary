package com.solkismet.urbandictionary.data.network

import com.solkismet.urbandictionary.data.models.SearchResult
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface Api {
    @GET("/define")
    fun search(@Header("x-rapidapi-host") apiHost: String,
               @Header("x-rapidapi-key") apiKey: String,
               @Query("term") term: String): Single<SearchResult>
}
