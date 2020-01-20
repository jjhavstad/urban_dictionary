package com.solkismet.urbandictionary

import android.app.Application
import com.solkismet.urbandictionary.data.di.networkModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class UrbanDictionaryApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@UrbanDictionaryApplication)
            modules(networkModule)
        }
    }
}
