package com.faddy.browsertest.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.faddy.browsertest.models.MostVisitedSitesModel
import com.faddy.browsertest.models.URLData

@Dao
interface URLDao {
    @Query("SELECT * FROM url_table")
    suspend fun getAllScheduleApps(): List<URLData>

    @Query("SELECT COUNT(*) FROM url_table WHERE generatedURL = :theURL")
    suspend fun checkIfDataAlreadyExists(theURL: String): Int

    @Query("SELECT hitCount FROM url_table WHERE generatedURL = :theURL")
    suspend fun getHitCountSingleSite(theURL: String): Int

    @Query("UPDATE url_table SET hitCount =:newCount WHERE generatedURL = :theURL")
    suspend fun incrementHitCount(newCount: Int, theURL: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrlIntoTable(model: URLData)

    @Query("SELECT COUNT(*) FROM url_table WHERE generatedURL = :url AND isBookmarked == 1")
    suspend fun isCurrentURLBookmarked(url: String): Int

    @Query("UPDATE url_table SET isBookmarked = 1 WHERE generatedURL = :url")
    suspend fun setCurrnetUrlBookmarkerd(url: String): Int

    @Query("SELECT * FROM url_table ORDER BY hitCount DESC LIMIT 10")
    suspend fun getTop9MostVisitedSites(): List<URLData>

    @Query("UPDATE url_table SET favIconBlob =:image WHERE generatedURL = :theURL")
    suspend fun setFavionToDB(image: ByteArray, theURL: String): Int

    @Query("UPDATE url_table SET title =:title WHERE generatedURL = :theURL")
    suspend fun setTitleOfUrl(title: String, theURL: String): Int

    @Query("SELECT title from url_table LIMIT 100")
    suspend fun getAllTitleOfDB(): List<String>

    @Query("SELECT title,generatedURL, favIconBlob from url_table LIMIT 100")
    suspend fun getTitleURLImageFromDB(): List<MostVisitedSitesModel>

}