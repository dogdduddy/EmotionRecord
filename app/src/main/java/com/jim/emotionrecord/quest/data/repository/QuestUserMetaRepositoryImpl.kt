package com.jim.emotionrecord.quest.data.repository

import com.jim.emotionrecord.quest.data.local.dao.QuestUserMetaDao
import com.jim.emotionrecord.quest.data.local.entity.QuestUserMetaEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class QuestUserMetaRepositoryImpl @Inject constructor(
    private val dao: QuestUserMetaDao,
) : QuestUserMetaRepository {

    override suspend fun get(): QuestUserMetaEntity? = dao.get()

    override suspend fun upsert(entity: QuestUserMetaEntity) = dao.upsert(entity)
}
