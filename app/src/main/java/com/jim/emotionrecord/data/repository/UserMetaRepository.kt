package com.jim.emotionrecord.data.repository

interface UserMetaRepository {
    suspend fun getFirstRecordedAt(): Long?
    suspend fun setFirstRecordedAt(epochMilli: Long)
    suspend fun deleteAll()
}
