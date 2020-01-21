package com.solkismet.urbandictionary.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.solkismet.urbandictionary.data.models.WordDetail

@Database(entities = [WordDetail::class], version = 1)
abstract class WordDetailDatabase : RoomDatabase() {
    abstract fun wordDetailDao(): WordDetailDao
}
