package com.eyepax_news_app.di

import android.content.Context
import com.eyepax_news_app.data.local.SharedPrefsManager
import com.eyepax_news_app.db.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideNewsAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase(appContext)
    }

    @Provides
    @Singleton
    fun provideSharedPrefs(@ApplicationContext appContext: Context): SharedPrefsManager {
        return SharedPrefsManager(appContext)
    }
}