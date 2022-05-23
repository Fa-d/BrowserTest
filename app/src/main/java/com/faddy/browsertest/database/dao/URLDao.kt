package com.faddy.browsertest.database.dao

import androidx.room.Dao
import androidx.room.Query
import com.faddy.browsertest.models.URLData

@Dao
interface URLDao {
    @Query("SELECT * FROM url_table")
    suspend fun getAllScheduleApps(): List<URLData>
}