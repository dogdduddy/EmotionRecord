package com.jim.emotionrecord.quest.data.repository

import com.jim.emotionrecord.quest.data.local.entity.QuestUserMetaEntity

interface QuestUserMetaRepository {
    suspend fun get(): QuestUserMetaEntity?
    suspend fun upsert(entity: QuestUserMetaEntity)
}
