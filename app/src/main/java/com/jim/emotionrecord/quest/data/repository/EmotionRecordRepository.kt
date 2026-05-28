package com.jim.emotionrecord.quest.data.repository

import com.jim.emotionrecord.quest.data.local.entity.EmotionRecordEntity
import kotlinx.coroutines.flow.Flow

interface EmotionRecordRepository {
    fun observeAll(): Flow<List<EmotionRecordEntity>>
    suspend fun insert(entity: EmotionRecordEntity): Long
    suspend fun insertAll(entities: List<EmotionRecordEntity>)
    suspend fun update(entity: EmotionRecordEntity)
    suspend fun markMissionCompleted(id: Long)
    suspend fun getInRange(start: Long, end: Long): List<EmotionRecordEntity>
    suspend fun getLatest(): EmotionRecordEntity?
    suspend fun getCount(): Int
    suspend fun deleteAll()
}
