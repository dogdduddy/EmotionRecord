package com.jim.emotionrecord.data.repository

import com.jim.emotionrecord.data.local.dao.EmotionCheckDao
import com.jim.emotionrecord.data.local.entity.EmotionCheckEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class EmotionRepositoryImpl @Inject constructor(
    private val dao: EmotionCheckDao
) : EmotionRepository {

    override fun observeAll(): Flow<List<EmotionCheckEntity>> = dao.observeAll()

    override suspend fun getAll(): List<EmotionCheckEntity> = dao.getAll()

    override suspend fun insert(entity: EmotionCheckEntity): Long = dao.insert(entity)

    override suspend fun insertAll(entities: List<EmotionCheckEntity>) = dao.insertAll(entities)

    override suspend fun update(entity: EmotionCheckEntity) = dao.update(entity)

    override suspend fun getLatest(): EmotionCheckEntity? = dao.getLatest()

    override suspend fun getInRange(start: Long, end: Long): List<EmotionCheckEntity> =
        dao.getInRange(start, end)

    override suspend fun getCount(): Int = dao.getCount()

    override suspend fun deleteAll() = dao.deleteAll()
}
