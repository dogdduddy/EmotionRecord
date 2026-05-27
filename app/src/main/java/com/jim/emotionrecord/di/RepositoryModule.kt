package com.jim.emotionrecord.di

import com.jim.emotionrecord.data.repository.EmotionRepository
import com.jim.emotionrecord.data.repository.EmotionRepositoryImpl
import com.jim.emotionrecord.data.repository.UserMetaRepository
import com.jim.emotionrecord.data.repository.UserMetaRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindEmotionRepository(impl: EmotionRepositoryImpl): EmotionRepository

    @Binds
    @Singleton
    abstract fun bindUserMetaRepository(impl: UserMetaRepositoryImpl): UserMetaRepository
}
