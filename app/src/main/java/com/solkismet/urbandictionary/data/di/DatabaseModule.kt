package com.solkismet.urbandictionary.data.di

import androidx.room.Room
import com.solkismet.urbandictionary.data.db.WordDetailDatabase
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataBaseModule = module {
    single {
        Room.databaseBuilder(
            androidApplication(),
            WordDetailDatabase::class.java,
            "word-detail-db"
        ).build()
    }

    single {
        get<WordDetailDatabase>().wordDetailDao()
    }
}
