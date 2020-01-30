package com.solkismet.urbandictionary.data.di

import com.solkismet.urbandictionary.data.repo.WordDetailRepository
import org.koin.dsl.module

val repositoryModule = module {
    single { WordDetailRepository() }
}
