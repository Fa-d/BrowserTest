package com.faddy.browsertest.di

import android.content.Context
import androidx.room.Room
import com.faddy.browsertest.database.WebsiteUrlDatabase
import com.faddy.browsertest.database.dao.URLDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {

    @Provides
    @Singleton
    fun buildDatabase(@ApplicationContext context: Context) : WebsiteUrlDatabase {
        return Room.databaseBuilder(context, WebsiteUrlDatabase::class.java, "website_url_db").build()
    }

    @Provides
    @Singleton
    fun getUrlDao(appDatabase: WebsiteUrlDatabase): URLDao {
        return appDatabase.getUrlDao()
    }

}