package com.solkismet.urbandictionary.data.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

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
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.createStringArrayList(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(definition)
        parcel.writeString(permaLink)
        parcel.writeInt(thumbsUp)
        parcel.writeStringList(soundUrls)
        parcel.writeString(author)
        parcel.writeString(word)
        parcel.writeLong(defId)
        parcel.writeString(currentVote)
        parcel.writeString(writtenOn)
        parcel.writeString(example)
        parcel.writeInt(thumbsDown)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<WordDetail> {
        override fun createFromParcel(parcel: Parcel): WordDetail {
            return WordDetail(parcel)
        }

        override fun newArray(size: Int): Array<WordDetail?> {
            return arrayOfNulls(size)
        }
    }

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
