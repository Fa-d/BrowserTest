package com.faddy.browsertest.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}