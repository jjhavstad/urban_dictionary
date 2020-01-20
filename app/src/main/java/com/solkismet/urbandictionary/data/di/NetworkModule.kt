package com.solkismet.urbandictionary.data.di

import com.solkismet.urbandictionary.data.network.SearchService
import org.koin.dsl.module

val networkModule = module {
    single { SearchService() }
}
