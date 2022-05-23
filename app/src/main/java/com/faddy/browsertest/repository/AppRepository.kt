package com.faddy.browsertest.repository

import com.faddy.browsertest.database.dao.URLDao
import com.faddy.browsertest.models.URLData
import javax.inject.Inject

class AppRepository @Inject constructor(private val urlDao: URLDao) {

    suspend fun getAllScheduleApps(): List<URLData> = urlDao.getAllScheduleApps()

}