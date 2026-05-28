package com.jim.emotionrecord.quest.di

import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepository
import com.jim.emotionrecord.quest.data.repository.EmotionRecordRepositoryImpl
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepository
import com.jim.emotionrecord.quest.data.repository.QuestUserMetaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class QuestRepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmotionRecordRepository(
        impl: EmotionRecordRepositoryImpl,
    ): EmotionRecordRepository

    @Binds
    @Singleton
    abstract fun bindQuestUserMetaRepository(
        impl: QuestUserMetaRepositoryImpl,
    ): QuestUserMetaRepository
}
