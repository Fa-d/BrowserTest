package com.faddy.browsertest.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


@Parcelize
data class MostVisitedSitesModel(
    var title: String = "",
    var generatedURL :String = "",
    var favIconBlob: ByteArray = ByteArray(0)

): Parcelable {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MostVisitedSitesModel

        if (title != other.title) return false
        if (!favIconBlob.contentEquals(other.favIconBlob)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = title.hashCode()
        result = 31 * result + favIconBlob.contentHashCode()
        return result
    }
}