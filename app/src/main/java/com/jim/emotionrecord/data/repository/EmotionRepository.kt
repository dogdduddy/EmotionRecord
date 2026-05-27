package com.jim.emotionrecord.data.repository

import com.jim.emotionrecord.data.local.entity.EmotionCheckEntity
import kotlinx.coroutines.flow.Flow

interface EmotionRepository {
    fun observeAll(): Flow<List<EmotionCheckEntity>>
    suspend fun getAll(): List<EmotionCheckEntity>
    suspend fun insert(entity: EmotionCheckEntity): Long
    suspend fun insertAll(entities: List<EmotionCheckEntity>)
    suspend fun update(entity: EmotionCheckEntity)
    suspend fun getLatest(): EmotionCheckEntity?
    suspend fun getInRange(start: Long, end: Long): List<EmotionCheckEntity>
    suspend fun getCount(): Int
    suspend fun deleteAll()
}
