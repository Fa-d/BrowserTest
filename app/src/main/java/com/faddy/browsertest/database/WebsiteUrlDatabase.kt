package com.faddy.browsertest.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.faddy.browsertest.database.dao.URLDao
import com.faddy.browsertest.models.URLData


@Database(entities = [URLData::class], version = 1, exportSchema = false)
abstract class WebsiteUrlDatabase : RoomDatabase() {

    abstract fun getUrlDao(): URLDao

}