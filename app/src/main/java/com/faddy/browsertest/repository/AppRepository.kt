package com.faddy.browsertest.repository

import com.faddy.browsertest.database.dao.URLDao
import com.faddy.browsertest.models.URLData
import javax.inject.Inject

class AppRepository @Inject constructor(private val urlDao: URLDao) {

    suspend fun getAllScheduleApps(): List<URLData> = urlDao.getAllScheduleApps()
    suspend fun checkIfDataAlreadyExists(theUrl: String): Int =
        urlDao.checkIfDataAlreadyExists(theUrl)

    suspend fun getHitCountSingleSite(theUrl: String): Int = urlDao.getHitCountSingleSite(theUrl)
    suspend fun incrementHitCount(newCount: Int, theURL: String): Int =
        urlDao.incrementHitCount(newCount, theURL)

    suspend fun insertUrlIntoTable(model: URLData) = urlDao.insertUrlIntoTable(model)
    suspend fun isCurrentURLBookmarked(url: String) = urlDao.isCurrentURLBookmarked(url)
    suspend fun getTop9MostVisitedSites() = urlDao.getTop9MostVisitedSites()
    suspend fun setFavionToDB(image: ByteArray, theURL: String) =
        urlDao.setFavionToDB(image, theURL)
    suspend fun setTitleOfUrl(title: String, theURL: String) =
        urlDao.setTitleOfUrl(title, theURL)

    suspend fun getAllTitleOfDB() = urlDao.getAllTitleOfDB()

    suspend fun getTitleURLImageFromDB() = urlDao.getTitleURLImageFromDB()

}