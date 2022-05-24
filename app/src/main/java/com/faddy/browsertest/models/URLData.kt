package com.faddy.browsertest.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "url_table")
@Parcelize
data class URLData(
/*    @ColumnInfo(name = "uid")
    var uid: Int = 0,   */
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "generatedURL")
    var generatedURL: String = "",

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "hitTimeStamp")
    var hitTimeStamp: Long = 0,

    @ColumnInfo(name = "hitCount")
    var hitCount: Int = 0,
) : Parcelable
