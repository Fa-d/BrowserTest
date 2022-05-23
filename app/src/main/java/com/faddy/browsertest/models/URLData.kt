package com.faddy.browsertest.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "url_table")
@Parcelize
data class URLData(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "uid")
    var uid: Int = 0,
    @ColumnInfo(name = "packageName")
    var packageName: String? = ""
): Parcelable
