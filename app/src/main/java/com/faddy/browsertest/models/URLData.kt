package com.faddy.browsertest.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "url_table")
@Parcelize
data class URLData(

    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "generatedURL")
    var generatedURL: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "hitTimeStamp")
    var hitTimeStamp: Long = 0,

    @ColumnInfo(name = "hitCount")
    var hitCount: Int = 0,

    @ColumnInfo(name = "isBookmarked")
    var isBookmarked: Boolean = false,

    @ColumnInfo(name = "favIconBlob")
    var favIconBlob: ByteArray = ByteArray(0)

) : Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as URLData

        if (generatedURL != other.generatedURL) return false
        if (title != other.title) return false
        if (hitTimeStamp != other.hitTimeStamp) return false
        if (hitCount != other.hitCount) return false
        if (isBookmarked != other.isBookmarked) return false
        if (!favIconBlob.contentEquals(other.favIconBlob)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = generatedURL.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + hitTimeStamp.hashCode()
        result = 31 * result + hitCount
        result = 31 * result + isBookmarked.hashCode()
        result = 31 * result + favIconBlob.contentHashCode()
        return result
    }
}

