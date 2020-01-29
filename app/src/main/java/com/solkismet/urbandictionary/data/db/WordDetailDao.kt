package com.solkismet.urbandictionary.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.solkismet.urbandictionary.data.models.WordDetail
import io.reactivex.Flowable

@Dao
interface WordDetailDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(wordDetail: WordDetail)

    @Query("SELECT * FROM word_details WHERE defid = :defId")
    fun getWordDetailById(defId: Long): Flowable<WordDetail>

    @Query("SELECT * FROM word_details WHERE word = :word COLLATE NOCASE")
    fun searchForWord(word: String): Flowable<MutableList<WordDetail>>
}
