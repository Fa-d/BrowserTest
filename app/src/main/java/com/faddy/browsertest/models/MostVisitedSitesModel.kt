package com.faddy.browsertest.models

import android.os.Parcelable
import androidx.room.ColumnInfo
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MostVisitedSitesModel(
    var siteName: String = "",
    @ColumnInfo(name = "favIconBlob")
    var favIconBlob: ByteArray = ByteArray(0)

): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MostVisitedSitesModel

        if (siteName != other.siteName) return false
        if (!favIconBlob.contentEquals(other.favIconBlob)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = siteName.hashCode()
        result = 31 * result + favIconBlob.contentHashCode()
        return result
    }
}