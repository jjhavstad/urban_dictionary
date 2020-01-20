package com.solkismet.urbandictionary.data.models

import android.os.Parcel
import android.os.Parcelable
import com.google.gson.annotations.SerializedName

data class WordDetail(
    @SerializedName("definition") val definition: String?,
    @SerializedName("permalink") val permaLink: String?,
    @SerializedName("thumbs_up") val thumbsUp: Int,
    @SerializedName("sound_urls") val soundUrls: List<String>?,
    @SerializedName("author") val author: String?,
    @SerializedName("word") val word: String?,
    @SerializedName("defid") val defId: Long,
    @SerializedName("current_vote") val currentVote: String?,
    @SerializedName("written_on") val writtenOn: String?,
    @SerializedName("example") val example: String?,
    @SerializedName("thumbs_down") val thumbsDown: Int
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
}
