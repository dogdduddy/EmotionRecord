package com.jim.emotionrecord.quest.data.repository

import com.jim.emotionrecord.quest.data.local.dao.EmotionRecordDao
import com.jim.emotionrecord.quest.data.local.entity.EmotionRecordEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmotionRecordRepositoryImpl @Inject constructor(
    private val dao: EmotionRecordDao,
) : EmotionRecordRepository {

    override fun observeAll(): Flow<List<EmotionRecordEntity>> = dao.observeAll()

    override suspend fun insert(entity: EmotionRecordEntity): Long = dao.insert(entity)

    override suspend fun insertAll(entities: List<EmotionRecordEntity>) = dao.insertAll(entities)

    override suspend fun update(entity: EmotionRecordEntity) = dao.update(entity)

    override suspend fun markMissionCompleted(id: Long) = dao.markMissionCompleted(id)

    override suspend fun getInRange(start: Long, end: Long): List<EmotionRecordEntity> =
        dao.getInRange(start, end)

    override suspend fun getLatest(): EmotionRecordEntity? = dao.getLatest()

    override suspend fun getCount(): Int = dao.getCount()

    override suspend fun deleteAll() = dao.deleteAll()
}
