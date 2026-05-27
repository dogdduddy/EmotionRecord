package com.jim.emotionrecord.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jim.emotionrecord.data.local.entity.EmotionCheckEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionCheckDao {
    @Insert
    suspend fun insert(entity: EmotionCheckEntity): Long

    @Insert
    suspend fun insertAll(entities: List<EmotionCheckEntity>)

    @Update
    suspend fun update(entity: EmotionCheckEntity)

    @Query("SELECT * FROM emotion_check ORDER BY recordedAt DESC")
    fun observeAll(): Flow<List<EmotionCheckEntity>>

    @Query("SELECT * FROM emotion_check ORDER BY recordedAt DESC")
    suspend fun getAll(): List<EmotionCheckEntity>

    @Query("SELECT * FROM emotion_check WHERE recordedAt BETWEEN :start AND :end ORDER BY recordedAt ASC")
    suspend fun getInRange(start: Long, end: Long): List<EmotionCheckEntity>

    @Query("SELECT * FROM emotion_check ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatest(): EmotionCheckEntity?

    @Query("SELECT COUNT(*) FROM emotion_check")
    suspend fun getCount(): Int

    @Query("DELETE FROM emotion_check")
    suspend fun deleteAll()
}
