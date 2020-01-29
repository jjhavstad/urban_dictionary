package com.solkismet.urbandictionary.data.models

import androidx.room.*
import com.google.gson.annotations.SerializedName

@Entity(tableName = "word_details")
@TypeConverters(WordDetail.ListToStringConverter::class)
data class WordDetail(
    @ColumnInfo(name = "definition")
    @SerializedName("definition")
    val definition: String?,
    @ColumnInfo(name = "permalink")
    @SerializedName("permalink")
    val permaLink: String?,
    @ColumnInfo(name = "thumbs_up")
    @SerializedName("thumbs_up")
    val thumbsUp: Int,
    @ColumnInfo(name = "sound_urls")
    @SerializedName("sound_urls")
    val soundUrls: List<String>?,
    @ColumnInfo(name = "author")
    @SerializedName("author")
    val author: String?,
    @ColumnInfo(name = "word")
    @SerializedName("word")
    val word: String?,
    @PrimaryKey
    @ColumnInfo(name = "defid")
    @SerializedName("defid")
    val defId: Long,
    @ColumnInfo(name = "current_vote")
    @SerializedName("current_vote")
    val currentVote: String?,
    @ColumnInfo(name = "written_on")
    @SerializedName("written_on")
    val writtenOn: String?,
    @ColumnInfo(name = "example")
    @SerializedName("example")
    val example: String?,
    @ColumnInfo(name = "thumbs_down")
    @SerializedName("thumbs_down")
    val thumbsDown: Int
) {
    class ListToStringConverter {
        private val delimiter = ","

        @TypeConverter
        fun listToString(list: List<String>?): String? {
            return list?.joinToString(delimiter)
        }

        @TypeConverter
        fun stringToList(value: String?): List<String>? {
            return value?.split(delimiter)
        }
    }
}
