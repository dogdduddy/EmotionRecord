package com.jim.emotionrecord.quest.di

import android.content.Context
import androidx.room.Room
import com.jim.emotionrecord.quest.data.local.QuestDatabase
import com.jim.emotionrecord.quest.data.local.dao.EmotionRecordDao
import com.jim.emotionrecord.quest.data.local.dao.QuestUserMetaDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QuestDatabaseModule {

    @Provides
    @Singleton
    fun provideQuestDatabase(@ApplicationContext context: Context): QuestDatabase =
        Room.databaseBuilder(context, QuestDatabase::class.java, "quest.db").build()

    @Provides
    fun provideEmotionRecordDao(db: QuestDatabase): EmotionRecordDao = db.emotionRecordDao()

    @Provides
    fun provideQuestUserMetaDao(db: QuestDatabase): QuestUserMetaDao = db.questUserMetaDao()
}
