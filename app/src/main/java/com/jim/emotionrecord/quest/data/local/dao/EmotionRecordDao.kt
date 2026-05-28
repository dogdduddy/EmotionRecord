package com.jim.emotionrecord.quest.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.jim.emotionrecord.quest.data.local.entity.EmotionRecordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface EmotionRecordDao {

    @Insert
    suspend fun insert(entity: EmotionRecordEntity): Long

    @Insert
    suspend fun insertAll(entities: List<EmotionRecordEntity>)

    @Update
    suspend fun update(entity: EmotionRecordEntity)

    @Query("UPDATE emotion_record SET missionCompleted = 1 WHERE id = :id")
    suspend fun markMissionCompleted(id: Long)

    @Query("SELECT * FROM emotion_record ORDER BY recordedAt DESC")
    fun observeAll(): Flow<List<EmotionRecordEntity>>

    @Query("SELECT * FROM emotion_record WHERE recordedAt BETWEEN :start AND :end ORDER BY recordedAt ASC")
    suspend fun getInRange(start: Long, end: Long): List<EmotionRecordEntity>

    @Query("SELECT * FROM emotion_record ORDER BY recordedAt DESC LIMIT 1")
    suspend fun getLatest(): EmotionRecordEntity?

    @Query("SELECT COUNT(*) FROM emotion_record")
    suspend fun getCount(): Int

    @Query("DELETE FROM emotion_record")
    suspend fun deleteAll()
}
