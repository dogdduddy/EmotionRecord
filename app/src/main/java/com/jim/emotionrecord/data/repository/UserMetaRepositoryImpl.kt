package com.jim.emotionrecord.data.repository

import com.jim.emotionrecord.data.local.dao.UserMetaDao
import com.jim.emotionrecord.data.local.entity.UserMetaEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserMetaRepositoryImpl @Inject constructor(
    private val dao: UserMetaDao
) : UserMetaRepository {

    override suspend fun getFirstRecordedAt(): Long? = dao.get()?.firstRecordedAt

    override suspend fun setFirstRecordedAt(epochMilli: Long) {
        dao.upsert(UserMetaEntity(firstRecordedAt = epochMilli))
    }

    override suspend fun deleteAll() = dao.deleteAll()
}
