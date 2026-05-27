package com.jim.emotionrecord.di

import android.content.Context
import androidx.room.Room
import com.jim.emotionrecord.data.local.AppDatabase
import com.jim.emotionrecord.data.local.dao.EmotionCheckDao
import com.jim.emotionrecord.data.local.dao.UserMetaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "emotion_record.db").build()

    @Provides
    fun provideEmotionCheckDao(db: AppDatabase): EmotionCheckDao = db.emotionCheckDao()

    @Provides
    fun provideUserMetaDao(db: AppDatabase): UserMetaDao = db.userMetaDao()
}
